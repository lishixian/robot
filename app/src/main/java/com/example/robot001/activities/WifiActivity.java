package com.example.robot001.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robot001.R;
import com.example.robot001.adapter.WifiListAdapter;
import com.example.robot001.data.AccessPoint;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WifiActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private NetworkInfo lastNetworkInfo;
    private WifiInfo lastWifiInfo;
    private WifiConfiguration lastWifiConfiguration;
    private List<AccessPoint> lastAccessPoints = new CopyOnWriteArrayList<>();
    private Network currentNetwork;
    private int lastPortalNetworkId = AccessPoint.INVALID_NETWORK_ID;

    private Disposable scanWifi;

    private WifiListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            finish();
            return;
        }
        initView();
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        wifiManager.setWifiEnabled(true);
        lastNetworkInfo = getActiveNetworkInfo();
        lastWifiInfo = wifiManager.getConnectionInfo();
    }

    private void initView() {
        RecyclerView rvList = findViewById(R.id.rvList);
        adapter = new WifiListAdapter();
        adapter.setItemClickListener(this::showDialog);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        scanWifi = Observable.interval(0, 10, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .doOnNext(aLong -> wifiManager.startScan())
                .subscribe();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        filter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, filter);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkRequest.Builder request = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
            cm.registerNetworkCallback(request.build(), callback);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (scanWifi != null && scanWifi.isDisposed()) {
            scanWifi.dispose();
        }
        unregisterReceiver(broadcastReceiver);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            cm.unregisterNetworkCallback(callback);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    case "android.net.wifi.CONFIGURED_NETWORKS_CHANGE":
                    case "android.net.wifi.LINK_CONFIGURATION_CHANGED":
                        updateAccessPoints();
                        break;
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                        updateAccessPoints();
                        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        updateNetworkInfo(info);
                        break;
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                        if (error == WifiManager.ERROR_AUTHENTICATING) {
                            handlePasswordError();
                        }
                        break;
                }
            }
        }
    };

    private final ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            setCurrentNetwork(network);
            portalCurrentWifi();
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (network.equals(getCurrentNetwork())) {
                updateNetworkInfo(null);
            }
        }
    };

    public Network getCurrentNetwork() {
        return currentNetwork;
    }

    public void setCurrentNetwork(Network currentNetwork) {
        this.currentNetwork = currentNetwork;
    }

    private void updateAccessPoints() {
        Single.create((SingleOnSubscribe<List<AccessPoint>>) emitter -> {
                    List<AccessPoint> accessPoints = new ArrayList<>();
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    if (lastWifiInfo != null && lastWifiInfo.getNetworkId() != AccessPoint.INVALID_NETWORK_ID) {
                        lastWifiConfiguration = getWifiConfigurationForNetworkId(lastWifiInfo.getNetworkId());
                    }
                    if (scanResults != null) {
                        for (ScanResult scanResult : scanResults) {
                            if (TextUtils.isEmpty(scanResult.SSID)) {
                                continue;
                            }
                            AccessPoint accessPoint = new AccessPoint(this.getApplicationContext(), scanResult);
                            if (accessPoints.contains(accessPoint)) {
                                continue;
                            }
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
                            if (wifiConfigurations != null) {
                                for (WifiConfiguration config : wifiConfigurations) {
                                    if (accessPoint.getQuotedSSID().equals(config.SSID)) {
                                        accessPoint.setWifiConfiguration(config);
                                    }
                                }
                            }
                            if (lastWifiInfo != null && lastNetworkInfo != null) {
                                accessPoint.update(lastWifiConfiguration, lastWifiInfo, lastNetworkInfo);
                            }
                            accessPoints.add(accessPoint);
                        }
                    }
                    Collections.sort(accessPoints);
                    emitter.onSuccess(accessPoints);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<AccessPoint>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<AccessPoint> accessPoints) {
                        lastAccessPoints = accessPoints;
                        adapter.setAccessPoints(lastAccessPoints);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void updateNetworkInfo(NetworkInfo networkInfo) {
        Single.create((SingleOnSubscribe<List<AccessPoint>>) emitter -> {
                    if (networkInfo != null) {
                        lastNetworkInfo = networkInfo;
                    }
                    lastWifiInfo = wifiManager.getConnectionInfo();
                    if (lastWifiInfo.getNetworkId() == AccessPoint.INVALID_NETWORK_ID) {
                        // ???????????? wifi ?????????lastPortalNetworkId ????????????
                        lastPortalNetworkId = AccessPoint.INVALID_NETWORK_ID;
                    }
                    if (lastWifiInfo != null && lastWifiInfo.getNetworkId() != AccessPoint.INVALID_NETWORK_ID) {
                        lastWifiConfiguration = getWifiConfigurationForNetworkId(lastWifiInfo.getNetworkId());
                    }
                    boolean reorder = false;
                    for (AccessPoint accessPoint : lastAccessPoints) {
                        if (accessPoint.update(lastWifiConfiguration, lastWifiInfo, lastNetworkInfo)) {
                            reorder = true;
                        }
                    }
                    if (reorder) {
                        Collections.sort(lastAccessPoints);
                    }
                    emitter.onSuccess(lastAccessPoints);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<AccessPoint>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<AccessPoint> accessPoints) {
                        // ????????????
                        adapter.setAccessPoints(accessPoints);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });

    }

    public void showDialog(AccessPoint accessPoint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(accessPoint.ssid);
        if (accessPoint.isSaved()) {
            builder.setPositiveButton("????????????", (dialog, which) -> forgetWifi(accessPoint))
                    .setNegativeButton("??????", null)
                    .show();

        } else {
            if (!accessPoint.isSaved() && accessPoint.isSecured) {
                EditText editText = new EditText(this);
                builder.setView(editText)
                        .setPositiveButton("??????", (dialog, which) -> {
                            if (editText.getText() != null) {
                                String password = editText.getText().toString();
                                if (!TextUtils.isEmpty(password)) {
                                    accessPoint.setPassword(password);
                                    connect(accessPoint);
                                }
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
            } else {
                connect(accessPoint);
            }
        }
    }

    private void connect(AccessPoint accessPoint) {
        accessPoint.generateNetworkConfig();
        int networkId = wifiManager.addNetwork(accessPoint.wifiConfiguration);
        wifiManager.enableNetwork(networkId, true);
    }

    public void forgetWifi(AccessPoint accessPoint) {
        boolean result = wifiManager.removeNetwork(accessPoint.wifiConfiguration.networkId);
        Toast.makeText(this, result ? "??????????????????" : "??????????????????", Toast.LENGTH_LONG).show();
    }

    public void handlePasswordError() {
        if (lastWifiConfiguration != null) {
            AccessPoint accessPoint = new AccessPoint(lastWifiConfiguration);
            accessPoint.setPasswordError(true);
        }

    }

    public void portalCurrentWifi() {
        if (lastWifiInfo.getNetworkId() != lastPortalNetworkId) {
            lastPortalNetworkId = lastWifiInfo.getNetworkId();
            Single.create((SingleOnSubscribe<Boolean>) emitter -> {
                        Network currentNetwork = getCurrentNetwork();
                        HttpURLConnection urlConnection = null;
                        try {
                            // ?????????????????????????????????
                            urlConnection = (HttpURLConnection) currentNetwork.openConnection(new URL("http://connect.rom.miui.com/generate_204"));
                            urlConnection.setInstanceFollowRedirects(false);
                            urlConnection.setConnectTimeout(10000);
                            urlConnection.setReadTimeout(10000);
                            urlConnection.setUseCaches(false);
                            urlConnection.getInputStream();
                            int responseCode = urlConnection.getResponseCode();
                            if (responseCode == 200 && urlConnection.getContentLength() == 0) {
                                responseCode = 204;
                            }
                            emitter.onSuccess(responseCode != 204 && responseCode >= 200 && responseCode <= 399);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null) {
                                urlConnection.disconnect();
                            }
                        }
                    })
                    .retry(throwable -> throwable instanceof UnknownHostException)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            if (aBoolean) {
                                // ????????????????????????
                                new AlertDialog.Builder(WifiActivity.this)
                                        .setTitle("????????????")
                                        .setPositiveButton("??????", null)
                                        .setNegativeButton("??????", null)
                                        .show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    /**
     * ?????? NetworkId ?????? WifiConfiguration ??????
     *
     * @param networkId ???????????? WifiConfiguration ????????? networkId
     * @return ?????? networkId ??? WifiConfiguration ??????
     */
    private WifiConfiguration getWifiConfigurationForNetworkId(int networkId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        final List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (lastWifiInfo != null && networkId == config.networkId) {
                    return config;
                }
            }
        }
        return null;
    }

    /**
     * ????????????????????????
     */
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

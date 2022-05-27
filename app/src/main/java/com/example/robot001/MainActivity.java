package com.example.robot001;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.robot001.activities.WifiActivity;
import com.example.robot001.adapter.ActiveAdapter;
import com.example.robot001.data.Active;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final List<Active> mActiveList = new ArrayList<>();

    private void initList(){
        mActiveList.add(new Active("WifiActivity", WifiActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initList();
        ActiveAdapter adapter = new ActiveAdapter(mActiveList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        adapter.setOnItemClickListener(position -> {
            Active active = mActiveList.get(position);
            Intent intent = new Intent(this,active.getActionClass());
            startActivity(intent);
        });

        adapter.setOnItemLongClickListener(position -> Toast.makeText(MainActivity.this, "long click " + position, Toast.LENGTH_SHORT).show());

    }

}

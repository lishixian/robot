package com.example.robot001.utils;

import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class DataConverter {
    private final static String TAG = DataConverter.class.getSimpleName();
    public static String bytes2Hex(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr != null) {
            for (byte b : bArr) {
                String hexString = Integer.toHexString(b & 255);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String bytes2HexWithSeparate(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr != null) {
            for (byte b : bArr) {
                String hexString = Integer.toHexString(b & 255);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
                sb.append(" ");
            }
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String bytes2Str(byte[] bArr) {
        String str = "";
        int length = bArr.length;
        for (int i = 0; i < bArr.length; i++) {
            str = str + ((char) bArr[i]);
        }
        return str;
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream, int i) throws IOException {
        if (inputStream != null && outputStream != null) {
            byte[] bArr = new byte[i];
            while (true) {
                int read = inputStream.read(bArr);
                if (read >= 0) {
                    outputStream.write(bArr, 0, read);
                } else {
                    return;
                }
            }
        }
    }

    public static boolean equals(Object obj, Object obj2) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Objects.equals(obj, obj2);
        }
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static byte[] hex2BigBytes(String str) {
        int length = str.length();
        int i = length / 2;
        byte[] bArr = new byte[i];
        String upperCase = str.toUpperCase();
        char[] charArray = upperCase.toCharArray();
        if (length % 2 == 1) {
            return null;
        }
        for (int i2 = 0; i2 < length; i2++) {
            if ((charArray[i2] < '0' || charArray[i2] > '9') && (charArray[i2] < 'A' || charArray[i2] > 'F')) {
                return null;
            }
        }
        for (int i3 = 0; i3 < length; i3 += 2) {
            bArr[(i - (i3 / 2)) - 1] = (byte) ((Character.digit(upperCase.charAt(i3), 16) << 4) + Character.digit(upperCase.charAt(i3 + 1), 16));
        }
        return bArr;
    }

    public static byte[] hex2Bytes(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            int length = str.length();
            byte[] bArr = new byte[(length / 2)];
            String upperCase = str.toUpperCase();
            char[] charArray = upperCase.toCharArray();
            if (length % 2 == 1) {
                return null;
            }
            for (int i = 0; i < length; i++) {
                if ((charArray[i] < '0' || charArray[i] > '9') && (charArray[i] < 'A' || charArray[i] > 'F')) {
                    return null;
                }
            }
            for (int i2 = 0; i2 < length; i2 += 2) {
                bArr[i2 / 2] = (byte) ((Character.digit(upperCase.charAt(i2), 16) << 4) + Character.digit(upperCase.charAt(i2 + 1), 16));
            }
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hex2Str(String str) {
        String upperCase = str.toUpperCase();
        char[] charArray = upperCase.toCharArray();
        int length = upperCase.length() / 2;
        byte[] bArr = new byte[length];
        if (upperCase.length() % 2 == 1) {
            return "";
        }
        for (int i = 0; i < upperCase.length(); i++) {
            if ((charArray[i] < '0' || charArray[i] > '9') && (charArray[i] < 'A' || charArray[i] > 'F')) {
                return "";
            }
        }
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i2 * 2;
            bArr[i2] = (byte) (("0123456789ABCDEF".indexOf(charArray[i3 + 1]) + ("0123456789ABCDEF".indexOf(charArray[i3]) * 16)) & 255);
        }
        return new String(bArr);
    }

    public static int littleEndianByteArrayToInt(byte[] bArr) {
        int i = 0;
        int length = bArr.length;
        if (length != 0) {
            for (int i2 = length - 1; i2 >= 0; i2--) {
                i += unsignedByteToInt(bArr[i2]) << (i2 * 8);
            }
        }
        return i;
    }

    public static byte[] reverse(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        int i = 0;
        for (int length = bArr.length - 1; length >= 0; length--) {
            bArr2[i] = bArr[length];
            i++;
        }
        return bArr2;
    }

    public static void safeCloseStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Throwable th) {
                LogUtil.d(TAG,"Error closing stream: " + th.toString());
            }
        }
    }

    public static byte[] str2Bytes(String str) {
        if (str != null) {
            byte[] bArr = new byte[(str.length() / 2)];
            try {
                return str.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return bArr;
            }
        } else {
            throw new IllegalArgumentException("Argument str ( String ) is null! ");
        }
    }

    public static String str2Hex(String str) {
        char[] charArray = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bytes = str.getBytes();
        for (byte b : bytes) {
            sb.append(charArray[(b & 240) >> 4]);
            sb.append(charArray[b & 15]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static String str2Unicode(String str) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            String hexString = Integer.toHexString(charAt);
            if (charAt > 128) {
                sb.append("\\u" + hexString);
            } else {
                sb.append("\\u00" + hexString);
            }
        }
        return sb.toString();
    }

    public static String unicode2Str(String str) {
        int length = str.length() / 6;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < length) {
            int i2 = i * 6;
            i++;
            String substring = str.substring(i2, i * 6);
            String str2 = substring.substring(2, 4) + "00";
            sb.append(new String(Character.toChars(Integer.valueOf(substring.substring(4), 16).intValue() + Integer.valueOf(str2, 16).intValue())));
        }
        return sb.toString();
    }

    public static int unsignedByteToInt(byte b) {
        return b & 255;
    }

    public static String ellipsize(String str) {
        if (str == null) {
            return null;
        }
        return str.length() >= 3 ? str.charAt(0) + "???" + str.charAt(str.length() - 1) : str;
    }

    public static void safeCloseStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Throwable th) {
                LogUtil.d(TAG,"Error closing stream: " + th.toString());
            }
        }
    }
}

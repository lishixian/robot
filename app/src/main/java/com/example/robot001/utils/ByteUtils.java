package com.example.robot001.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteUtils {

    private static final String TAG = ByteUtils.class.getSimpleName();
    /**
     * 基本数据互转工具
     *
     * @author liujinliang
     */

    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    /**
     * int转byte
     *
     * @param x
     * @return
     */
    public static byte intToByte(int x) {
        return (byte) x;
    }

    /**
     * byte转int
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        //Java的byte是有符号，通过 &0xFF转为无符号
        return b & 0xFF;
    }

    /**
     * byte[]转int
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
            (b[2] & 0xFF) << 8 |
            (b[1] & 0xFF) << 16 |
            (b[0] & 0xFF) << 24;
    }

    public static int byteArrayToInt(byte[] b, int index) {
        return b[index + 3] & 0xFF |
            (b[index + 2] & 0xFF) << 8 |
            (b[index + 1] & 0xFF) << 16 |
            (b[index + 0] & 0xFF) << 24;
    }

    /**
     * int转byte[]
     *
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a) {
        return new byte[] {
            (byte) ((a >> 24) & 0xFF),
            (byte) ((a >> 16) & 0xFF),
            (byte) ((a >> 8) & 0xFF),
            (byte) (a & 0xFF)
        };
    }

    /**
     * short转byte[]
     *
     * @param b
     * @param s
     * @param index
     */
    public static void byteArrToShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }

    /**
     * byte[]转short
     *
     * @param b
     * @param index
     * @return
     */
    public static short byteArrToShort(byte[] b, int index) {
        return (short) (((b[index + 0] << 8) | b[index + 1] & 0xff));
    }

    /**
     * 16位short转byte[]
     *
     * @param s short
     * @return byte[]
     */
    public static byte[] shortToByteArr(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * byte[]转16位short
     *
     * @param b
     * @return
     */
    public static short byteArrToShort(byte[] b) {
        return byteArrToShort(b, 0);
    }

    /**
     * long转byte[]
     *
     * @param x
     * @return
     */
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * byte[]转Long
     *
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    /**
     * 从byte[]中抽取新的byte[]
     *
     * @param data  - 元数据
     * @param start - 开始位置
     * @param end   - 结束位置
     * @return 新byte[]
     */
    public static byte[] getByteArr(byte[] data, int start, int end) {
        byte[] ret = new byte[end - start];
        for (int i = 0; (start + i) < end; i++) {
            ret[i] = data[start + i];
        }
        return ret;
    }

    /**
     * 流转换为byte[]
     *
     * @param inStream
     * @return
     */
    public static byte[] readInputStream(InputStream inStream) {
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            byte[] data = null;
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();
            return data;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                return null;
            }
        }
    }

    /**
     * byte[]转inputstream
     *
     * @param b
     * @return
     */
    public static InputStream readByteArr(byte[] b) {
        return new ByteArrayInputStream(b);
    }

    /**
     * byte数组内数字是否相同
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean isEq(byte[] s1, byte[] s2) {
        int slen = s1.length;
        if (slen == s2.length) {
            for (int index = 0; index < slen; index++) {
                if (s1[index] != s2[index]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * byte数组转换为Stirng
     *
     * @param s1     -数组
     * @param encode -字符集
     * @param err    -转换错误时返回该文字
     * @return
     */
    public static String getString(byte[] s1, String encode, String err) {
        try {
            return new String(s1, encode);
        } catch (UnsupportedEncodingException e) {
            return err == null ? null : err;
        }
    }

    /**
     * byte数组转换为Stirng
     *
     * @param s1-数组
     * @param encode-字符集
     * @return
     */
    public static String getString(byte[] s1, String encode) {
        return getString(s1, encode, null);
    }

    //测试
    public static void main(String[] args) {
        System.err.println(isEq(new byte[] {1, 2}, new byte[] {1, 2}));
    }

    /**
     * 字节数组转16进制字符串
     *
     * @param b
     * @return
     */
    public static String byteArrToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    /**
     * 16进制字符创转int
     *
     * @param hexString
     * @return
     */
    public static int hexStringToInt(String hexString) {
        int value = 0;
        try {
            value = Integer.parseInt(hexString, 16);
        } catch (NumberFormatException e) {
            LogUtil.d(TAG, "hexStringToInt: error" + e);
        }
        return value;
    }

    /**
     * 十进制转二进制
     *
     * @param i
     * @return
     */
    public static String intToBinary(int i) {
        return Integer.toBinaryString(i);
    }

    /**
     * double转byte
     *
     * @param d
     * @return
     */
    public static byte[] doubleToBytes(double d) {
        Long value = Double.doubleToRawLongBits(d);
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    /**
     * byte转double
     *
     * @param b
     * @return
     */
    public static double bytesTodouble(byte[] b) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (b[i] & 0xff)) << (8 * i);
        }
        // 返回对应于给定位表示形式的 double 值。
        return Double.longBitsToDouble(value);
    }

    /**
     * utc时间戳转 4 字节byte []
     *
     * @param time 单位毫秒
     *             946656000 2000/01/01 时间戳
     * @return
     */
    public static byte[] utcToByte(long time) {
        time = time / 1000 - 946656000;
        LogUtil.d("time", "utcToByte: " + time);
        byte[] timeBytes = new byte[4];
        timeBytes[0] = (byte) (time >> 24 & 0xff);
        timeBytes[1] = (byte) (time >> 16 & 0xff);
        timeBytes[2] = (byte) (time >> 8 & 0xff);
        timeBytes[3] = (byte) (time & 0xff);
        return timeBytes;
    }

    /**
     * 将两个byte数组 组装成一个新的字节数组
     *
     * @param bt1
     * @param bt2
     * @return
     */
    public static byte[] byteMerge(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    //将byte数组置空
    public static byte[] resetArray(byte[] a){
        byte[] b2 = new byte[a.length];
        for(int i=0;i<a.length;i++)
        {
            a[i] = b2[i];
        }
        return a;
    }
}

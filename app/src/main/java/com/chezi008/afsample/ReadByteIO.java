package com.chezi008.afsample;

import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import kotlin.jvm.Synchronized;
import tv.danmaku.ijk.media.player.misc.IAndroidIO;

public class ReadByteIO implements IAndroidIO {
    public static final String URL_SUFFIX = "recv_data_online";
    private static final String TAG = "ReadByteIO";

    // 内存队列，用于缓存获取到的流数据，要实现追帧效果，只需要根据策略丢弃本地缓存的内容即可
    private LinkedBlockingDeque<byte[]> flvData = new LinkedBlockingDeque<>(50);


    private ReadByteIO() {

    }

    public static ReadByteIO get() {
        return ReadByteIOHolder.instance;
    }

    private static class ReadByteIOHolder {
        private final static ReadByteIO instance = new ReadByteIO();
    }

    @Override
    public int open(String url) throws IOException {
        Log.d(TAG, "open: ");
        if (url.equals(URL_SUFFIX)) {
            return 1; // 打开播放流成功
        }
        return -1; // 打开播放流失败
    }

    @Override
    public int read(byte[] buffer, int size) throws IOException {
        Log.d(TAG, "read buffer: " + size + ",flvData capacity：" + flvData.size());
        byte[] tmpBytes = new byte[0]; // 阻塞式读取，没有数据不渲染画面
        try {
            tmpBytes = flvData.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tmpBytes.length > size) {
            System.arraycopy(tmpBytes, 0, buffer, 0, size);
            byte[] left = new byte[tmpBytes.length - size];
            System.arraycopy(tmpBytes, size, left, 0, left.length);
            flvData.addFirst(left);
            Log.w(TAG, "read: tmpBytes.length>size 数据拆分" + tmpBytes.length);
            return size;
        }
        System.arraycopy(tmpBytes, 0, buffer, 0, tmpBytes.length);
        return tmpBytes.length;
    }

    @Override
    public long seek(long offset, int whence) throws IOException {
        return 0;
    }

    @Override
    public int close() throws IOException {
        return 0;
    }


    public boolean addLast(byte[] bytes) { // 新收到的数据通过该接口，添加到缓存队列的队尾
//        Log.e(TAG, "addLast tmpList size " + bytes.length);
        flvData.add(bytes);
        return true;
    }

}

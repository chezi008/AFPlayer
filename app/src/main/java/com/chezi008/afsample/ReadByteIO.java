package com.chezi008.afsample;

import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;

import kotlin.jvm.Synchronized;
import tv.danmaku.ijk.media.player.misc.IAndroidIO;

public class ReadByteIO implements IAndroidIO {
    public static final String URL_SUFFIX = "recv_data_online";
    private static final String TAG = "ReadByteIO";

    // 内存队列，用于缓存获取到的流数据，要实现追帧效果，只需要根据策略丢弃本地缓存的内容即可
    private LinkedBlockingDeque<Byte> flvData  = new LinkedBlockingDeque<>();


    private ReadByteIO(){

    }

    public static ReadByteIO get() {
        return ReadByteIOHolder.instance;
    }

    private static class ReadByteIOHolder{
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
        Log.d(TAG, "read buffer: "+size);
        byte[] tmpBytes = takeFirstWithLen(size); // 阻塞式读取，没有数据不渲染画面
        System.arraycopy(tmpBytes, 0, buffer, 0, size);
        return size;
    }

    @Override
    public long seek(long offset, int whence) throws IOException {
        return 99565222;
    }

    @Override
    public int close() throws IOException {
        return 0;
    }


    /**
     * 取 byte 数据用于界面渲染
     * @param len
     * @return
     */
    private byte[] takeFirstWithLen(int len) {
        byte[] byteList = new byte[len];
        for (int i = 0; i < byteList.length; i++) {
            try {
                byteList[i] = flvData.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return byteList;
    }

    public boolean addLast(byte[] bytes) { // 新收到的数据通过该接口，添加到缓存队列的队尾
//        Log.e(TAG, "addLast tmpList size " + bytes.length);
        for (byte aByte : bytes) {
            flvData.add(aByte);
        }
        return true;
    }

}

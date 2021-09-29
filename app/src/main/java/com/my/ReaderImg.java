package com.my;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReaderImg {


    public static float byteArray2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    public static int byteArray2int(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    float[] readImg(Context context,String  filePath, int ind) throws IOException {
        try {
            //InputStream is = context.getResources().getAssets().open(filePath);
            InputStream is = new FileInputStream(filePath);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();

            int base_offset = ind * 28*28*4;

            float[] floatBuf  = new float[28*28];
            for(int i=0;i< 28*28;i++)
            {
                floatBuf[i] = byteArray2float(bytes, base_offset +i * 4 );
            }

            return floatBuf;
        }catch(Exception ex)
        {
            Log.e("ReadImg", ex.toString(),ex);
            throw ex;
        }
    }


    float[] readAllImg(Context context,String  filePath,int batchDataNum) throws IOException {
        try {
            //InputStream is = context.getResources().getAssets().open(filePath);
            InputStream is = new FileInputStream(filePath);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();


            float[] floatBuf  = new float[28*28*batchDataNum];
            for(int i=0;i< 28*28*batchDataNum ;i++)
            {
                floatBuf[i] = byteArray2float(bytes,  i * 4 );
            }

            return floatBuf;
        }catch(Exception ex)
        {
            Log.e("ReadImg", ex.toString(),ex);
            throw ex;
        }
    }

    int[] readAllImg(Context context,String  filePath,int batchDataNum,int widthHeight) throws IOException {
        try {
            //InputStream is = context.getResources().getAssets().open(filePath);
            InputStream is = new FileInputStream(filePath);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();


            int count = widthHeight*widthHeight*batchDataNum;
            int[] intBuf  = new int[count];
            for(int i=0;i< count ;i++)
            {
                intBuf[i] = byteArray2int(bytes,  i * 4 );
            }

            return intBuf;
        }catch(Exception ex)
        {
            Log.e("ReadImg", ex.toString(),ex);
            throw ex;
        }
    }
}

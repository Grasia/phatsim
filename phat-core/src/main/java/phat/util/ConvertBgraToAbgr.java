/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package phat.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

public class ConvertBgraToAbgr extends RecursiveAction {

    private int mStart;
    private int mLength;
    private byte[] outArray;
    private byte[] inArray;
    int width;
    int height;

    public ConvertBgraToAbgr(byte[] src, int start, int length, byte[] dst, int width, int height) {
        this.inArray = src;
        this.mStart = start;
        this.mLength = length;
        this.outArray = dst;
        this.width = width;
        this.height = height;
    }

    protected void computeDirectly() {
        int yAux;
        for (int y = mStart; y < mStart + mLength; y++) {
            yAux = y * width;
            for (int x = 0; x < width; x++) {
                int inPtr = (yAux + x) * 4;
                int outPtr = ((height - y - 1) * width + x) * 4;

                outArray[outPtr + 1] = inArray[inPtr + 0]; //a
                outArray[outPtr + 2] = inArray[inPtr + 1]; //b
                outArray[outPtr + 3] = inArray[inPtr + 2]; //g
                outArray[outPtr + 0] = inArray[inPtr + 3]; //r
            }
        }
    }
    protected static int sThreshold = 200;

    @Override
    protected void compute() {
        if (mLength < sThreshold) {
            computeDirectly();
            return;
        }

        int split = mLength / 2;

        invokeAll(new ConvertBgraToAbgr(inArray, mStart, split, outArray, width, height),
                new ConvertBgraToAbgr(inArray, mStart + split, mLength - split,
                outArray, width, height));
    }

    public static Map<Integer, byte[]> buffers = new HashMap<Integer, byte[]>();
    
    private static byte[] getBuffer(int length) {
        byte[] result = buffers.get(length);
        if(result == null) {
            result = new byte[length];
            buffers.put(length, result);
        }
        return result;
    }
    
    public static void convert(ByteBuffer bgraBuf, BufferedImage out) {
        WritableRaster wr = out.getRaster();
        DataBufferByte db = (DataBufferByte) wr.getDataBuffer();

        byte[] dst = db.getData();
        byte[] src = getBuffer(dst.length);
        bgraBuf.clear();
        bgraBuf.get(src);
        bgraBuf.clear();

        int width = wr.getWidth();
        int height = wr.getHeight();

        //int processors = Runtime.getRuntime().availableProcessors();

        ConvertBgraToAbgr fb = new ConvertBgraToAbgr(src, 0, height, dst, width, height);

        //ForkJoinPool pool = new ForkJoinPool();

        //long startTime = System.currentTimeMillis();
        //pool.invoke(fb);
        fb.computeDirectly();
        //long endTime = System.currentTimeMillis();
    }
}
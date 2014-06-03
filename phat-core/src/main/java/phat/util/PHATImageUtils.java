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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 *
 * @author pablo
 */
public class PHATImageUtils {

    // TODO Optimizar la conversion!!
    public static byte[] bufferedImageToJPGByteArray(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
            //baos.flush();
            byte[] imageInByte = baos.toByteArray();
            //baos.close();
            return imageInByte;
        } catch (IOException ex) {
            Logger.getLogger(PHATImageUtils.class.getName()).log(Level.SEVERE, "bufferedImageToByteArray", ex);
        }
        return null;
    }

    public static void printImageFormatNames() {
        System.out.println("Informa Names:");
        for (String s : ImageIO.getReaderFormatNames()) {
            System.out.println(s);
        }
    }

    // TODO Optimizar la conversion!!
    public static byte[] bufferedImageToBMPByteArray(BufferedImage image, BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            /* ImageTypeSpecifier type =
             ImageTypeSpecifier.createFromRenderedImage(image);
             Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, "bmp");
             System.err.println(ImageIO.getWriterFormatNames());
             boolean sig=iter.hasNext();*/
            //BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D ngraphics = (Graphics2D) bufferedImage.getGraphics();
            /*ngraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
             RenderingHints.VALUE_INTERPOLATION_BILINEAR);*/
            //ngraphics.drawImage(image, 0, 0,512,384,0,0,image.getWidth(),image.getHeight(), null);        	
            //ngraphics.drawImage(image.getScaledInstance(200, 300, BufferedImage.SCALE_FAST), 0, 0,null);        	
            ngraphics.drawImage(image, 0, 0, null);
            ImageIO.write(bufferedImage, "bmp", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ex) {
            Logger.getLogger(PHATImageUtils.class.getName()).log(Level.SEVERE, "bufferedImageToByteArray", ex);
        }
        return null;
    }

    public static void convertABGRToRGB(BufferedImage image, int[] buffer) {
        // 0 -> A
        // 1 -> B
        // 2 -> G
        // 3 -> R
        if (image.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            return;
        }
        WritableRaster wr = image.getRaster();
        DataBufferByte db = (DataBufferByte) wr.getDataBuffer();

        byte[] inArray = db.getData();

        int offset = 0;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (inArray[1 + offset] & 0xFF) | ((inArray[2 + offset] & 0xFF) << 8)
                    | ((inArray[3 + offset] & 0xFF) << 16) | ((inArray[0 + offset] & 0xFF) << 24);
            offset += 4;
        }
        return;
    }

    public static byte[] convertABGRToRGB565(BufferedImage image, BufferedImage bufferedImage) {
        if (image.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            return null;
        }
        Graphics2D ngraphics = (Graphics2D) bufferedImage.getGraphics();
        ngraphics.drawImage(image, 0, 0, null);

        WritableRaster wr = image.getRaster();
        DataBufferByte db = (DataBufferByte) wr.getDataBuffer();

        return db.getData();
        /*WritableRaster wr = image.getRaster();
         DataBufferByte db = (DataBufferByte) wr.getDataBuffer();
        
         byte[] inArray = db.getData();
        
         int offset = 0;        
         for(int i = 0; i < buffer.length; i+=2) {
         //buffer[i] = (inArray[1 + offset] & 0xFF) | ((inArray[2 + offset] & 0xFF) << 8)
         //        | ((inArray[3 + offset] & 0xFF) << 16) | ((inArray[0 + offset] & 0xFF) << 24);
         buffer[i] = (byte) ((inArray[1 + offset] & 0xF8) | (inArray[2 + offset] >> 5)); // R
         buffer[i] = (byte) (((inArray[2 + offset] & 0x1C) << 3) | (inArray[2 + offset] >> 3)); // G
         buffer[i+1] = inArray[2 + offset];
         buffer[i+2] = inArray[3 + offset];
         buffer[i+3] = inArray[0 + offset];
         offset+=4;
         }*/
    }

    public static byte[] bufferedImageToFormat(BufferedImage image, String formatName) {
        //	return ((DataBufferByte) image.getData().getDataBuffer()).getData();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.setUseCache(false);
            ImageIO.write(image, formatName, baos);

            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(PHATImageUtils.class.getName()).log(Level.SEVERE, "bufferedImageToByteArray", ex);
        }
        return null;
    }

    public static byte[] bufferedImageToFormat(BufferedImage image, String formatName, ByteArrayOutputStream baos) {
        //	return ((DataBufferByte) image.getData().getDataBuffer()).getData();
        try {
            baos.reset();
            ImageIO.setUseCache(false);
            ImageIO.write(image, formatName, baos);

            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(PHATImageUtils.class.getName()).log(Level.SEVERE, "bufferedImageToByteArray", ex);
        }
        return null;
    }

    public static void getScreenShotBGRA(ByteBuffer bgraBuf, BufferedImage out) {
        WritableRaster wr = out.getRaster();
        DataBufferByte db = (DataBufferByte) wr.getDataBuffer();

        byte[] cpuArray = db.getData();

        // copy native memory to java memory
        bgraBuf.clear();
        bgraBuf.get(cpuArray);
        bgraBuf.clear();
    }

    /**
     * Good format for java swing.
     *
     * @param bgraBuf
     * @param out
     */
    public static void getScreenShotABGR(ByteBuffer bgraBuf, BufferedImage out) {
        WritableRaster wr = out.getRaster();
        DataBufferByte db = (DataBufferByte) wr.getDataBuffer();

        byte[] cpuArray = db.getData();

        // copy native memory to java memory
        bgraBuf.clear();
        bgraBuf.get(cpuArray);
        bgraBuf.clear();

        int width = wr.getWidth();
        int height = wr.getHeight();

        // flip the components the way AWT likes them
        for (int y = 0; y < height / 2; y++) {
            for (int x = 0; x < width; x++) {
                int inPtr = (y * width + x) * 4;
                int outPtr = ((height - y - 1) * width + x) * 4;

                byte b1 = cpuArray[inPtr + 0];
                byte g1 = cpuArray[inPtr + 1];
                byte r1 = cpuArray[inPtr + 2];
                byte a1 = cpuArray[inPtr + 3];

                byte b2 = cpuArray[outPtr + 0];
                byte g2 = cpuArray[outPtr + 1];
                byte r2 = cpuArray[outPtr + 2];
                byte a2 = cpuArray[outPtr + 3];

                cpuArray[outPtr + 0] = a1;
                cpuArray[outPtr + 1] = b1;
                cpuArray[outPtr + 2] = g1;
                cpuArray[outPtr + 3] = r1;

                cpuArray[inPtr + 0] = a2;
                cpuArray[inPtr + 1] = b2;
                cpuArray[inPtr + 2] = g2;
                cpuArray[inPtr + 3] = r2;
            }
        }
    }

    public static byte[] convertABGRToYUV220SP(BufferedImage image) {
        byte[] oneFrame = new byte[(int) (image.getWidth() * image.getHeight() * 1.5)];
        WritableRaster wr = image.getRaster();
        DataBufferByte db = (DataBufferByte) wr.getDataBuffer();

        byte[] inArray = db.getData();

        int width = wr.getWidth();
        int height = wr.getHeight();

        int i = 0;
        int numpixels = width * height;
        int R, G, B, Y, U, V;
        int ui = numpixels;
        int vi = numpixels + numpixels / 4;
        int color;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int inPtr = (y * width + x) * 4;
                color = image.getRGB(x, y);
                R = inArray[inPtr + 3];
                G = inArray[inPtr + 2];
                B = inArray[inPtr + 1];

                Y = (int) ((0.257 * R) + (0.504 * G) + (0.098 * B) + 16);
                oneFrame[i] = (byte) Y;
                if (0 == y % 2 && 0 == x % 2) {
                    oneFrame[vi++] = (byte) ((0.439 * R) - (0.368 * G) - (0.071 * B) + 128);
                    oneFrame[ui++] = (byte) (-(0.148 * R) - (0.291 * G) + (0.439 * B) + 128);
                }
                i++;
            }
        }
        return oneFrame;
    }

    public static void main(String[] args) throws IOException {

        Set<String> set = new HashSet<String>();

        // Get list of all informal format names understood by the current set of registered readers
        String[] formatNames = ImageIO.getReaderFormatNames();

        for (int i = 0; i < formatNames.length; i++) {
            set.add(formatNames[i].toLowerCase());
        }
        System.out.println("Supported read formats: " + set);

        set.clear();

        // Get list of all informal format names understood by the current set of registered writers
        formatNames = ImageIO.getWriterFormatNames();

        for (int i = 0; i < formatNames.length; i++) {
            set.add(formatNames[i].toLowerCase());
        }
        System.out.println("Supported write formats: " + set);

        set.clear();

        // Get list of all MIME types understood by the current set of registered readers
        formatNames = ImageIO.getReaderMIMETypes();

        for (int i = 0; i < formatNames.length; i++) {
            set.add(formatNames[i].toLowerCase());
        }
        System.out.println("Supported read MIME types: " + set);

        set.clear();

        // Get list of all MIME types understood by the current set of registered writers
        formatNames = ImageIO.getWriterMIMETypes();

        for (int i = 0; i < formatNames.length; i++) {
            set.add(formatNames[i].toLowerCase());
        }
        System.out.println("Supported write MIME types: " + set);

    }
}

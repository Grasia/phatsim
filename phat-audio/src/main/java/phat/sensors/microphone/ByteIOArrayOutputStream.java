/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.sensors.microphone;

import java.io.ByteArrayOutputStream;

/**
 *
 * @author Pablo
 */
public class ByteIOArrayOutputStream extends ByteArrayOutputStream {

        ByteIOArrayOutputStream(int size) {
            super(size);
        }

        public byte[] getBuf() {
            return buf;
        }
    }

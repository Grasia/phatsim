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
package phat.client.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import phat.client.DOSObjectClientWriter;
import phat.client.PHATClientConnection;
import phat.mobile.servicemanager.services.Service;
import sim.android.media.service.AudioStreamDataPacket;

/**
 *
 * @author pablo
 */
public class SpeakerTest {

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, InterruptedException {
        //androidPlayShortAudioFileViaAudioTrack();
        //javaPlayShortAudioFileViaAudioTrack();
        rawPlayShortAudioFileViaAudioTrack();
    }

    public static void javaPlayShortAudioFileViaAudioTrack() throws UnsupportedAudioFileException, IOException, InterruptedException {
        PHATClientConnection phatClientConnection = new PHATClientConnection("192.168.0.108", 65056, "Smartphone1", Service.SPEAKER);
        DOSObjectClientWriter dOSObjectClientWriter = new DOSObjectClientWriter(phatClientConnection);
        dOSObjectClientWriter.connect();

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("/home/pablo/tts_file.wav"));

        byte[] audioData;
        int read;
        int total = 0;
        while ((read = audioInputStream.read(audioData = new byte[1486])) > 0) {
            System.out.println("Writing = " + read);
            total += read;
            AudioStreamDataPacket asdp = new AudioStreamDataPacket(audioData, read / 2, read);
            dOSObjectClientWriter.write(asdp);
        }

        System.out.println("total = "+total);
        Thread.sleep(1000);
        dOSObjectClientWriter.close();
    }
    
    public static void androidPlayShortAudioFileViaAudioTrack() throws IOException, InterruptedException {
        PHATClientConnection phatClientConnection = new PHATClientConnection("192.168.0.108", 65056, "Smartphone1", Service.SPEAKER);
        DOSObjectClientWriter dOSObjectClientWriter = new DOSObjectClientWriter(phatClientConnection);

        dOSObjectClientWriter.connect();

        int intSize = 1486;

//Reading the file..
        byte[] byteData = null;
        File file = null;
        file = new File("/home/pablo/tts_file.wav");

        
        FileInputStream in = null;
        try {
            in = new FileInputStream("/home/pablo/tts_file.wav");

        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        int bytesread = 0, ret = 0;

        int total = 0;
        // Read data
        while ((ret = in.read(byteData = new byte[intSize], 0, intSize)) > 0) {
            total += ret;
            System.out.println("read = "+ret);
            if (ret != -1) {
                AudioStreamDataPacket asdp = new AudioStreamDataPacket(byteData, ret / 2, ret);
                /*for(int i = 0; i < ret; i+=2) {
                    byte aux = byteData[i];
                    byteData[i] = byteData[i+1];
                    byteData[i+1] = aux;
                }*/
                dOSObjectClientWriter.write(asdp);
                bytesread += ret;
            } else {
                break;
            }
        }
        System.out.println("Total = "+total);
        Thread.sleep(1000);
        dOSObjectClientWriter.close();
    }
    
    public static void rawPlayShortAudioFileViaAudioTrack() throws IOException, InterruptedException {
        PHATClientConnection phatClientConnection = new PHATClientConnection(
                "192.168.0.108", 65056, "Smartphone1", Service.SPEAKER);
        phatClientConnection.connect();
        OutputStream outputStream = phatClientConnection.getSocket().getOutputStream();
        

//Reading the file..
        File file = new File("/home/pablo/tts_file.wav");
        int intSize = (int) file.length();
        byte[] byteData = new byte[intSize];
        
        FileInputStream in = new FileInputStream("/home/pablo/tts_file.wav");

        int bytesread = 0, ret = 0;

        int total = 0;
        // Read data
        while ((ret = in.read(byteData, 0, intSize)) > 0) {
            total += ret;
            System.out.println("read = "+ret);
            if (ret != -1) {
                outputStream.write(byteData, 0, ret);
                bytesread += ret;
            } else {
                break;
            }
        }
        System.out.println("Total = "+total);
        outputStream.close();
        phatClientConnection.close();
    }
}

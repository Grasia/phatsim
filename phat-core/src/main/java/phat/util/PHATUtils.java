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

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author pablo
 */
public class PHATUtils {

    public static void removeFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void removeFileWithExtension(String path, final String extension) {
        File[] files = new File(path).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isFile()) {
                    return file.getName().endsWith('.' + extension);
                }
                return false;
            }
        });
        for (File file : files) {
            file.delete();
        }
    }
    
    public static void removeNativeFiles() {
        if(isWindows()) {
            removeFileWithExtension(".", "dll");
        } else {
            removeFileWithExtension(".", "so");
        }
    }
    
    public static boolean isWindows() {
        String so = System.getProperty("os.name");
        return so.contains("Windows");
    }
    
    public static void checkAndCreatePath(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
    
    /**
     * Checks if arguments contains multilisterner option "-lm"
     * @param args
     * @return 
     */
    public static boolean isMultiListener(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("-ml")) {
                return true;
            }
        }
        return false;
    }
}

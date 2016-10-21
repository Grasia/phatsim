/*
 * Copyright (C) 2016 Pablo Campillo-Sanchez <pabcampi@ucm.es>
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
package phat.console;

/**
 *
 * @author pablo
 */
public class JSONUtils {

    public static String toStringFormatted(String in, int tabSize) {
        String result = "";
        int numberTabs = 0;
        String currentMaring = "";
        boolean commillas = false;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);

            switch (c) {
                case '"':
                    result += c;
                    commillas = !commillas;
                    break;
                case '{':
                case '[':
                    result += "\n";
                    currentMaring = getSameChar(" ", numberTabs);
                    result += currentMaring;
                    result += c;
                    numberTabs += tabSize;
                    if (i + 1 < in.length() && in.charAt(i + 1) != '[' && in.charAt(i + 1) != '{') {
                        result += "\n";
                        currentMaring = getSameChar(" ", numberTabs);
                        result += currentMaring;
                    }
                    break;
                case '}':
                case ']':
                    numberTabs -= tabSize;
                    result += "\n";
                    currentMaring = getSameChar(" ", numberTabs);
                    result += currentMaring;
                    result += c;
                    if (i + 1 < in.length() && in.charAt(i + 1) != ',' && in.charAt(i + 1) != '[' && in.charAt(i + 1) != '{') {
                        result += "\n";
                    }
                    break;
                case ',':
                    if (!commillas && in.charAt(i + 1) != '[' && in.charAt(i + 1) != '{') {
                        result += c + "\n" + currentMaring;
                    }
                    break;
                default:
                    result += c;
                    break;
            }
        }
        return result;
    }

    public static String getSameChar(String s, int times) {
        StringBuilder outputBuffer = new StringBuilder(times);
        for (int i = 0; i < times; i++) {
            outputBuffer.append(s);
        }
        return outputBuffer.toString();
    }
}

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
package phat.body.sensing.hearing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It contains a list to add sentences. It creates a file with a JSGF Grammar
 * where only given sentences are valid.
 *
 * @author pablo
 */
public class GrammarFacilitator {

    private static final String HEAD = "#JSGF V1.0;\n"
            + "/**\n"
            + " * JSGF Grammar for CMUSphinx\n"
            + " */\n"
            + "grammar simple;\n";
    private static final String FILE_EXTENSION = "gram";
    List<String> sentences = new ArrayList<>();
    String path;
    String name;

    public GrammarFacilitator(String path, String name) {
        this.path = path;
        this.name = name;
    }

    /**
     * Adds a sentence to be included in the gramar. Sentences are converted to
     * lowercase because of cmusphinx.
     *
     * @param sentence
     * @return
     */
    public GrammarFacilitator add(String sentence) {
        sentences.add(sentence.toLowerCase());
        return this;
    }

    /**
     * Create a file with the JSGF Grammar
     *
     * @return
     */
    public boolean createFile() {
        try {
            try (FileWriter newDic = new FileWriter(path + "/" + name + "." + FILE_EXTENSION); BufferedWriter bufWriter = new BufferedWriter(newDic)) {
                bufWriter.write(HEAD);
                if (sentences.size() > 0) {
                    bufWriter.write("public <sentence> = (" + sentences.get(0));

                    for (int i = 1; i < sentences.size(); i++) {
                        bufWriter.write(" | " + sentences.get(i));
                    }
                    bufWriter.write(" );\n");
                }
                bufWriter.flush();
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(GrammarFacilitator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}

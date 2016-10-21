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

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 * @author pablo
 */
public class ResponseParserImpl implements ResponseParser {

    @Override
    public String getFormattedString(JSONRPC2Request request, JSONRPC2Response response) {
        String id = String.valueOf(request.getID());
        String result = request.getMethod() + " (" + id + "):\n";
        result += "-------------------------------------------------------\n";
        try (JsonReader jsonReader = Json.createReader(new StringReader(response.getResult().toString()))) {
            if (request.getMethod().equals("help")) {
                JsonArray array = jsonReader.readArray();
                List<JsonObject> jOList = new ArrayList(array.getValuesAs(JsonObject.class));
                if (!jOList.isEmpty()) {
                    Collections.sort(jOList, new Comparator<JsonObject>() {
                        @Override
                        public int compare(JsonObject o1, JsonObject o2) {
                            return o1.getString("cName").toLowerCase().compareTo(o2.getString("cName").toLowerCase());
                        }
                    });
                }
                for (JsonObject o : jOList) {
                    result += o.getString("cUsage") + "\n";
                }
            } else {
                result += JSONUtils.toStringFormatted(response.getResult().toString(), 2)+"\n";
            }
            result += "*******************************************************";
        }
        return result;
    }

    private String getColumnSeparation(String in, String separator, int colNum) {
        String[] cols = in.split(separator);
        String result = "";
        for (int i = 0; i < cols.length; i++) {
            String item = cols[i];
            int intLenght = item.length();
            result += append(item, " ", colNum - intLenght);
        }
        return result;
    }

    private String append(String in, String string, int times) {
        for (int i = 0; i < times; i++) {
            in += string;
        }
        return in;
    }
}

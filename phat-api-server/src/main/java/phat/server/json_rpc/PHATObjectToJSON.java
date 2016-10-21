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
package phat.server.json_rpc;

import com.google.common.collect.HashBiMap;
import com.jme3.scene.Spatial;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.conditions.AutoCondParam;
import phat.agents.automaton.conditions.AutomatonCondition;
import phat.agents.filters.DiseaseManager;
import phat.agents.filters.Symptom;
import phat.structures.houses.House;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.HouseFactory.HouseType;

/**
 *
 * @author pablo
 */
public class PHATObjectToJSON {

    static enum KEYS {
        id, role, name, type, state, priority, condition, condName, stage, symptoms, level, rooms, objects
    }

    public static JSONObject getJSON(Automaton automaton) {
        Map<String, Object> map = new HashMap<>();
        Set<String> keySet = automaton.getMetaKeys();
        if (keySet != null) {
            Iterator<String> it = keySet.iterator();
            while (it.hasNext()) {
                String key = it.next();
                map.put(key, automaton.getMetadata(key));
            }
        }
        map.put(PHATObjectToJSON.KEYS.condition.name(), getJSON(automaton.getFinishCondition()));
        map.put(PHATObjectToJSON.KEYS.priority.name(), automaton.getPriority());
        map.put(PHATObjectToJSON.KEYS.name.name(), automaton.getName());
        map.put(PHATObjectToJSON.KEYS.state.name(), automaton.getState());
        JSONObject result = new JSONObject(map);
        return result;
    }

    public static JSONObject getJSON(AutomatonCondition condition) {
        if (condition != null) {
            Class<?> cClass = condition.getClass();
            String condName = cClass.getSimpleName();
            Map<String, Object> map = new HashMap<>();
            map.put(PHATObjectToJSON.KEYS.condName.name(), condName);

            for (Method m : cClass.getMethods()) {
                AutoCondParam cp = m.getAnnotation(AutoCondParam.class);
                if (cp != null) {
                    try {
                        String value = String.valueOf(m.invoke(condition));
                        map.put(cp.name(), value);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(PHATObjectToJSON.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println(ex);
                    }
                }
                return new JSONObject(map);
            }
        }
        return null;
    }

    public static JSONObject getDisease(DiseaseManager dm) {
        if (dm == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(PHATObjectToJSON.KEYS.symptoms.name(), getSymptoms(dm));
        map.put(PHATObjectToJSON.KEYS.stage.name(), dm.getStage());

        return new JSONObject(map);
    }

    public static JSONArray getSymptoms(DiseaseManager dm) {
        JSONArray symptoms = new JSONArray();

        Iterator<Symptom> it = dm.getSymptoms().iterator();
        while (it.hasNext()) {
            symptoms.add(it.next());
        }
        return symptoms;
    }

    public static JSONObject getSymptom(Symptom symptom) {
        Map<String, Object> map = new HashMap<>();
        map.put(PHATObjectToJSON.KEYS.level.name(), symptom.getCurrentLevel());
        map.put(PHATObjectToJSON.KEYS.type.name(), symptom.getSymptomType());

        return new JSONObject(map);
    }

    public static JSONObject getJSON(String id, HouseType houseType, House house) {
        Map<String, Object> map = new HashMap<>();

        JSONArray roomsArray = new JSONArray();
        for (String rName : house.getRoomNames()) {
            roomsArray.add(rName);
        }
        map.put(PHATObjectToJSON.KEYS.rooms.name(), roomsArray);
        map.put(PHATObjectToJSON.KEYS.type.name(), houseType);
        map.put(PHATObjectToJSON.KEYS.name.name(), id);

        return new JSONObject(map);
    }

    public static JSONObject getJSON(String roomName, House house) {
        Map<String, Object> map = new HashMap<>();

        JSONArray objArray = new JSONArray();
        for (Spatial s : house.getPhyObjecInRoom(roomName)) {
            JSONObject o = getIDAndRole(s);
            if (o != null) {
                objArray.add(o);
            }
        }
        map.put(PHATObjectToJSON.KEYS.objects.name(), objArray);
        map.put(PHATObjectToJSON.KEYS.name.name(), roomName);

        return new JSONObject(map);
    }

    public static JSONObject getIDAndRole(Spatial spatial) {
        System.out.println("spatial Name = "+spatial.getName());
        String id = spatial.getUserData("ID");
        System.out.println("id = "+id);
        System.out.println("");
        if (id != null) {
            Map<String, Object> map = new HashMap<>();
            map.put(PHATObjectToJSON.KEYS.id.name(), id);
            map.put(PHATObjectToJSON.KEYS.role.name(), spatial.getUserData("ROLE"));

            return new JSONObject(map);
        }
        return null;
    }
}

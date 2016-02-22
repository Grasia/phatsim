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
package phat.agents.events;

/**
 *
 * @author pablo
 */
public class EventRecord {
    long timestamp;
    PHATEvent event;

    public EventRecord(long timestamp, PHATEvent event) {
        this.timestamp = timestamp;
        this.event = event;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PHATEvent getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "EventRecord{" + "timestamp=" + timestamp + ", event=" + event + '}';
    }
}

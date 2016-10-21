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
package phat.structures.houses.commands;

import com.jme3.app.Application;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.world.PHATCalendar;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "UpdateDateTime", type = "env", debug = false)
public class UpdateDateTimeCommand extends PHATCommand {

    int year = -1;
    int month = -1;
    int dayOfMonth = -1;
    int hourOfDay = -1;
    int min = -1;
    int sec = -1;

    public UpdateDateTimeCommand() {
    }

    public UpdateDateTimeCommand(PHATCommandListener l) {
        super(l);
    }

    @Override
    public void runCommand(Application app) {
        WorldAppState worldAppState = app.getStateManager().getState(WorldAppState.class);

        PHATCalendar calendar = worldAppState.getCalendar();

        if (calendar != null) {
            if (year != -1) {
                calendar.setYear(year);
            }
            if(month != -1) {
                calendar.setMonth(month);
            }
            if(dayOfMonth != -1) {
                calendar.setDayOfMonth(dayOfMonth);
            }
            if(hourOfDay != -1) {
                calendar.setHourOfDay(hourOfDay);
            }
            if(min != -1) {
                calendar.setMinute(min);
            }
            if(sec != -1) {
                calendar.setSecs(sec);
            }
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @PHATCommParam(mandatory = false, order = 1)
    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    @PHATCommParam(mandatory = false, order = 2)
    public void setMin(int min) {
        this.min = min;
    }

    @PHATCommParam(mandatory = false, order = 3)
    public void setSec(int sec) {
        this.sec = sec;
    }

    @PHATCommParam(mandatory = false, order = 4)
    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    @PHATCommParam(mandatory = false, order = 5)
    public void setMonth(int month) {
        this.month = month;
    }

    @PHATCommParam(mandatory = false, order = 6)
    public void setYear(int year) {
        this.year = year;
    }
    
}

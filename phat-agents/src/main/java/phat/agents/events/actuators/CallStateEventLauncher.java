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
package phat.agents.events.actuators;

import phat.agents.events.PHATEventListener;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.mobile.adm.TelephonyRegistryChecker;

/**
 *
 * @author pablo
 */
public class CallStateEventLauncher {

    TelephonyRegistryChecker telephonyRegistryChecker;
    DeviceSource deviceSource;
    AndroidVirtualDevice avd;
    PHATEventListener eventListener;
    String lastPhoneNumber = "";
    float timeToChek = 0.5f;
    float timer = 0f;
    String currentCallState = TelephonyRegistryChecker.CALL_STATES.IDLE.name();
    boolean stateChanged = false;
    
    Thread callStateProxy;

    public CallStateEventLauncher(AndroidVirtualDevice avd, DeviceSource deviceSource, PHATEventListener eventListener) {
        this.avd = avd;
        this.deviceSource = deviceSource;
        this.eventListener = eventListener;
        this.telephonyRegistryChecker = new TelephonyRegistryChecker(avd.getSerialNumber());
    }

    public void update(float tpf) {
        timer += tpf;
        if (stateChanged) {
            stateChanged = false;
            // Launch event new call state
            eventListener.newEvent(
                    new PHATCallStateEvent(
                    deviceSource.getId() + "-Call-" + currentCallState,
                    deviceSource,
                    currentCallState,
                    lastPhoneNumber));
        }
        if (timer >= timeToChek) {
            timer = 0f;
            // check new state
            if (callStateProxy == null || !callStateProxy.isAlive()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String phoneNumber = telephonyRegistryChecker.getIncomingPhoneNumber();
                        String state = telephonyRegistryChecker.getCallState().name();
                        if(!state.equals(currentCallState)) {
                            stateChanged = true;
                        }
                        currentCallState = state;
                        lastPhoneNumber = phoneNumber;
                    }
                }).start();
            }
        }
    }
}

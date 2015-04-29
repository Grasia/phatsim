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
package phat.agents.automaton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import phat.PHATInterface;
import phat.agents.Agent;

/**
 * Esta clase implementa un automata de estados finitos. En ella, hay que dar
 * las posibles transiciones entre estados. Una función de transición aleatoria
 * es asumida, pero se puede redefinir en otras clases. IMPORTANTE, PARA USAR
 * ESTADOS EN FSM DEBEN REDEFINIR RESTART, VER QUICK START DE MANUAL Los fallos
 * derivados de una mala implementación del método restart son particularmente
 * difíciles de detectar ya que se manifiestan no la primera vez que se hace una
 * transición a un estado, sino en las sucesivas transiciones a este (cuando no
 * se ha ejecutado el restart adecuadamente por el inteprete del FSM).
 *
 * @author Juan A. Botía, Pablo Campillo, Francisco Campuzano, and Emilio
 * Serrano
 */
public class FSM extends Automaton {

    /**
     * Mapa indicando para una automata el conjunto de siguientes estados
     * (también objetos de tipo automata) que se pueden dar.
     */
    protected HashMap<Automaton, ArrayList<Transition>> possibleTransitions = new HashMap<Automaton, ArrayList<Transition>>();
    /**
     * Lista con estados finales
     */
    protected ArrayList<Automaton> finalStates = new ArrayList<Automaton>();
    /**
     * Estado incial
     */
    protected Automaton initialState = null;

    /**
     * Mapa para registar pdfs a transiciones
     */
    // protected HashMap<Automaton,HashMap<Automaton,Pdf>> pdfForTransitions=
    // new HashMap<Automaton,HashMap<Automaton,Pdf>> ();;
    /**
     * Vease implementación del padre
     */
    public FSM(Agent agent) {
        super(agent);
    }

    /**
     * Vease implementación del padre
     *
     */
    public FSM(Agent agent, int priority, String name) {
        super(agent, priority, name);
    }

    public void registerStartState(Automaton automaton) {
        initialState = automaton;
        initialState.parent = this;
    }

    /**
     * Registrar las posibles transiciones desde las que se puede ir a un
     * estado. Si el automata tiene un único estado, se puede pasar null como
     * destino.
     *
     * @param source
     * @param destiny
     */
    public void registerTransition(Automaton source, Automaton destiny) {
        source.parent = this;
        destiny.parent = this;
        ArrayList<Transition> r = possibleTransitions.get(source);
        if (r == null) {
            r = new ArrayList<Transition>();
        }
        r.add(new Transition(destiny));
        possibleTransitions.put(source, r);        
    }

    /**
     * Registrar una transicion que incluye una condicion arbitraria
     *
     * @param source
     * @param destiny
     */
    public void registerTransition(Automaton source, Transition transition) {
        source.parent = this;
        transition.getTarget().parent = this;
        ArrayList<Transition> r = possibleTransitions.get(source);
        if (r == null) {
            r = new ArrayList<Transition>();
        }
        r.add(transition);
        possibleTransitions.put(source, r);
    }

    /**
     * Registrar las posibles transiciones desde las que se puede ir a un
     * estado. Si el automata tiene un único estado, se puede pasar null como
     * destino. Además se le pasa una función de probabilidad, no se transita si
     * la función no lo permite
     *
     * @param source
     * @param destiny
     */
    /*
     * public void registerTransition(Automaton source,Automaton destiny, Pdf
     * pdf){ registerTransition(source,destiny); HashMap<Automaton,Pdf> aux= new
     * HashMap<Automaton,Pdf>(); aux.put(destiny,pdf);
     * pdfForTransitions.put(source, aux); }
     */
    /**
     * Pasandole un array genera todas las transiciones posibles entre los
     * estados.
     */
    public void registerAllPossibleTransition(Automaton[] states) {
        for (int i = 0; i < states.length; i++) {
            for (int j = i; j < states.length; j++) {
                registerTransition(states[i], states[j]);
                registerTransition(states[j], states[i]);
            }
        }
    }

    /**
     * Registrar estado final del autómata. Puede haber varios.
     */
    public void registerFinalState(Automaton nameFinalState) {
        finalStates.add(nameFinalState);
        nameFinalState.parent = this;
    }

    /**
     * Dado un estado se devuelve una lista con los posibles estados que le
     * siguen en el protocolo.
     *
     * @param source
     * @return
     */
    public ArrayList<Transition> possibleNextStates(Automaton source) {
        ArrayList<Transition> r = possibleTransitions.get(source);
        if (r == null) {
            throw new UnsupportedOperationException(
                    "Not transitions registered from " + source.toString()
                    + ", automaton " + this.toString());
        }
        ArrayList<Transition> activatedTransitions = new ArrayList<Transition>();
        for (Transition t : r) {
            if (t.evaluate()) {
                activatedTransitions.add(t);
            }
        }
        ;
        return activatedTransitions;

    }

    /**
     * Método para decidir una transición de las posibles. En su forma por
     * defecto tomar aleatoriamente una de las posibles transiciones desde el
     * estado actual. Se puede redefinir método para restringir las transiciones
     * posibles. Además compara el registro de pdfs para ver si la transición
     * actual esta regida por una de ellas.
     *
     * @param simState
     * @return
     */
    public Automaton decideTransition(PHATInterface phatInterface) {
        ArrayList<Transition> states = possibleNextStates(currentState);
        if (states.isEmpty()) {
            return null;
        }
        Transition r = states.get(phatInterface.getRandom().nextInt(
                states.size()));
        notifyNextAutomaton(r.getTarget());
        return r.getTarget();

    }
    
    @Override
    public void replaceCurrentAutomaton(Automaton automaton) {
        if(currentState != null) {
            for(Transition t: possibleTransitions.get(currentState)) {
                registerTransition(automaton, t);
            }
            currentState.setFinished(true);
            currentState.notifyNextAutomaton(automaton);
        }
        currentState = automaton;
    }

    private Automaton checkIfTransitionIsActivated(Automaton source, PHATInterface phatInterface) {
        List<Transition> activatedTransitions = new ArrayList<Transition>();
        ArrayList<Transition> r = possibleTransitions.get(source);
        if (r != null) {
            for (Transition t : r) {
                if (t.evaluate()) {
                    activatedTransitions.add(t);
                }
            }
            if (!activatedTransitions.isEmpty()) {
                Transition trans = activatedTransitions.get(phatInterface.getRandom().nextInt(
                        activatedTransitions.size()));
                return trans.getTarget();
            }
        }
        return null;
    }

    /**
     * Este método lleva le control del autómata. Si el estado actual ha
     * terminado, se toma aleatoriamente uno de los estados a los que se puede
     * transitar. Si el estado alcanzado es final, se finaliza el automáta.
     * Vease comentarios en código de método.
     *
     * @param state
     */
    @Override
    public void nextState(PHATInterface phatInterface) {
        if (!init) {
            notifityPreInitToListeners();
            initState(phatInterface);
            init = true;
            notifityPostInitToListeners();
        }
        // si este nivel esta terminado, devolver control
        if (this.isFinished(phatInterface)) {
            setFinished(true);
            return;
        }
        // si marca de pausa, ignorar
        if (pause) {
        	return ;
        }
        // El estado incial se da en el primer registro.
        if (currentState == null) {
            currentState = this.initialState;
            if (initialState == null) {
                throw new UnsupportedOperationException(
                        "Initial state not given, some transitions must be registered");
            }
            notifyNextAutomaton(currentState);
            if (!currentState.init) {
                notifityPreInitToListeners();
                currentState.initState(phatInterface);
                currentState.init = true;
                notifityPostInitToListeners();
            }
        }
        // se comprueba si se cumple una condicion de transicion
		/*Automaton nextstate = checkIfTransitionIsActivated(currentState, phatInterface);
         if (nextstate != null) {
         currentState = nextstate;
         currentState.restart(phatInterface);// reiniciar estado por si ya se
         // había usado
         if (ECHO) {
         System.out.println(agent.getId() + " with automaton " + name
         + ", transition to state " + currentState.toString());
         }
         }*/

        // comprobar si estado actual se ha acabado.
        if (currentState.isFinished(phatInterface)) {
            currentState.notifityListeners(true);
            printPendingTransitions();
            // comrpobar si era estado final para dar por termiando este
            // automata
            if (ECHO) {
                System.out.println(agent.getId() + " with automaton " + name
                        + ", finishes states " + currentState.toString());
            }
            if (this.finalStates.contains(currentState)) {
                this.setFinished(true);
                notifityListeners(true);
                if (ECHO) {
                    System.out.println(agent.getId() + ", " + name
                            + " automaton finished");
                }
                return ;
            }
            // si no, llamar a decidir una transición
            Automaton nstate = decideTransition(phatInterface);
            if (nstate != null) {
                currentState = nstate;           
                currentState.restart(phatInterface);// reiniciar estado por si
                // ya se había usado
            }
            if (ECHO) {
                System.out.println(agent.getId() + " with automaton " + name
                        + ", transition to state " + currentState.toString());
            }
        }

        if (!currentState.isFinished(phatInterface)) {
            // siguiente paso del estado actual
            currentState.nextState(phatInterface); // los estados tienen a su
            // bez subestados, es
            // automata jerárquico.
        }
       
    }

    /**
     * Método forceState para simular una comunicación entre automatas. Se le
     * pasa el estado al que se quiere forzar el automata a transitar.
     *
     * @param state Estado al que transitar
     * @param simState
     */
    public void forceState(Automaton state, PHATInterface phatInterface) {
        currentState = state;
        currentState.restart(phatInterface);// reiniciar estado por si ya se
        // había usado
        if (ECHO) {
            System.out
                    .println(agent.getId() + " with automaton " + name
                    + ", transition forced to state "
                    + currentState.toString());
        }
    }

    public void printPendingTransitions() {
        System.out.println(agent.getId() + ":" + name
                + " - Possible Transitions:");
        for (Automaton a : possibleTransitions.keySet()) {
            System.out.println("\t-" + a.getName());
        }
    }

    @Override
    public Automaton getDefaultState(PHATInterface phatInterface) {
        throw new UnsupportedOperationException("Not used");
    }

    @Override
    public ArrayList<Automaton> createNewTransitions(PHATInterface phatInterface) {
        throw new UnsupportedOperationException("Not used");
    }

    @Override
    public void initState(PHATInterface phatInterface) {
    }
}

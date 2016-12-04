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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.conditions.AutomatonCondition;

/**
 * Clase automata de comportamiento de personas. Los automatas son a la vez
 * estados. Hay un constructor para el autómata padre o principal y otro para
 * hijos (o nietos). Si un automata tiene otro subordinado, nextState lleva el
 * control, i.e., lo que hace en cada paso. Si un estado es el más bajo de la
 * jerarquía, autómata que no tiene un sub-autómata subordinado, se redefine el
 * método nextState para que haga algo concreto (extendiendo la clase
 * SimpleState).
 *
 * Esta clase utiliza un patrón estado (un autómata se comporta de manera
 * distinta según su sub-automata actual) y un patrón composite (los a utómatas
 * se pueden crear componiendo distintos sub-autómatas).
 *
 * @todo ES NECESARIO UNA LISTA DE AUTOMATAS CONCURRENTES, AUTOMATAS A LOS QUE
 * SE LES DA PASO SIEMPRE EN CADA STEP DE EJECUCIÓN: PARA CHEQUEAR SI HAY FUEGO,
 * PARA VER SI HAN RECIBIDO UN AVISO DE UN PROTOCOLO... LO QUE SEA. SERIA UN
 * COMPORTAMIETNO EXTRA
 * @author Juan A. Botía, Pablo Campillo, Francisco Campuzano, and Emilio
 * Serrano
 */
public abstract class Automaton {

    protected HashMap<String, String> metadata = null;
    /**
     * Persona que implementa el automata
     */
    protected Agent agent;
    /**
     * Estado actual del autómata
     */
    protected Automaton currentState;
    /**
     * Parent automaton
     */
    Automaton parent;
    /**
     * Lista de tareas o transiciones pendientes en el autómata.
     */
    protected LinkedList<Automaton> pendingTransitions;
    /**
     * Imprimir evolución de automata por pantalla
     */
    protected static boolean ECHO = true;
    /**
     * Prioridad del estado o comportamientos del autómata en forma de entero.
     * No hay valores especiales
     */
    protected int priority;
    /**
     * Nombre del estado o comportamientos del autómata
     */
    protected String name;

    /**
     * Automaton states
     */
    public static enum STATE {

        NOT_INIT, STARTED, DEFAULT, DEFAULT_STARTED, FINISHED, RESUMED, INTERRUPTED
    };
    STATE state = STATE.NOT_INIT;
    /**
     * Referencia a automata padre, no obligatoria
     */
    protected Automaton automatonFahter = null;
    protected AutomatonCondition finishCondition;
    List<AutomatonListener> listeners = new ArrayList<>();
    boolean canBeInterrupted = true;
    AutomatonModificator automatonModificator;

    public void addListener(AutomatonListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeListener(AutomatonListener l) {
        listeners.remove(l);
    }

    void notifityListeners() {
        for (AutomatonListener al : listeners) {
            al.stateChanged(this, state);
        }
    }

    public Automaton containsStateOfKind(Class<? extends Automaton> targetClass) {
        Iterator<Automaton> iterator = this.pendingTransitions.iterator();
        Automaton next;
        while (iterator.hasNext()) {
            next = iterator.next();
            if (targetClass.isAssignableFrom(next.getClass())) {
                return next;
            } else {
                Automaton result = next.containsStateOfKind(targetClass);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public Automaton containsStateWithPrefix(String prefix) {
        Iterator<Automaton> iterator = this.pendingTransitions.iterator();
        Automaton next;
        while (iterator.hasNext()) {
            next = iterator.next();
            if (next.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                return next;
            } else {
                Automaton result = next.containsStateWithPrefix(prefix);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Este método crea un autómata principal. El más alto de la jerarquía. Por
     * eso no hace falta pasar valores como la duración o la prioridad.
     *
     * @param personImplementingAutomaton
     */
    public Automaton(Agent agent) {
        this.agent = agent;
        pendingTransitions = new LinkedList<>();
        this.name = agent.getId() + "-AUTOMATON";
        this.priority = 0;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Fijar una referencia al automata padre.
     *
     * @param a
     */
    public void setAutomatonFather(Automaton a) {
        this.automatonFahter = a;
    }

    /**
     * Este método crea un autómata distinto al más alto de la jerarquía.
     *
     * @param personImplementingAutomaton
     * @param priority Prioridad para ejecutar este sub-autómata
     * @param name Nombre
     */
    public Automaton(Agent agent, int priority, String name) {
        this.agent = agent;
        pendingTransitions = new LinkedList<>();
        this.name = name;
        this.priority = priority;

    }

    /**
     * Añadir transición a un estado en la lista de transiciones pendientes. La
     * lista queda ordenada de mayor a menor prioridad.
     *
     * @param newTransition Estado destino de la transición
     * @param first es true si se quiere que se ponga el primero en cola si
     * varios tienen la misa prioridad (caso de pausar un estado porque te
     * interesa que sea el siguiente), false si es el último (caso de insertar
     * un nuevo estado porque te interesa que el código muestre el orden de
     * ejecución).
     */
    public void addTransition(Automaton newTransition, boolean first) {
        newTransition.parent = this;
        if (this.pendingTransitions.isEmpty()) {
            this.pendingTransitions.add(newTransition);
        } else {
            int i = 0;
            for (Automaton ps : pendingTransitions) {
                if (!first && newTransition.priority > ps.priority) {
                    pendingTransitions.add(i, newTransition);
                    break;
                }
                if (first && newTransition.priority >= ps.priority) {
                    pendingTransitions.add(i, newTransition);
                    break;
                }
                i++;
            }
            // si no se insertó, se pone al final
            if (i == pendingTransitions.size()) {
                pendingTransitions.add(newTransition);
            }
        }
    }

    protected Automaton getNextAutomaton() {
        Automaton newTransition = null;
        if (!pendingTransitions.isEmpty()) {
            newTransition = pendingTransitions.getFirst();
            pendingTransitions.removeFirst();

            transmitListeners(newTransition);
        }
        return newTransition;
    }

    protected void transmitListeners(Automaton newTransition) {
        if (newTransition != null) {
            newTransition.setAutomatonModificator(automatonModificator);

            for (AutomatonListener al : listeners) {
                newTransition.addListener(al);
            }
        }
    }

    /**
     * Comprueba las transiciones pendientes actuales, si alguna tiene el mismo
     * nombre que la pasada como parámetro devuelve true. También comprueba el
     * estado actual.
     *
     * @param name
     * @return
     */
    public boolean isTransitionPlanned(String name) {
        return (getTransitionPlanned(name) != null);
    }

    /**
     * Comprueba las transiciones pendientes actuales y el estado actual, si
     * alguna tiene el mismo nombre que la pasada como parámetro la devuelve,
     * null si no la encuentra.
     *
     * @param name
     * @return
     */
    public Automaton getTransitionPlanned(String name) {
        for (Automaton ps : pendingTransitions) {

            if (ps.name.equals(name)) {
                return ps;
            }
        }
        if (currentState != null && currentState.name.equals(name)) {
            return currentState;
        }
        return null;
    }

    /**
     * Número de transiciones pendientes.
     *
     * @return
     */
    public int transitionsPlanned() {

        return this.pendingTransitions.size();
    }

    public void interrupt(PHATInterface phatInterface) {
    }

    public void resume(PHATInterface phatInterface) {
    }

    /**
     * Este método lleva le control del autómata. En esencia, se delega en el
     * nextState del estado/autómata hijo actual. Utiliza dos métodos abstractos
     * que dependen de la persona, y por tanto, del tipo de estado concreto. Un
     * estado que no tenga autómata redefine este método (extendiendo
     * SimpleState). Vease comentarios en código de método.
     *
     * @nota: no hay una etiqueta de estado a ignorar
     * @param state
     * @return
     */
    public void nextState(PHATInterface phatInterface) {
        switch (state) {
            case NOT_INIT:
                //System.out.println(getAgent().getId()+":"+getName()+"(NOT_INIT)");
                /*currentState = null;
                if(pendingTransitions != null) {
                    pendingTransitions.clear();
                }*/ 
                initState(phatInterface);
                currentState = getNextAutomaton();
                setState(STATE.STARTED);
                break;
            case DEFAULT:
                //System.out.println(getAgent().getId()+":"+getName()+"(DEFAULT)");
                currentState = getDefaultState(phatInterface);
                if (currentState == null) {
                    setState(STATE.FINISHED);
                } else {
                    transmitListeners(currentState);
                    setState(STATE.DEFAULT_STARTED);
                }
                break;
            case DEFAULT_STARTED:
            case STARTED:
                //System.out.println(getAgent().getId()+":"+getName()+"(STARTED)");
                run(phatInterface);
                break;
            case FINISHED:
                //System.out.println(getAgent().getId()+":"+getName()+"(FINISHED)");
                break;
            case INTERRUPTED:
                //System.out.println(getAgent().getId()+":"+getName()+"(INTERRUPTED)");
                if (finishCondition != null) {
                    finishCondition.automatonInterrupted(this);
                }
                if (currentState != null) {
                    currentState.setState(STATE.INTERRUPTED);
                    currentState.nextState(phatInterface);
                }
                interrupt(phatInterface);
                break;
            case RESUMED:
                //System.out.println(getAgent().getId()+":"+getName()+"(RESUMED)");
                if (finishCondition != null) {
                    finishCondition.automatonResumed(this);
                }
                if (currentState != null) {
                    currentState.setState(STATE.RESUMED);
                    currentState.nextState(phatInterface);
                }
                resume(phatInterface);
                setState(STATE.STARTED);
                break;
        }
    }

    protected void run(PHATInterface phatInterface) {
        if (isFinished(phatInterface)) {
            setState(STATE.FINISHED);
        } else if (currentState == null) {
            setState(STATE.DEFAULT);
        } else if (currentState.getState() == STATE.INTERRUPTED) {
            currentState.setState(STATE.RESUMED);
        } else {
            // There is one state with higher priority?
            if (isPossibleAttendAHigherPriorityState()) {
                currentState.setState(STATE.INTERRUPTED);
                currentState.nextState(phatInterface); // Transmit the interruption
                addTransition(currentState, true);
                currentState = getNextAutomaton();
            } else if (currentState.getState() == STATE.FINISHED) {
                //System.out.println(agent.getId()+":"+currentState.getName()+"(FINISHED)");
                currentState = getNextAutomaton();
            } else {
                if (automatonModificator != null) {
                    Automaton last = currentState;
                    currentState = automatonModificator.monitoring(currentState);
                    if(last != currentState) {
                        transmitListeners(currentState);
                    }
                }
                currentState.nextState(phatInterface);
            }
        }
    }

    protected boolean isPossibleAttendAHigherPriorityState() {
        return !pendingTransitions.isEmpty()
                && pendingTransitions.getFirst().priority > currentState.priority
                && (currentState == null || currentState.isCanBeInterrupted());
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><"+agent.getId()+":"+getName()+":"+state.name());
        this.state = state;
        notifityListeners();
    }

    /**
     * Método de terminación Si se fijo externamente a finalizado, ha finalizado
     * Si la duración no se fijo a -1 y el tiempo que queda es menor a 0, ha
     * finalizado En otro caso no ha terminado. Este método puede redefinirse en
     * clases hijas (por ejemplo para comparar si se ha llegado a un destino).
     * Se recomienda llamar al finish padre en el método redefinido. Si este
     * método devuelve true, en el control del autómata hay que fijar a true la
     * finalización (ver método nextState)
     *
     * @param phatInterface
     * @return
     */
    public boolean isFinished(PHATInterface phatInterface) {
        if (finishCondition != null && finishCondition.evaluate(agent)) {
            return true;
        }
        return false;
    }

    /**
     * Un automata debe incluir alguna descripción para el ECHO que ayude a la
     * depuración
     *
     * @return
     */
    @Override
    public String toString() {
        String result = agent.getId() + ": " + name + " (" + state + "):\n";
        for (Automaton a : pendingTransitions) {
            result += "\t- " + a.name + " (" + state + ")\n";
        }
        return result;
    }

    /**
     * Borrar transiciones pendientes. Útil, por ejemplo, al interrumpir un
     * estado.
     */
    protected void clearPendingTransitions() {
        this.pendingTransitions.clear();
    }

    /**
     * Compara si el nombre del estado actual (no la descripción de toSTring que
     * es más completa) es igual a la pasada como parámetro.
     *
     * @param s
     */
    public boolean isCurrentState(String s) {
        if (currentState == null) {
            return false;
        }
        return currentState.name.equals(s);
    }

    /**
     * Obtener el sub-autómata actual.
     *
     * @return
     */
    public Automaton getCurrentState() {
        return currentState;
    }

    /**
     *
     * @param phatInterface
     * @return
     */
    public abstract void initState(PHATInterface phatInterface);

    /**
     * Estado por defecto al que se vuelve cuando no hay ninguna transición en
     * la lista. Si se devuelve null, cuando el autómata acabe las transiciones
     * pendientes devolverá el control al autómata padre.
     *
     * @return
     */
    public abstract Automaton getDefaultState(PHATInterface phatInterface);

    /**
     * Obtener nombre del autómata.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * True si el string pasado es el nombre del automata.
     *
     * @param s
     * @return
     */
    public boolean isName(String s) {
        return name.equals(s);
    }

    /**
     * Fijar salida por pantalla
     *
     * @param b
     * @return
     */
    public static void setEcho(boolean b) {
        ECHO = b;
    }

    /**
     * Obtener el automata más profundo en curso que no sea SimpleState. Se usa
     * en comunicaciones para insertar comportamientos fruto de la reacción.
     */
    public static Automaton getDeepestAutomaton(Agent agent) {
        Automaton a1 = agent.getAutomaton();
        Automaton a2 = agent.getAutomaton().currentState;
        while (a2 != null || !(a2 instanceof SimpleState)) {
            a1 = a2;
            a2 = a2.currentState;

        }
        return a1;

    }

    /**
     * Obtener el automata de cierto nombre dentro de un persona actualmente en
     * ejecución
     */
    public static void addTransitionInSpecificAutomaton(Agent agent,
            String automatonName, Automaton newTransition) {
        Automaton aut = agent.getAutomaton();
        while (!aut.name.equals(automatonName)) {
            if (aut.currentState == null) {
                throw new RuntimeException("Automaton " + automatonName
                        + " is not been executed in " + agent);

            }
            aut = aut.currentState;
        }
        aut.addTransition(newTransition, true);

    }

    public Agent getAgent() {
        return agent;
    }

    public Automaton setFinishCondition(AutomatonCondition finishCondition) {
        this.finishCondition = finishCondition;
        return this;
    }

    public AutomatonCondition getFinishCondition() {
        return this.finishCondition;
    }

    public Automaton getLeafAutomaton(Vector<Automaton> traversed) {
        if (currentState != null) {
            if (currentState instanceof SimpleState) {
                return currentState;
            } else {
                if (traversed.contains(currentState)) {
                    new Exception("Loop detected. Looping sequence is " + traversed + ". The repeated element was " + currentState).printStackTrace();
                    System.exit(0);
                    return null;
                } else {
                    traversed.add(currentState);
                    return currentState.getLeafAutomaton(traversed);
                }
            }
        } else {
            return null;
        }
    }

    public Automaton getCurrentAutomaton() {
        Automaton result = this;
        while (result.currentState != null) {
            result = result.currentState;
        }
        return result;
    }

    public Automaton getLeafAutomaton() {
        return getLeafAutomaton(new Vector<Automaton>());
    }

    public String getCurrentActionName() {
        if (currentState != null) {
            return currentState.getCurrentActionName();
        }
        return getName();
    }
    
    public Automaton getCurrentAction() {
        if (currentState != null) {
            return currentState.getCurrentAutomaton();
        }
        return this;
    }

    public boolean isCanBeInterrupted() {
        return canBeInterrupted;
    }

    public Automaton setCanBeInterrupted(boolean canBeInterrupted) {
        this.canBeInterrupted = canBeInterrupted;
        return this;
    }

    public <T extends Automaton> T getCurrentUpperAutomatonByType(Class<T> automatonType) {
        if (currentState != null) {
            if (currentState.getClass().getSuperclass().equals(automatonType)) {
                return (T) currentState;
            } else {
                return currentState.getCurrentUpperAutomatonByType(automatonType);
            }
        } else {
            return null;
        }
    }

    public Automaton setMetadata(String key, String data) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }

        metadata.put(key, data);
        return this;
    }

    public String getMetadata(String key) {
        if (metadata == null) {
            return null;
        }

        return metadata.get(key);
    }
    
    public Set<String> getMetaKeys() {
        if(metadata != null) {
            return metadata.keySet();
        }
        return null;
    }

    public Automaton getParent() {
        return parent;
    }

    public Automaton getRootParent() {
        Automaton parent = getParent();
        while (parent != null && parent.getParent() != null) {
            parent = parent.getParent();
        }
        if (parent == null) {
            return this;
        }
        return parent;
    }

    public AutomatonModificator getAutomatonModificator() {
        return automatonModificator;
    }

    public void setAutomatonModificator(AutomatonModificator automatonModificator) {
        this.automatonModificator = automatonModificator;
    }

    public int getPriority() {
        return priority;
    }
}

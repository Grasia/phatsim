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
import java.util.LinkedList;
import java.util.List;

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
     * Booleano para indicar que el automata ha terminado.finished indica tanto
     * parada como pausa dependiendo de que alguien vuelva a ejecutar el
     * automata o no.
     */
    private boolean finished;
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
     * Marca para pausar el autómata, por ejemplo durante ciertas interacciones
     */
    protected boolean pause = false;
    /**
     * Inidica si se ha inicializado el estado
     */
    protected boolean init = false;
    /**
     * Referencia a automata padre, no obligatoria
     */
    protected Automaton automatonFahter = null;
    protected AutomatonCondition finishCondition;
    List<AutomatonListener> listeners = new ArrayList<>();
    boolean canBeInterrupted = true;

    public void addListener(AutomatonListener l) {
        listeners.add(l);
    }

    public void removeListener(AutomatonListener l) {
        listeners.remove(l);
    }

    public void notifityListeners(boolean isSuccessful) {
        for (AutomatonListener al : listeners) {
            al.automatonFinished(this, isSuccessful);
        }
    }

    public void notifityPreInitToListeners() {
        for (AutomatonListener al : listeners) {
            al.preInit(this);
        }
    }
    
    public void notifityPostInitToListeners() {
        for (AutomatonListener al : listeners) {
            al.postInit(this);
        }
    }

    public void notifyNextAutomaton(Automaton nextAutomaton) {
        for (AutomatonListener al : listeners) {
            al.nextAutomaton(this, nextAutomaton);
        }
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
        this.finished = false;
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
        this.finished = false;

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

    /**
     * Devuelve la transición de más prioridad, la primera (saca de lista, borra
     * y pone finish a false). Si se le pasa un estado actual, lo pausa, y lo
     * mete en cola para volverlo a ejecutar en el futuro. Para testar esta
     * clase se puede comparar nombres de transiciones intercambiadas y así
     * asegurarse que el campo y el pause se hacen adecuadamente.
     *
     * @nota La versión anterior usaba setFinished, actualmente la pausa es lo
     * que hace que el estado se prepare para una futura reanudación.
     */
    protected Automaton nextAutomaton(Automaton stateToBeReplaced,
            PHATInterface phatInterface) {
        // if(currentState!=null) System.out.println("STATE: " +
        // currentState.toString() + " PENDING " +
        // this.pendingTransitions.toString());
        printPendingTransitions();
        Automaton newTransition = pendingTransitions.getFirst();
        pendingTransitions.removeFirst();
        if (stateToBeReplaced != null) {
            stateToBeReplaced.interrupt();// el estado será retomado
            if (ECHO) {
                System.out.println(agent.getId() + ", " + name + " paused "
                        + stateToBeReplaced.name);
            }
            addTransition(stateToBeReplaced, true);
        }
        notifyNextAutomaton(newTransition);
        return newTransition;
    }

    public void replaceCurrentAutomaton(Automaton automaton) {
        if(currentState != null) {
            currentState.interrupt();
            currentState.setFinished(true);
            currentState.notifyNextAutomaton(automaton);
        }
        currentState = automaton;
    }
    
    public void printPendingTransitions() {
        System.out.println(agent.getId() + ":" + name
                + " - Pending Transitions:");
        for (Automaton a : pendingTransitions) {
            System.out.println("\t-" + a);
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

    /**
     * Este método lleva le control del autómata. En esencia, se delega en el
     * nextState del estado/autómata hijo actual. Utiliza dos métodos abstractos
     * que dependen de la persona, y por tanto, del tipo de estado concreto. Un
     * estado que no tenga autómata redefine este método (extendiendo
     * SimpleState). Vease comentarios en código de método.
     *
     * @nota: no hay una etiqueta de estado a ignorar
     * @param state
     */
    public void nextState(PHATInterface phatInterface) {
        // si marca de pausa, ignorar
        if (pause || finished) {
            return;
        }
        if (!init) {
            notifityPreInitToListeners();
            initState(phatInterface);
            init = true;
            notifityPostInitToListeners();
        }
        // generar nuevas transiciones y añadirlas en cola si no se devuelve
        // null
        ArrayList<Automaton> newTransitions = createNewTransitions(phatInterface);
        if (newTransitions != null && !newTransitions.isEmpty()) {
            for (Automaton ps : newTransitions) {
                this.addTransition(ps, false);
            }
            if (ECHO) {
                System.out.println(agent.getId() + ", " + name
                        + " pending transitions extended "
                        + pendingTransitions.toString());
            }
        }
        // tratamiento para dar estado inicial y comprobar si estado actual se
        // ha acabado.
        if (currentState == null || currentState.isFinished(phatInterface)) {
            if (currentState != null) {// ya había un estado y ahora esta
                // terminado
                if (ECHO) {
                    System.out.println(agent.getId() + ", " + name
                            + " automaton finished " + currentState.toString());
                }
                currentState.setFinished(true);// fijar como terminado
                currentState.notifityListeners(true);
            }
            if (!pendingTransitions.isEmpty()) {// tomar siguiente transición
                // pendiente (tanto si es como
                // estado inicial o como
                // siguiente)
                currentState = nextAutomaton(null, phatInterface);
                if (ECHO) {
                    System.out.println(agent.getId() + ", " + name
                            + " automaton changes to state "
                            + currentState.toString());
                }
            } else {// si no hay transiciones pendintes ir a estado por defecto
                // (tanto si es como estado inicial o como siguiente)
                currentState = getDefaultState(phatInterface);
                if (ECHO && currentState != null) {
                    System.out.println(agent.getId() + ", " + name
                            + " automaton changes default state "
                            + currentState.toString());
                }
            }
            if (currentState == null) {// no hay estado por defecto ni
                // transiciones pendientes, se pone
                // finalizado y se devuelve para dar
                // control al autómata padre
                this.setFinished(true);
                notifityListeners(true);
                if (ECHO) {
                    System.out
                            .println(agent.getId()
                            + ", "
                            + name
                            + " automaton finished and no default state given, control returned to upper automaton. ");
                }
                return;
            }
        }

        // parar estado en curso para iniciar uno de mayor prioridad
        if ((!pendingTransitions.isEmpty())
                && pendingTransitions.getFirst().priority > currentState.priority
                && currentState.isCanBeInterrupted()) {
            currentState.notifityListeners(true);
            currentState = nextAutomaton(currentState, phatInterface);
            if (ECHO) {
                System.out.println(agent.getId() + ", change due to priority, "
                        + name + " automaton changes to state "
                        + currentState.toString());
            }

        }

        // siguiente paso del estado actual
        currentState.nextState(phatInterface); // los estados tienen a su vez
        // subestados, es automata
        // jerárquico.
    }

    /**
     * Este método es llamado cuando un estado es parado por falta de prioridad.
     * En él se debe preparar el estado para una reanudación. Si el estado es,
     * por ejemplo, ir al baño, y te quedas en un punto del plano cuando te
     * paran... no basta retomar dicho estado tras un tiempo porque tu posición
     * habrá cambiado. En esos casos, entre otras cosas, habrá que borrar la
     * lista de transiciones pendientes con clearPendingTransitions. Se puede
     * redefinir para dar otros comportamientos. Por ejemplo, asignar restar
     * tiempos de ejecución (ver MoveAndStay donde se recupera la transición
     * Stay y se le resta de la duración el tiempo ya ejeceutado).
     */
    public void interrupt() {
        this.clearPendingTransitions();
        if (currentState != null) {
            currentState.interrupt();
        }
    }

    /**
     * Este método es llamado cuando se quiere usar una instancia de automata
     * otra vez sin crear una nueva. Por ejemplo por ejemplo en el automata
     * estático. Deben ponerse todas las variables necesarias como en el inicio
     * de la clase.
     *
     * @param simState
     */
    public void restart(PHATInterface phatInterface) {
        setFinished(false);
        currentState = null;
        pause = false;
        this.clearPendingTransitions();
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
     * @return
     */
    public boolean isFinished(PHATInterface phatInterface) {
        if (finishCondition != null && finishCondition.evaluate(agent)) {
            return true;
        }
        return finished;
    }

    /**
     * Fijar que el automata termine. No confundir con método pause, no hay
     * reanudación tras finish.
     *
     * @param finished
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Fijar que el automata en el estado actual termine. No confundir con
     * método pause, no hay reanudación tras finish.
     *
     * @param finished
     */
    public void setFinishedTheCurrentState(boolean finished) {
        currentState.finished = finished;
    }

    /**
     * Un automata debe incluir alguna descripción para el ECHO que ayude a la
     * depuración
     *
     * @return
     */
    public String toString() {
        return name;
    }

    /**
     * Borrar transiciones pendientes. Útil, por ejemplo, al interrumpir un
     * estado.
     */
    protected void clearPendingTransitions() {
        this.pendingTransitions.clear();
    }

    /**
     * Ver si automatata esta pausado
     *
     * @return
     */
    public boolean getPause() {
        return pause;
    }

    /**
     * Pausar automata
     *
     * @param b
     */
    public void setPause(boolean b) {
        this.pause = b;
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
     * Método que devuelve un conjunto de autómatas/estados a los que transitar
     * desde el estado actual. Este método se redefine en automatas que
     * extiendan Automaton para crear los sub-autómatas o estados. Este método
     * creará los autómatas/estados necesarios El método puede usar currentState
     * para decidir el siguiente estado que se dará. En caso de dar varios
     * estados se tomarán por orden de prioridad (aunque el array devuelto puede
     * ir desordenado)
     *
     * @return
     */
    public abstract ArrayList<Automaton> createNewTransitions(
            PHATInterface phatInterface);

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

    public Automaton getLeafAutomaton() {
        if (currentState != null) {
            if (currentState instanceof SimpleState) {
                return currentState;
            } else {
                return currentState.getLeafAutomaton();
            }
        } else {
            return null;
        }
    }

    public String getCurrentAction() {
        if (currentState != null) {
            return currentState.getCurrentAction();
        }
        return getName();
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
            System.out.println("asdf->" + currentState.getName());
            if (currentState.getClass().getSuperclass().equals(automatonType)) {
                System.out.println("asdf->Activity");
                return (T) currentState;
            } else {
                System.out.println("asdf->no");
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

    public Automaton getParent() {
        return parent;
    }
}

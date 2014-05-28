mvn exec:java -Dexec.mainClass="phat.mason.PHATSimState"
Lanzamiento de una simulación desde MASON usando PHAT. Pero MASON delega
el control a PHAT que va llamando al planificador de MASON dependiendo del
adaptador de tiempo (p.e. un step por segundo).

Para ello he realizado sobre todo adaptadores:
1. phat.mason.TimeAdapter.
2. phat.mason.space.HouseAdapter: Carga el modelo de la casa con sus objetos usando PHAT y se genera
un mundo MASON (Continuous3D) que contiene los objetos físicos de la casa.
Estos objetos implementan la interfaz PhysicsObject (la que usa MASON) y 
AbstractControl (la que usa PHAT). Estos objetos físicos se añaden como controlador
a los objetos PHAT (Spatial) de forma que actualizan su posición en el mundo MASON.
Continuous3D tiene funciones eficientes para buscar objetos en el espacio.
3. phat.mason.agents.ActorAdapter: La ida es la parecida a la anterior.

Para la lógica de los agentes he tomado estas clases de UbikSim (autómata gerárquico):
- Automaton (TODO: tener en cuenta el TimeAdapter)
- FSM
- SimpleState

y he creado subclases con de Automaton repesentando los estados.
El grueso de la lógica está en phat.mason.agents.PatientAgent

Automaton start = new DoNothing(this, 0, 5, "PatientIdle"); // Está sin hacer nada 5 segundos
Automaton lookBehindR = new PlayAnimation(this, 0, 0, "LookBehindR", "LookBehindR"); // Mira a la derecha
Automaton tripOver = new TripOver(this, 0, 0, "TripOver"); // Se cae
Automaton tryStandUp = new DoNothing(this, 0, 5, "TryStandUp"); // "Intenta levantarse" durante 5 segundos
Automaton requestHelp = new RequestHelp(this, 0, 0,"RequestHelp", 8f); // Pide ayuda (pone el automaton MoveTo en relativeAgent)
        
FSM fsm = new FSM(this);
fsm.registerStartState(start);
fsm.registerTransition(start, lookBehindR);
fsm.registerTransition(lookBehindR, tripOver);
fsm.registerTransition(tripOver, tryStandUp);
fsm.registerTransition(tryStandUp, requestHelp);
fsm.registerFinalState(requestHelp);


Para lanzar la misma simulación con la GUI de MASON:
mvn exec:java -Dexec.mainClass="phat.mason.gui.GUISimState"

Para lanzar la demo con android 
env PATH=$PATH:METER_AQUI_EL_PATH_A_la_carpeta_tools_del_SDK_DE_ANDROID:METER_AQUI_EL_PATH_A_la_carpeta_platform_tools_del_SDK_DE_ANDROIDp mvn compile exec:java -Dexec.mainClass="phat.mason.PHATSimState"

Ojo: El AVD del teléfono
- android 4.2.2
- 64MB de heap
-512 MB RAM
- al menos 100MB de tarjeta SD
- al menos 300MB de memoria interna
- procesador intel
- aceleración instalada (en windows, seguir instrucciones para conseguir aceleración, en linux, tener instalado qemu-kvm y NO TENER INSTALADO VIRTUALBOX)
- haber hecho antes en el móvil una instalación de la app. PAra eso, ir a phat-android/phat-android-app y hacer un mvn android:deploy teniendo arrancadas los emuladores de todos los móviles implicados.
- configurar en el móvil para que no tenga el apagado al minuto. Eso se hace arrancando el emulador del móvil, accediendo a settings (arrastrando hacia abajo la barra superior y pulsando en la esquina superior derecha), display y luego configurar el sleep a 30min.

SIMULATION 1:
Shows simulated sun and witch shadows enabled. The time is accelerated 1000 times
in order to see the sun movement.
mvn exec:java -Dexec.mainClass="phat.world.TestWorld"

SIMULATION 2:
Similiar scenario but it shows a house where 3 lights are switched on and off
every 10 seconds.
mvn exec:java -Dexec.mainClass="phat.structures.houses.TestHouse"

SIMULATION 3:

mvn exec:java -Dexec.mainClass="phat.environment.TestSpatialEnvironmentAPI"
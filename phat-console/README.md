# Instalation
1. Make sure the "config.properties" exists and sets the right ip address:
```
ip=127.0.0.1
port=44123
```
2. A PHAT simulation should be running before launching the Console GUI.
To launch the Console GUI:
```
$mvn exec:java -Dexec.mainClass="phat.console.ConsoleControl"
```
3. It should print "connected!"
4. Type the commands in the botom text field.
5. Console GUI commands (don't type '>'. It's just to identify command entries.

To show all commands and their usage:
```
>help
```
It is possible to show command usage by its type:
```
>help {env|body|agent|device|info}
```
Result:
```
>help env # To show commands related with the environment
>help body # To show commands related with the bodies of the characters
>help device # To show commands related with the devices
>help info # To show commands that get information about the entities of the simulation
```
For example: Sitdown a character in a chair in the kitchen

# Methods to get information commands
```
>help info
```
Result:
```
help (0):
-------------------------------------------------------
agentInfo <bodyId>
bodyInfo [bodyId]
envInfo [roomId]
help [env|body|device|info]
*******************************************************
```
# To get information about the characters available.
```
>body info
```
Result:
```
bodyInfo (2):
-------------------------------------------------------

[
  {
    "date":"Thu Sep 29 14:03:15 CEST 2016",
    "posture":"Standing",
    "walking":false,
    "lastCommand":"GoToCommand(Human0 (8.766094 2.3841858E-7 1.7770876))",
    "id":"Human0",
    "maxSpeed":0.5,
    "anim":null,
    "room":"LivingRoom"
  }
  {
    "date":"Thu Sep 29 14:03:15 CEST 2016",
    "posture":"Sitting",
    "walking":false,
    "lastCommand":"GoToCommand(Human0 (8.766094 2.3841858E-7 1.7770876))",
    "id":"Sujeto1",
    "maxSpeed":0.5,
    "anim":null,
    "room":"LivingRoom"
  }
]
*******************************************************
```
There are two: Human0 and Sujeto1. Sujeto 1 will be choosen. Before, we need to know the name of the command:
```
>help body
```
Result:
```
help (3):
-------------------------------------------------------
AlignBodyWith <bodyId> <entityId>
AttachIcon <bodyId> <imagePath> <true|false>
BodyLabel <bodyId> <true|false>
CloseObject <bodyId> <bodyId> <objectId> [minDistance]
CreateBody <bodyId> <Elder|Young|ElderLP>
DebugSkeleton <bodyId> <true|false>
FallDown <bodyId>
GoCloseToBody <bodyId> <targetBodyId>
GoCloseToObject <bodyId> <bodyId> <targetObjectId> [minDistance]
GoIntoBed <bodyId> <bedId>
GoToSpace <bodyId> <spaceId>
LookAt <bodyId> <targetId>
OpenObject <bodyId> <bodyId> <objectId> [minDistance]
PickUp <bodyId> <bodyId> <entityId> <Left|Right>
RemoveBodyFromSpace <bodyId>
RotateToward <bodyId> <bodyId> <entityId> [true|false]
SayASentence <bodyId> <bodyId> <message> [volume]
SetBodyColor <bodyId> <bodyId> <bodyId> <bodyId> <r> <g> <b> <a>
SetBodyHeight <bodyId> <height>
SetBodyInHouseSpace <bodyId> <spaceId>
SetBodyXYZ <bodyId> <x> <y> <z>
SetCameraToBody <bodyId> <bodyId> <distance> <height>
SetDisplSpeed <bodyId> <speed>
SetPCListenerToBody <bodyId>
SetRigidArm <bodyId> <bodyId> <true|false> <true|false>
SetShortSteps <bodyId> <true|false>
SetStoopedBody <bodyId> <true|false>
ShowLabelsOfVisibleObjects <bodyId> <true|false>
SitDown <bodyId> <placeId>
StandUp <bodyId>
TremblingHand <bodyId> <bodyId> <true|false> <true|false>
TremblingHead <bodyId> <true|false>
WaitForBody <bodyId> <targetBodyId>
*******************************************************
```
The command is: 
```
SitDown <bodyId> <placeId>
```
It has 2 params:
1. The first one is the id of the body, e.i., "Sujeto1"
2. The second one is the id of the chair where the character will sit.
We need to discover the id of the chair. Before, it is nacesary to get the names of the rooms of the house:
```
>envInfo
```
Result:
```
envInfo (5):
-------------------------------------------------------

{
  "rooms":
  [
    "Kitchen",
    "BathRoom1",
    "BathRoom2",
    "BedRoom1",
    "Hall",
    "BedRoom2",
    "LivingRoom",
    "Outside",
    "Terrace",
    "SCorridor0"
  ],
  "name":"House1",
  "type":"House3room2bath"
}
*******************************************************
```
Now, we can ask for information about the Kitchen:
```
>envInfo Kitchen
```
Result:
```
envInfo (6):
-------------------------------------------------------

{
  "objects":
  [
    {
      "role":"PlaceToSeat",
      "id":"Chair1"
    }
    {
      "role":"PlaceToSeat",
      "id":"Chair2"
    }
    {
      "role":"Bottle",
      "id":"Bottle1"
    }
    {
      "role":"Sink",
      "id":"Sink"
    }
    {
      "role":"Table",
      "id":"Table1"
    }
    {
      "role":"Extractor",
      "id":"Extractor1"
    }

  ],
  "name":"Kitchen"
}
*******************************************************
```

For instance, we choose Chair1, so the command will be:
```
>SitDown Sujeto1 Chair1
```
When a command is finished, the GUI shows the name of the command with its id in brackets and the result: Fail or Success.
The response could take a while. For example, when the character has to walk to a given place.
In the meantime, others commands could be executed, so it is necessary an id to differentiate them.

Result of SitDown command with id 8:
```
SitDown (8):
-------------------------------------------------------
Success
*******************************************************
```


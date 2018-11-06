# PHAT Simulator
[![Build Status](https://travis-ci.com/Melkoroth/phatsim.svg?branch=master)](https://travis-ci.com/Melkoroth/phatsim)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a85d9d9f7e2e458cb8b4ea9edf734994)](https://www.codacy.com/app/Melkoroth/phatsim?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Melkoroth/phatsim&amp;utm_campaign=Badge_Grade)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![OpenJDK Version](https://img.shields.io/badge/openjdk-v1.8-red.svg)](http://openjdk.java.net/)
[![Maven Version](https://img.shields.io/badge/maven-v3.1.1-orange.svg)](http://maven.apache.org/)

**P**hysical **H**uman **A**ctivity **T**ester **Sim**ulator is a JAVA platform for the 3d simulation of Ambient Assisted Living Environments.

It is used to model scenarios where simulated humans recreate activities of daily life. Filters can then be added that represent an illness.


## Requirements:

### Java 1.7 at least (set variable JAVA_HOME). 

In ubuntu at the time of writing the minimun version is 1.8. Install it by typing:
```
$ sudo apt install openjdk-8-jre
$ sudo apt install openjdk-8-jdk-headless
```
You can check the java version with:
```
$ java -version
$ javac -version
```
To change between java versions:
```
sudo update-alternatives --config java
```

### Maven 3.1.1+ at least (set variable M2_HOME).

```
$ sudo apt install maven
```
If you need to set M2 variables add the following lines to your .bashrc or .zshrc or whatever you use:
```
export M2_HOME="/usr/share/maven"
export M2="$M2_HOME/bin"
export PATH="$M2:$PATH"
```


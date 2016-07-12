#!/usr/bin/env bash

# Calls run.jar passing in the input and output parameters
#
# Example:
#
# Notes: The jar was build using jdk 1.8
# If an earlier version of the jdk is needed you will need to:
# 1) Edit pom.xml so that <source>1.8</source> and <target>1.8</target> are updated
# 2) Ensure maven in installed and run mvn clean install
# 3) Ensure the build ran fin and that ./run.jar is updated
 

java -jar src/run.jar ./venmo_input/venmo-trans.txt ./venmo_output/output.txt






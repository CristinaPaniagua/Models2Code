#!/bin/bash

time_to_sleep=10s

echo Starting Core Systems... Service initializations usually need around 20 seconds.


nohup java -jar $(find . -maxdepth 1 -name arrowhead-serviceregistry-\*.jar | sort | tail -n1) &> sout_sr.log &
echo Service Registry started
sleep ${time_to_sleep} #wait for the Service Registry to fully finish loading up


nohup java -jar $(find . -maxdepth 1 -name arrowhead-authorization-\*.jar | sort | tail -n1) &> sout_auth.log &
echo Authorization started


nohup java -jar $(find . -maxdepth 1 -name arrowhead-orchestrator-\*.jar | sort | tail -n1) &> sout_orch.log &
echo Orchestrator started

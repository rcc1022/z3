#!/bin/bash

podman exec -it maven mvn -f /work/clivia/pom.xml dependency:tree
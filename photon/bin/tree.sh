#!/bin/bash

podman exec -it maven mvn -f /work/photon/pom.xml dependency:tree
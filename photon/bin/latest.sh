#!/bin/bash

podman exec -it maven mvn -f /work/photon/pom.xml clean install versions:use-latest-releases
find . -name 'pom.xml.*'
find . -name 'pom.xml.*' -exec rm -rf {} \;
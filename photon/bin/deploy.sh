#!/bin/bash

sh bin/install.sh

podman stop -t 1 tomcat
rm -rf ~/tomcat/webapps/ROOT
cp -r photon-web/target/photon-web-1.0 ~/tomcat/webapps/ROOT
podman restart tomcat
tail -f ~/tomcat/logs/catalina.out

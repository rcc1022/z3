#!/bin/bash

sh bin/install.sh

podman stop -t 1 tomcat
rm -rf ~/tomcat/webapps/ROOT
cp -r clivia-web/target/clivia-web-1.0 ~/tomcat/webapps/ROOT
podman start tomcat
tail -f ~/tomcat/logs/catalina.out

#!/bin/bash

docker stop -t 1 eagle
rm -rf /home/eagle/webapps/ROOT
unzip -q ~/work/eagle-web.war -d /home/eagle/webapps/ROOT
docker start eagle
tail -n100 -f /home/eagle/logs/catalina.out
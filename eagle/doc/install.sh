#!/bin/bash

apt update -y
apt upgrade -y
apt install -y unzip docker.io
systemctl enable docker
systemctl restart docker

docker network create local

docker run -d \
    --privileged=true \
    --restart=always \
    -e MYSQL_ROOT_PASSWORD=root \
    --name=mysql \
    -p 3306:3306 \
    mysql:latest

sleep 10s
rm -rf /home/mysql
mkdir -p /home/mysql
docker cp mysql:/etc/mysql /home/mysql/etc
docker cp mysql:/var/lib/mysql /home/mysql/lib
docker stop -t 1 mysql
docker rm mysql
docker run -d \
    --privileged=true \
    --restart=always \
    --network=local \
    -v /home/mysql/etc:/etc/mysql \
    -v /home/mysql/lib:/var/lib/mysql \
    --name=mysql \
    mysql:latest

# c
docker run -d \
    --privileged=true \
    --restart=always \
    --network=local \
    -v /home/eagle/webapps:/tomcat/webapps \
    -v /home/eagle/upload:/tomcat/webapps/ROOT/upload \
    -v /home/eagle/logs:/tomcat/logs \
    --name=eagle \
    -p 8080:8080 \
    tomcat:9.0.x

docker run -d \
    -p 80:80 -p 443:443 \
    --privileged=true \
    --restart=always \
    --network=local \
    -v /home/nginx/http.d:/etc/nginx/http.d \
    -v /home/nginx/log:/var/log/nginx \
    -v /home/certbot/letsencrypt:/etc/letsencrypt \
    -v /home/certbot/log:/var/log/certbot \
    --name=certng \
    certng:latest

docker exec -it certng certbot --nginx --register-unsafely-without-email -d 5kiz.cn
docker exec -it certng certbot --nginx --register-unsafely-without-email -d ouigr.cn
docker exec -it certng certbot --nginx --register-unsafely-without-email -d jb100.cn
docker exec -it certng certbot --nginx --register-unsafely-without-email -d elcxzk.cn
docker exec -it certng certbot --nginx --register-unsafely-without-email -d echtp.cn
docker exec -it certng certbot --nginx --register-unsafely-without-email -d ycsbe.cn

docker exec -it certng certbot --nginx --register-unsafely-without-email -d ql4k9.cn
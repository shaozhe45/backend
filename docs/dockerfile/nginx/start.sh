#!/bin/bash

docker stop changqing_nginx
docker rm changqing_nginx
docker run -idt -p 10000:10000 -p 180:180 --name changqing_nginx --restart=always \
    -v /data/projects/:/data/projects \
    -v `pwd`/conf/:/etc/nginx \
    -v `pwd`/logs/:/var/log/nginx  \
    -v `pwd`/html/crossdomain.xml:/usr/share/nginx/html/crossdomain.xml  \
    -e TZ="Asia/Shanghai" \
    nginx:1.17.0


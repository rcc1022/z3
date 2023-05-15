#!/bin/bash

sh bin/install.sh

ssh -i~/.ssh/jump root@139.180.221.26 'rm -rf ~/work/jump/eagle-web.war'
scp -i~/.ssh/jump eagle-web/target/eagle-web.war root@139.180.221.26:~/work/jump/
ssh -i~/.ssh/jump root@139.180.221.26 'sh ~/work/jump/test.sh'
#!/bin/bash

rm -rf clivia-web/src/main/webapp/c
cd clivia-console/ui
sh bin/build.sh
mv build ../../clivia-web/src/main/webapp/c
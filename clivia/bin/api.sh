#!/bin/bash

rm -rf clivia-web/src/main/webapp/a
cd clivia-api/ui
sh bin/build.sh
mv build ../../clivia-web/src/main/webapp/a
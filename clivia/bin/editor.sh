#!/bin/bash

cd clivia-editor/ui
sed -i "s/^const root = .*;$/const root = '';/" src/http.js
rm -rf dist
yarn build
git checkout src/http.js
cd ../..

rm -rf clivia-web/src/main/webapp/e
cp -rf clivia-editor/ui/dist clivia-web/src/main/webapp/e

rm -rf ~/tomcat/webapps/ROOT/e
cp -rf clivia-editor/ui/dist ~/tomcat/webapps/ROOT/e
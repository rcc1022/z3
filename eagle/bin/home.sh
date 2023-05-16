#!/bin/bash

rm -rf eagle-web/src/main/webapp/assets
rm -rf eagle-web/src/main/webapp/favicon.ico
rm -rf eagle-web/src/main/webapp/index.html

cd eagle-home/ui
rm -rf dist
sed -i "s/^const root = .*;$/const root = '';/" src/util/http.js
npm run build
git checkout src/util/http.js
cp -rf dist/** ../../eagle-web/src/main/webapp/
#!/bin/bash

rm -rf build
sed -i "s/^const root = .*;$/const root = 'http://43.154.106.190:8080';/" src/http.js
yarn build
git checkout src/http.js
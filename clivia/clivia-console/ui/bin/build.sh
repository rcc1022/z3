#!/bin/bash

rm -rf build
sed -i "s/^const root = .*;$/const root = '';/" src/http.js
yarn build
git checkout src/http.js
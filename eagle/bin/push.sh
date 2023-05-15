#!/bin/bash

git add bin
git add doc
git add pom.xml

for name in eagle-*; do
  git add $name/src
  git add $name/pom.xml
done

for name in eagle-*/ui; do
  git add $name/src
  git add $name/declaration.d.ts
  git add $name/index.html
  git add $name/package.json
  git add $name/README.md
  git add $name/vite.config.js
done

git add eagle-sync/crawler/bin
git add eagle-sync/crawler/docker
git add eagle-sync/crawler/main
git add eagle-sync/crawler/*.go
git add eagle-sync/crawler/config.json
git add eagle-sync/crawler/go.mod

git commit -m dev
git push

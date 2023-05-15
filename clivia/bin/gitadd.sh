#!/bin/bash

git add bin
git add LICENSE
git add pom.xml
git add README.md
git add .gitignore

for name in clivia-*
do
  git add $name/src
  git add $name/pom.xml
done

for name in clivia-console/ui clivia-api/ui
do
  git add $name/bin
  git add $name/public
  git add $name/src
  git add $name/config-overrides.js
  git add $name/package.json
  git add $name/README.md
  git add $name/yarn.lock
done

for name in clivia-editor/ui
do
  git add $name/public
  git add $name/src
  git add $name/.gitignore
  git add $name/index.html
  git add $name/package.json
  git add $name/README.md
  git add $name/vite.config.js
done
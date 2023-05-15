#!/bin/bash

git add bin
git add LICENSE
git add pom.xml
git add README.md
git add .gitignore

for name in photon-*
do
  git add $name/src
  git add $name/pom.xml
done
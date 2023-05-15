#!/bin/bash

for name in c o;do
  rm -rf eagle-web/src/main/webapp/$name
  cp -rf ../clivia/clivia-web/src/main/webapp/c eagle-web/src/main/webapp/$name

  sed -i 's/React App/百乐庭/g' eagle-web/src/main/webapp/$name/index.html

  dir=eagle-web/src/main/webapp/$name/static/js
  js=`ls -l $dir | grep -E '.js$' | awk '{print $NF}'`
  jsmap=`ls -l $dir | grep -E '.js.map$' | awk '{print $NF}'`

  sed -i 's/clivia-console/百乐庭/g' $dir/$js
  sed -i 's/clivia-console/百乐庭/g' $dir/$jsmap
  sed -i 's/Clivia Console/百乐庭/g' $dir/$js
  sed -i 's/Clivia Console/百乐庭/g' $dir/$jsmap
  sed -i 's/Clivia UI/百乐庭/g' $dir/$js
  sed -i 's/Clivia UI/百乐庭/g' $dir/$jsmap
done;

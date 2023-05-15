# UI

## install

```
dnf module list nodejs
dnf module install -y nodejs:15/default
npm install -g yarn

curl -sL https://rpm.nodesource.com/setup_14.x | bash -
curl -sL https://dl.yarnpkg.com/rpm/yarn.repo | tee /etc/yum.repos.d/yarn.repo
dnf install -y nodejs yarn make g++

yarn create react-app ui
cd ui
yarn add antd react-app-rewired customize-cra babel-plugin-import react-draft-wysiwyg draft-js draftjs-to-html html-to-draftjs @ant-design/charts
sed -i 's/react-scripts /react-app-rewired /g' package.json
```

## upgrade

```
sh bin/latest.sh
```
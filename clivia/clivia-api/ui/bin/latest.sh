#!/bin/bash

yarn upgrade-interactive --latest
rm -rf node_modules
rm -rf yarn.lock
yarn
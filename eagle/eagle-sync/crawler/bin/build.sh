#!/bin/bash

rm -rf crawler
go build -o crawler main/main.go

rm -rf /mnt/hgfs/share/desert/eagle/crawler
mv crawler /mnt/hgfs/share/desert/eagle/
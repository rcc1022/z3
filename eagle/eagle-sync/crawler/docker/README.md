```
docker build -t crawler:latest crawler
```

```
docker run -d \
    --privileged=true \
    --restart=always \
    --network=local \
    -v /home/crawler/567sq:/crawler \
    --name=crawler-567sq \
    crawler:latest
```

```
docker run -d \
    --privileged=true \
    --restart=always \
    --network=local \
    -v /home/crawler/ouigr:/crawler \
    --name=crawler-ouigr \
    crawler:latest
```

```
docker run -d \
    --privileged=true \
    --restart=always \
    --network=local \
    -v /home/crawler/jb100:/crawler \
    --name=crawler-jb100 \
    crawler:latest
```
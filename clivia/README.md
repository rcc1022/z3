# clivia

dependency on [tephra](https://github.com/heisedebaise/photon).

## bin

upgrade dependencies to latest version.

```bash
sh bin/latest.sh
```

install.

```bash
sh bin/install.sh
```

build console ui.

```bash
sh bin/console.sh
```

deploy to a [tomcat in podman](https://github.com/heisedebaise/docker/tree/master/tomcat:9.0).

```bash
sh bin/deploy.sh
```

git push.

```bash
sh bin/push.sh
```

## root user

When you log in to the `root` user for the first time, an account will be created automatically, and the password will be the one you provided when logging in. The `root` user has all permissions.
# vws4ls

## Build and start

### Build jar files

First, all necessary jar files need to build.
We use a dedicated development container for this, defined by the `docker-compose.yml` file.

```sh
docker-compose build
docker-compose run sdk
cd /src/FlowableDelegates
mvn package
```

This will generate jar files in the corresponding `target` folders, like `target/FlowableDelegates-1.0-SNAPSHOT.jar`.

### Build containers

Next, the docker images need to be build.
These use the previously generated jar files, which need to be copied:

```sh
cd deploy
./build.sh
```

## Start flowable ui

Finally, the containers can be started:

```sh
cd deploy
docker-compose up
```

Then visit http://localhost:8081/flowable-ui.

The default credentials are as follows:

```
user: admin
password: test
```

## Use AAS GUI

Open http://localhost:3000/ in your browser.
Then click the burger menu at the top.
Enter the following:

Registry: http://localhost:4000/registry
AAS Server:
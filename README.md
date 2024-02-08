# Getting Started


If you want to build a jar with running test cases:
```shell script
./gradlew clean build
```

If you want to build a jar without running test cases:
```shell script
./gradlew clean build -x test
```

If you want to build a docker image and run in container:
```shell script
./docker-compose up -d
```

You can customise the ports via the application.properties file in `main/resources` 
apply reference to Dockerfile and docker-compose.yaml accordingly



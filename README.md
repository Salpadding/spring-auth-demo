# run

1. run authorization server

```sh
./gradlew auth:bootRun
```

2. run gateway

```sh
./gradlew gateway:bootRun
```

3. run resource server

```sh
./gradlew resource:bootRun
```

4. open browser

http://127.0.0.1:10002/api/v1/resource/me

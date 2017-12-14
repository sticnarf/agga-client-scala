# agga-client-scala

Client for [sticnarf/agga](https://github.com/sticnarf/agga)

## Build

```bash
$ sbt assembly
```

Then, the assembly can be found in the `target/scala-2.12` directory.

## Usage

```bash
$ java -jar agga-client-scala-assembly-0.1-SNAPSHOT.jar \
  -Dakka.cluster.client.initial-contacts.0=akka.tcp://agga@server0.example.com:7000/system/receptionist
  -Dagga.tcp-listen.hostname=127.0.0.1 \
  -Dagga.tcp-listen.port=1080 \
  -Dagga.client-key=client-key-2 \
```
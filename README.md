# Elasticsearch exercise

This project contains a small [Spring Boot](https://spring.io/projects/spring-boot) REST API connected to a [Elasticsearch](https://www.elastic.co/) single-node cluster. The goal of this is to exercise general backend knowledge and get more familiarized with development related to search engines.

## Dataset
The CSV file used to populate an index inside the Elasticsearch cluster was taken from the [IMDB Most Popular Films and Series](https://www.kaggle.com/mazenramadan/imdb-most-popular-films-and-series) dataset. Due to this being an exercise, not all columns of the CSV file are processed, making the media entries look like this to the index:

| Name                        |  Year  | Rate    |  Genre                     |
| :-------------------------- | :----: | :-----: | :------------------------: |
| Venom: Let There Be Carnage |  2021  |   6.4   | Action, Adventure, Sci-Fi  |
| The Simpsons                |  2007  |   8.1   | Animation, Comedy          |
| The Amazing Spider-Man      |  2012  |   6.9   | Action, Adventure, Fantasy |
| Avatar 2                    |  2022  | No rate | Action, Adventure, Sci-Fi  |

The CSV is already included in the project in both [main](src\main\resources\imdb.csv) and [test](src\test\resources\imdb.csv). Note that the files must be kept there, since that's where the program reads them from when creating the index. 

## Pre-requirements
- [Java 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Gradle 7.3.3](https://gradle.org/releases/)
- [Docker Desktop 4.16.2 (or current stable release)](https://docs.docker.com/desktop/release-notes/#4162) 

## Setting up the Elasticsearch cluster
First, it is necessary to pull the Elasticsearch image, for this exercise, version 8.6.1 was used:
```shell
docker pull docker.elastic.co/elasticsearch/elasticsearch:8.6.1
```

If you are using the Windows release of Docker Desktop, you may need to install a Windows Subsystem for Linux distribution in order to run the Elasticsearch container. The default Ubuntu distribution was used in this project, which may be installed with the command:
```shell
wsl --install
```

Note that you can use other distributions if you already had them installed, but in that case, it is recommended to use a distribution with WSL V2 enabled. More info regarding updating to WSL V2 can be found [here](https://learn.microsoft.com/en-us/windows/wsl/install#upgrade-version-from-wsl-1-to-wsl-2)

Then we must start a single-node cluster:
```shell
docker run -p 127.0.0.1:9200:9200 -e xpack.security.enabled=false -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:8.6.1
```
The container created with the previous command must be kept running for the app to work.

## Running
The REST API can be started with the command:

```shell
./gradlew bootRun
```

The command will start a Tomcat server in the port 8080. A [Swagger](https://swagger.io/) UI will be available in http://localhost:8080/swagger-ui/index.html. 

The program automatically connects to the Elasticsearch cluster created earlier, and will try to create an index with the name `imdb_media`, if it doesn't already exist. 

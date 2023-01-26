# Pureinsight's Backend Engineer Technical Test

This skeleton project presents the base structure for a REST API with search capabilities. Has an [Apache Lucene](https://lucene.apache.org/)
in-memory search engine, populated with 12 random movies taken from the [IMDB Most Popular Films and Series dataset](https://www.kaggle.com/mazenramadan/imdb-most-popular-films-and-series):

| Name                        |  Year  |
| :-------------------------- | :----: |
| Venom: Let There Be Carnage |  2021  |
| The Simpsons                |  2007  |
| The Amazing Spider-Man      |  2012  |
| Raiders of the Lost Ark     |  1981  |
| Mr. & Mrs. Smith            |  2005  |
| Predestination              |  2014  |
| The Bourne Supremacy        |  2004  |
| Scary Movie 2               |  2001  |
| Pearl Harbor                |  2001  |
| A Knight's Tale             |  2001  |
| Se7en                       |  1995  |
| Inglourious Basterds        |  2009  |


## Pre-requirements
- [Java 11](https://www.oracle.com/java/technologies/downloads/#java11)
- [Gradle 7.3.3](https://gradle.org/releases/)


## Running
```shell
./gradlew bootRun
```

The command will start a Tomcat server in the port 8080. A [Swagger](https://swagger.io/)
UI will be available in http://localhost:8080/swagger-ui/index.html.

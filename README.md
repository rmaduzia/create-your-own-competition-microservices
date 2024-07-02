# create-your-own-competition-microservices

MIGRATING-TO-MICROSERVICES - WORK IN PROGRESS



# Create competitions

_ _ _

Application for organizing amateur informal and official sports events with tables, results, tree of matches, etc.

Right now I'm working on migrating this project to microservices architecture

- - -

Project is created with:

* Java 17
* Keycloak
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* MySQL
* Flyway
* RabbitMQ
* Maven
* Lombok
* Junit
* Mockito
* Thymeleaf
* Swagger2

- - -



### Features

_ _ _

1. Create events and competitions with all details.
2. Tables with results.
3. Team draws.
4. Create a tree of matches with or without groups, depending on the number of teams.
5. Create team, invite people and join competition or event.
6. After each match, the organizer marks who won and who lost + team members do the same (avoid trying to cheat). the decision is made, as from each team at least 50% will give the same decision as the organizer otherwise, when one of the teams does not agree, the match is repeated or its result is finally approved by the organizer.
7. Possibility to write a public opinion about the organizer
8. Most of data are public, so beginner athletes can boast about how they did before

### Temporary Graph - In Progress:

![create-competition-diagram.png](create-competition-diagram.png)

###### Status

Project is in progress while migration into microservices with some active features and other improvements to make.

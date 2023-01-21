# Playground JPA

This repository contains code written to demonstrate how to use Java Persistence API (JPA) using Hibernate
implementation. Most examples also use Spring Data JPA for convenience, Flyway to automated database state provisioning
and TestContainers to provision Postgres database before test execution.

## How to run

| Description   | Command                                  |
|:--------------|:-----------------------------------------|
| Run tests     | `./gradlew test`                         |
| Create module | `./gradlew createModule -Pmodule=<name>` |

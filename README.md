# Arrowhead Papyrus Utilities (Pre-Release v1.0.0-alpha)

## Table of Contents
1. [Introduction](#introduction)
    1. [About Arrowhead](#about-arrowhead)
    2. [About Papyrus](#about-papyrus)
2. [Pre-Release Utilities](#pre-release-utilities)
    1. [Installation](#installation)
    2. [UML/SysML Model](#umlsysml-model)
    3. [Common Requirements Plugin](#common-requirements-plugin)
    4. [Setup Plugins](#setup-plugins)
        1. [Service Registry](#service-registry)
    5. [Deployment Plugins](#deployment-plugins)
        1. [Local Cloud Core Systems](#local-cloud-core-systems)
        2. [Local Cloud Provider/Consumer Systems](#local-cloud-providerconsumer-systems)
        3. [Database System, Orchestration & Security Rules](#database-system-orchestration-and-security-rules)

## Introduction
### About Arrowhead
The [Eclipse Arrowhead project](https://projects.eclipse.org/projects/iot.arrowhead) consists of systems and services that are needed for anyone to design, implement and deploy Arrowhead-compliant System of Systems. The generic concept of the Arrowhead Framework is based on the concept of Service Oriented Architectures, and aims at enabling all of its users to work in a common and unified approach – leading towards high levels of interoperability.

The [Arrowhead Framework](https://github.com/eclipse-arrowhead) is addressing IoT based automation and digitalisation. The approach taken is that the information exchange of elements in the Internet of Things is abstracted to services. This is to enable IoT interoperability in-between almost any IoT elements . The creation of automation is based on the idea of self-contained Local Clouds. Compared to the well-known concept of global clouds, in Arrowhead a local cloud can provide improvements and guarantees regarding:

* Real time data handling
* Data and system security
* Automation system engineering
* Scalability of automation systems

### About Papyrus
[Eclipse Papyrus](https://www.eclipse.org/papyrus/) is an open-source Model-Based Engineering tool that enables model-based techniques such as simulation, formal testing, safety analysis, performance/trade-offs analysis, and architecture exploration. It is a Domain Specific Language (DSL) platform based on the Unified Modeling Language (UML) and aims to implement the complete UML specification..

## Pre-Release Utilities
### Installation
This plugin offers a set of utilities for the systems' engineering automation of the **setup, deployment and validation** with the modeling help of Papyrus. The installation can be performed through Eclipse by selecting the released zip file of the plugin site in:
* Help -> Install New Software -> Add -> Archive

In the image below we can see that two plugin categories are offered. The installation process must be done in two separate steps, first the common utilities (*Step 1*) followed by the plugins (*Step 2*).

<p align="center">
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/5e2088c4-316c-4b0a-9a41-b59bef3116c3">
</p>

Once everything is installed the upgraded workspace user interface should have a new menu *Arrowhead* containing **Setup, Deployment & Validation** tags for the execution of the plugins as seen in the image below.

<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/12077c7e-6534-4cdd-8618-d56d7daa3e35" width="348" height="78"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/3ab4db6a-ca66-41e0-8648-3238913961b8" width="281" height="78"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/0225a7d9-0297-466d-af82-5fc809dd86b9" width="300" height="78"/>

### UML/SysML Model
The release includes a UML model example of a distributed local cloud system. It is compound by a set of sensors, terminals, a middleware collector and a database. There are two interfaces offered *DataService* and *ConfigurationService* for respectively the obtention and update of data from sensor/database in different elements of the system and configuring the sampling ratio of the sensors.

In the left view we can see a high level description of the Local Cloud Design Descriptions (LCDDs), System Design Descriptions (SysDDs) and Interface Design Descriptions (IDDs). In contrast, the right view shows the internal display or low level description of a Local Cloud where a set of Deployed Entities based on the previously defined SysDDs and IDDs are interconnected, displaying the consumer and provider interactions.

<p align="center">
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/e2d6af14-88b3-4cb4-b5f3-957a970081f8">
</p>

### Common Requirements Plugin

The requirements plugin includes the common libraries shared by the plugins, the [Arrowhead Papyrus Profile (APP)](https://github.com/eclipse-arrowhead/profile-library-sysml/) for the UML/SysML modeling with Papyrus Eclipse, a common API for parsing the UML model and the generated code into APX (approximate) objects of the APP stereotype implementations, the APX class definitions and other utilities for e.g. script execution in different OS.

The utilities require the following programs and minimum versions for their correct functionality:
* Java SE Runtime Environment (JRE) - Version 11
* Maven - Version 3.5
* MySQL - Version 5.7

### Setup Plugins

The setup plugins are meant to define the configuration for the governing core or support systems in the local cloud. The current release includes the Service Registry, i.e. the system in charge of storing the information related to the systems, services, security configuration, etc.

#### Service Registry

This plugin performs two actions, the installation of the AH database and the creation of a user (default name *arrowhead*) with admin permits on the AH tables. The plugin requires the existing root and the new arrowhead users with their respective passwords. If the database already exists, the plugin resets the tables.

<p align="center">
  <img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/4de13128-6144-469c-9bb5-29cdd46ea93e" width="486" height="296"/>
</p>

### Deployment Plugins

The deployment plugins are meant to automate the installation of the core/support systems, the skeleton implementation of the providing/consuming systems in a Local Cloud and the service registry rule creation for the systems' intracloud communication. The plugins generate the **arrowhead** folder in the selected workspace with the folders:

1. Core & Support systems in **arrowhead/core-systems**
2. Provider & Consumer systems in **arrowhead/local-cloud-name/cloud-systems**
3. Database Rules in **arrowhead/local-cloud-name/db-rules**

```
arrowhead
└───core-systems                     (1) Local Cloud Core Systems
└───example-cloud                    (2) Local Cloud Provider/Consumer Systems
    └───cloud-systems
    |   └───collector-provider
    |   |   └───src/main
    |   |       └───java
    |   |       └───resources
    |   ...
    |   └───terminal-b-consumer
    |       └───src/main
    |           └───java
    |           └───resources  
    └───db-rules                     (3) Database System, Orchestration & Security Rules
```

#### Local Cloud Core Systems

This plugin allows the download and compilation of the Arrowhead framework core/support systems into JAR executables. The executables along with the starting/stopping scripts can be found under the folder **arrowhead/core-systems** of the workspace. 

The only-core selection obtains the systems from the repository [arrowhead-core-systems](https://github.com/fernand0labra/arrowhead-core-systems) with version **4.4.1** where as the core+support selection obtains them from the repository [core-java-spring](https://github.com/eclipse-arrowhead/core-java-spring) with version **4.6.1** . 
* The current implementation works for Windows OS and Java. 
* The Maven compilation tests can be skipped for quicker compilation time. 

<p align="center">
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/48982fdd-03d2-474c-a1c5-5701c81bbfa5" width="550" height="340"/>
</p>

#### Local Cloud Provider/Consumer Systems

This plugin allows the skeletons generation of the providing and consuming systems. The generated implementation includes the endpoint network configuration, the request and response data types (DTO package), the communication process with the core systems (Orchestrator, Service Registry & Authorization) and the Java/Maven project for compilation of executables.

Once the papyrus project with the UML model has been selected, the following screen will appear where the local cloud and the systems can be chosen for their code generation. 
* The Java files will be saved under the folder **arrowhead/local-cloud-name/cloud-systems**. 
* The current implementation works for Windows OS and Java. 

<p align="center">
    <img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/dcc9f37c-6ef0-4796-9f08-583306e50b07" width="473" height="432"/>
</p>

In the following tree-like schema, we can see the structure of a providing and consuming system that has been generated with the plugin from the example UML/SysML model. It contains three packages for respectively the controller, the data and the security. 
* API Support for GET and POST operations
  * GET is assumed to not have a request
  * POST response is assumed to be a String
* Protocol Support for HTTP
* Encoding Support for JSON and XML
* Insecure communication (No certificates)

```
collector-provider/src/main                                 Provider & Consumer System
└───java
│   └───eu/arrowhead
│       └───provider
│       │   CollectorProviderMain.java                      Main Function         (Consumer behavior)
│       │   ProviderApplicationInitListener.java            Application Listener  (Provider behavior)
│       │   ServiceControllerHttp.java                      Service Controller    (Provider behavior)
│       └───dto
│       │   ConfigureRequestDTO.java
│       │   GetDataResponseDTO.java
│       │   UpdateDataRequestDTO.java
│       └───security                                        NOTE - Security files currently not in use
│           ProviderAccessControlFilter.java
│           ProviderSecurityConfig.java
│           ProviderTokenSecurityFilter.java
└───resources
    │   application.properties                              System & Network Configuration
    │   info.txt
    └───certificates                                        NOTE - Certificates should be self-generated
    └───META-INF
            additional-spring-configuration-metadata.json
```

#### Database System, Orchestration and Security Rules

This plugin allows the generation of SQL scripts for registering (1) the providing and consuming systems with their respective services, (2) the Orchestrator Store rules without Authorization check and (3) the Intra-cloud Security rules with Authorization check. The following extracts of the different scripts display the results of executing the plugin over the example UML/SysML model.

<p align="center">
    <img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/1d9429c1-855a-4382-85b0-69cff18e83e6" width="426" height="386" hspace="25"/>
</p>

1. **system-service-registry.sql** inserts the systems' information onto *system_*, *service_definition* and *service_registry*.
<p align="center">
  <img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/b6a64847-4240-4a15-974a-6142326a6551" width="483" height="381"/>
</p>

2. **orchestrator-rules.sql** inserts the connections' information onto *orchestrator_store*.
<p align="center">
  <img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/eb6568bb-502f-4bc7-9dec-49eceec28dfa" width="780" height="144"/>
</p>

3. **security-rules.sql** inserts the connections' information onto *authorization_intra_cloud* and *authorization_intra_cloud_interface_connection*.
<p align="center">
  <img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/d1a3514b-2c17-4619-bac4-6438a6b08da7" width="715" height="207"/>
</p>

[//]: # (### Validation Plugins)

[//]: # (#### Database Validation)

[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/0f806e8a-77ca-4f28-921f-9551dff4ba70" width="20" height="20"/>)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/dac9be20-6683-436b-a16e-e6ec811f0493" width="20" height="20"/>)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/5666c16a-d257-4773-9ec6-182b95c0c3e2" width="20" height="20"/>)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/bb875dd3-db61-468e-8280-777b759b854a" width="20" height="20"/>)


[//]: # (#### Code Validation)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/c85b926e-c212-42d7-8a71-0f1d249ea1f3" width="20" height="20"/>)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/765f9b90-3cd3-4841-959c-bf6b1b838e80" width="20" height="20"/>)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/16e3e9ba-6a68-454a-a0b6-b5d4a4cd9940" width="20" height="20"/>)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/df64dd22-1c24-4723-a1fe-1547c0b74921" width="20" height="20"/>)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/fdd88066-ed5a-4b2d-8ad4-843bd09f63e2" width="20" height="20"/>)
[//]: # (<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/24e96ca5-619d-4c6d-9e3d-962ccd9cadeb" width="20" height="20"/>)

# Arrowhead Papyrus Utilities (Pre-Release v1.0.0-alpha)

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
This plugin offers a set of utilities for the systems' engineering automation of the **setup, deployment and validation** with the modeling help of Papyrus. The installation can be performed through Eclipse by selecting the released zip file of the plugin site in:
* Help -> Install New Software -> Add -> Archive

In the image below we can see that two plugin categories are offered. The installation process must be done in two separate steps, first the common utilities (*Step 1*) followed by the plugins (*Step 2*).

<p align="center">
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/5e2088c4-316c-4b0a-9a41-b59bef3116c3">
</p>


### UML/SysML Model
The release includes a UML model example of a distributed local cloud system. It is compound by a set of sensors, terminals, a middleware collector and a database. There are two interfaces offered *DataService* and *ConfigurationService* for respectively the obtention and update of data from sensor/database in different elements of the system and configuring the sampling ratio of the sensors.

In the left view we can see a high level description of the Local Clouds, System Design Descriptions (SysDDs) and Interface Design Descriptions (IDDs). In contrast, the right view shows the internal display or low level description of a Local Cloud where a set of Deployed Entities based on the previously defined SysDDs and IDDs are interconnected, displaying the consumer and provider interactions.

<p align="center">
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/e2d6af14-88b3-4cb4-b5f3-957a970081f8">
</p>

### Common Requirements Plugin

The requirements plugin includes the common libraries shared by the plugins, the [Arrowhead Papyrus Profile (APP)](https://github.com/eclipse-arrowhead/profile-library-sysml/) for the UML/SysML modeling with Papyrus Eclipse, a common API for parsing the UML model and the generated code into APX (approximate) objects of the APP stereotype implementations, the APX class definitions and other utilities for e.g. script execution in different OS.

### Setup Plugins
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/f374ae9b-2735-49af-91ab-572887abc888" width="20" height="20"/>

#### Service Registry
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/4de13128-6144-469c-9bb5-29cdd46ea93e" width="20" height="20"/>

### Deployment Plugins
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/52e4c6a8-6b61-4681-a222-8c5e00d65fd3" width="20" height="20"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/c0e9e4f1-4fb0-455a-a2be-390396d78b1d" width="20" height="20"/>

#### Local Cloud Core Systems
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/48982fdd-03d2-474c-a1c5-5701c81bbfa5" width="20" height="20"/>

#### Local Cloud Provider/Consumer Systems
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/dcc9f37c-6ef0-4796-9f08-583306e50b07" width="20" height="20"/>

#### Database Orchestration & Security Rules
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/1d9429c1-855a-4382-85b0-69cff18e83e6" width="20" height="20"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/73045a60-429f-4a1d-870f-32d5e8050a17" width="20" height="20"/>

### Validation Plugins
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/79cff281-e52d-4824-9209-ae42c980c517" width="20" height="20"/>

#### Database Validation
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/e1bd2556-8e0e-4b41-b0d3-0efc56432600" width="20" height="20"/>

#### Code Validation
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/c85b926e-c212-42d7-8a71-0f1d249ea1f3" width="20" height="20"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/765f9b90-3cd3-4841-959c-bf6b1b838e80" width="20" height="20"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/16e3e9ba-6a68-454a-a0b6-b5d4a4cd9940" width="20" height="20"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/df64dd22-1c24-4723-a1fe-1547c0b74921" width="20" height="20"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/fdd88066-ed5a-4b2d-8ad4-843bd09f63e2" width="20" height="20"/>
<img src="https://github.com/fernand0labra/arrowhead-papyrus-utilities/assets/70638694/24e96ca5-619d-4c6d-9e3d-962ccd9cadeb" width="20" height="20"/>

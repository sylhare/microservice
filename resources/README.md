# MicroService Workshop Java Implementation by Fred George
Copyright 2015-17 by Fred George. May be copied with this notice, but not used in classroom training.

Java is the most difficult to setup since there is a broad variety of Java
development tools (and versions). The following instructions are for installing
the code in IntelliJ by JetBrains. Adapt as necessary for your environment.

## IntelliJ setup
Import the sample code:
- Choose "Import Project"
- Select the pom.xml file (Maven import)
- Accept all the defaults in subsequent dialog boxes

Now tag the source and test directories:
    - File/Project Structure...
    - Select "Modules"
        -- Tag src directory as Sources
        -- Tag test directory as Tests
        -- Click "OK"

Confirm that everything builds correctly (and necessary libraries exist)

## IntelliJ execution
Each MicroService runs independently. So in IntelliJ, we need to setup up a
configuration for each.

For the Monitor service:
- Run/Edit Configurations...
- Click "+", and select Application
-- Name: Monitor_java
-- Main class: select "..." and click on the Monitor application
-- Program arguments: <IP address of RabbitMQ machine> <port of RabbitMQ>
-- JRE: Select an installed JRE (I used 1.8)
-- Click "OK"

For the Need service:
- Run/Edit Configurations...
- Click "+", and select Application
-- Name: Need_java
-- Main class: select "..." and click on the Need application
-- Program arguments: <IP address of RabbitMQ machine> <port of RabbitMQ>
-- JRE: Select an installed JRE (I used 1.8)
-- Click "OK"

Start each of the services:
- Run/Run.../Monitor_java
-- Service should start with message "[*] Waiting for messages. To exit press CTRL+C"
-- If you get a stack trace of any kind, look carefully to see what connection aspect 
is failing
- Run/Run.../Need_java
-- If you get a stack trace of any kind, look carefully to see what connection aspect 
is failing
-- Service should show JSON packets being sent
-- Monitor service should show JSON packets being received

## Next Steps
You're running MicroServices. Now let's write some more to run with these two.



# Microservice

Based on Fred George's [microservice workshop](https://github.com/fredgeorge/microservice_workshop)

- Bus with Rabbit MQ
- Microservices with Java

## About 

### Microservices

Microservices needs to be super tiny 
  - a couple of lines of codes (>100).
  
Microservices should only store the information they care about to make decision
  - You can have as many databases as miscroservice
  - A reporting database can be use to store and centralise the data, but not for operational work.  
  
If a microservice have only one listener, consider removing that communication from the Bus.
They could interface directly to each other before putting the output back on the Bus.

### Bus

The Bus stores all messages that goes through him
  - Get the data out of the bus!
  - Refer to external resources via URI.
  - Have unique IDs to differentiate packets
  - Communication is key, agree on the attributes the microservices will use.
  - Use filters on those attributes to solely get the information each services need

### Architecture

Microservices are relatively new and it comes with challenges. 
There are no pattern or Design book for them, however:
  - Fred George's River Pattern is used in this example.

Microservices add complexity and may not fill all needs. They provide
 - a good flexibility
 - opportunities to aggressively change the behaviour. 

They should follow the same framework
  - so they are easy to spawn and you don't need to worry about interfaces.

You test a microservice by launching into production right away, you will see the results instantaneously 
  - Cost of testing vs Cost of productions errors

## Get Started

Import the project, you can build it with Maven and the `pom.xml`.

Run the Rabbit MQ with docker:
```bash
docker-compose up
```

Run the micro services with as argument (locally with `127.0.0.1`):
```bash
<Rabbit MQ IP> <Rabbit MQ port>
127.0.0.1 5672
```



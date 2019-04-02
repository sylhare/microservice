# MicroService Workshop by Fred George
Copyright 2015-17 by Fred George. May be copied with this notice, but not used in classroom training.

## Before Class...
Before the class, attendees should install and compile the sample code for the language
they will be using. Libraries (DLL's, Gems, etc) can then be preloaded before class without
relying on spotty Internet connectivity in class.

Also since tools vary, detailed instructions for getting the code to run in your
chosen environment is difficult. It's best if you don't spend valuable class time
trying to get the code to run.

Occassionally attendee machines have been locked down in various ways by their company. 
Again, it is best to wrestle with these issues before class.

Optionally, RabbitMQ can be installed locally (natively or via Docker). The instructor
will provide instances of RabbitMQ if this is not feasible.

## Language Sample Setup
The starting code is designed to allow attendees to quickly begin designing and developing
new MicroServices. The following languages have sample code:
- Java
- C#
- Ruby

See the README.md files in each of the language-specific directories for setup instructions.

While some attendees have tried other languages (JavaScript and Clojure, among others),
they have never caught up to the rest of the class.

## RabbitMQ
RabbitMQ is used as the asynchronous message bus in the workshop. The instructor 
will have instances of RabbitMQ running under Docker on the instructor's
machine. This does require attendees to attach wirelessly to the instructor's machine,
and the Wifi load can be troublesome at times.

Alternatively if you have Docker installed locally, attendees can bring up their own
RabbitMQ instances. Please use the standard rabbitmq:management pull.

Also, please make sure to configure your system to allow external access to your own
RabbitMQ. Pairs share instances of RabbitMQ, and most OS will block the ports by default.

## Service operation
Regardless of language, the startup parameters for the services are the same:
- IP address of RabbitMQ
- Port of RabbitMQ

By default, RabbitMQ uses port 5672 for access and 15672 for its management console.

The Docker instances of the instructor's machine have remapped the ports for each
instance of RabbitMQ to:
- 56xx (xx = 73-85) for RabbitMQ access, and
- 156xx (xx = 73-85) for management console

For the admin console, RabbitMQ uses _guest_ for the user id and password.

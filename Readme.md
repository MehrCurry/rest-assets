POC for Storing Files in GridFS
===============================

This is a little REST (and Camel) Wrapper for an MongeDB GridFS.

The AssetResource class has some methods to store files into GridFS. Main goal is to experiment with different methods to access MongoDB.

Some methods loads the file into memory (which is bad for large files), some methods try to use the stream as is.

The is also a camel route which moves every file in the ./asset subdirectory to the DB.

You can start the application in your IDE as usual by executing the main class, in this case AssetRepositoryApplcation or from the command line
by executing the fatjar with java -jar.
 
The application expects a mongodb to be present (see application.properties).

I use a docker instance with boot to docker on my mac. I use docker image [tutum/mongodb](https://registry.hub.docker.com/u/tutum/mongodb/). You can create one with

    docker run -d -e MONGODB_PASSWORD=123456 -p 27017:27017 tutum/mongodb
    
The application registers an REST resource /assets. 

Monitoring can be done via Spring Boot Actuator REST URLS: /metrics, /health, ...

There is also [jolokia](https://jolokia.org/) installed to use [hawtio](http://hawt.io/).


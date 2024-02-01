# SpringBootMicroservice Full Architecture Project

**Services we are going to build:-**<br>
**1 Product Service** - Create and View Products , act as Product Catalog <br>
**2 Order Service** - Can Order Products <br>
**3 Inventory Service** - Can check if products are in stock<br>
**4 Notification Service** - Can send Notification , after order is placed<br>
**5** Order,Inventory and Notification are going to interact with each other<br>
**6** Synchronous and Asynchronous Communication <br>

Architecture Diagram - 

![img.png](img.png)


InterProcess Communication - This can be achieved by RestTemplate/Webclient. From Spring 5 onwards RestTemplate is under maintenance and WebClient has more modern APIs compartively.
It can be achieved in two ways:- 
1. Synchronous Communication - One service will make request to another service and will wait for the response.
2. Asynchronous Communication - Fire and forget when service make a request and doesn't care of response.

To Use WebClient we need dependency
`<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-webflux</artifactId>
</dependency>`

And then we will configure inside WebClientConfiguration where we will create bean of WebClient and class will be configured with
@Configuration annotation so that it can be scanned by ComponentScan and bean can be handle by Spring IOC Container

By Default webclient make all request as asynchronous. To make it as synchronous you have append with ".block()"
`webclient.get()
         .uri("URL")   
        .retrieve()
        .bodyToMono(ReturnResponseTypeFromRequestedUrl)
        .block();`
        

**SERVICE DISCOVERY**
The place where all the services will be at one place. and will be registered to discovery server
All request will go through discovery server and than redirected to respective target service.
By this we will avoid hardcoding any url and port number and can use application name in URI to request any service.
Also it will loadbalanced itself by just using `@LoadBalanced` to your webclient if you have multiple instance running on server<br>
<br>
_we need to add dependency :-_
Eureka server which is part of spring-cloud-netflix
and this server should run on standard port number - **8761**
<br>
<br>
**NOTE : **
To run various instance of any microservice we should not restrict port to specific so we can set `server.port = 0 `so that at runtime it will pick random portnumber based on availability
To run multiple instances change port to 0 and in edit configuration select-> allow multiple instance.
If we stop discovery service and rest other services are still Up than still our APIs will work as it will have internal copy of server running in client


<br>
<br>
**API GATEWAY- SPRING CLOUD GATEWAY**
<br>
API GATEWAY - which acts like entry point and will route  to respected service based on the rules configured.<br>
**DEFAULT PORT -  9191**
<br>
- Routing based on Request Header<br>
- Authentication<br>
- Security<br>
- Load Balancing<br>

**Features of SPRING CLOUD GATEWAY** <br>
- Able to match routes on any request attributes
- predicates and filters are specific to routes
- Circuit breaker Integration
- Spring cloud discovery client integration
- Request Rate Limiting
- Easy to write predicates and filters
- Path rewriting

<br>
Dependency used :-

  ` <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>`

![img_1.png](img_1.png)

<br>
<br>
Route - basic build block of API GATEWAY. Its is defined by an ID,destination URI, collection of predicates, and a collection of filters.A route is matched if the aggregate predicate is true
<br>
Predicate - This is a java 8  function predicate . This let you match on anything from the HTTP request  such as headers or parameters <br>
Filter - These are the instances of Gateway Filter  that have been constructed with a specific factory. Here you can modify requests and responses before or after sending the downstream request.<br>

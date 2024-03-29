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

----------------------
InterProcess Communication - This can be achieved by RestTemplate/Webclient. From Spring 5 onwards RestTemplate is under maintenance and WebClient has more modern APIs compartively.
It can be achieved in two ways:- 
1. Synchronous Communication - One service will make request to another service and will wait for the response.
2. Asynchronous Communication - Fire and forget when service make a request and doesn't care of response.

To Use WebClient we need dependency

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

And then we will configure inside WebClientConfiguration where we will create bean of WebClient and class will be configured with
@Configuration annotation so that it can be scanned by ComponentScan and bean can be handle by Spring IOC Container

By Default webclient make all request as asynchronous. To make it as synchronous you have append with ".block()"

    webclient.get()
         .uri("URL")   
        .retrieve()
        .bodyToMono(ReturnResponseTypeFromRequestedUrl)
        .block();
        
----------------------

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

----------------------

<br>
**API GATEWAY- SPRING CLOUD GATEWAY**
<br>
API GATEWAY - which acts like entry point and will route  to respected service based on the rules configured.<br>
**DEFAULT PORT -  9191**
<br>

- Routing based on Request Header
- Authentication
- Security
- Load Balancing

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

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>


![img_1.png](img_1.png)

<br>
<br>
Route - basic build block of API GATEWAY. Its is defined by an ID,destination URI, collection of predicates, and a collection of filters.A route is matched if the aggregate predicate is true
<br>
Predicate - This is a java 8  function predicate . This let you match on anything from the HTTP request  such as headers or parameters <br>
Filter - These are the instances of Gateway Filter  that have been constructed with a specific factory. Here you can modify requests and responses before or after sending the downstream request.<br>
<br>
**Note**:- We should not implement security logic in gateway as it will break the basic architecture flow of GATEWAY. Instead we should create new microservice which will take care of all authentication and validation
and return to Gateway whether to route successfully to target or return exception to client.
<br>

----------------------

**SPRING SECURITY**
Authentication and authorization are critical components of microservice security. Use Spring Security to implement these functionalities to ensure that only authorized users or services can access your microservices.
<br>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

PasswordEncoder - Is used to encrypt the password while saving in DB <br>
JWT HELPER - we will create helper component to handle JWT(Json Web Token) Validation, Creation. <br>
**Dependency used to access jwt** - <br>
<!--JWT-->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>

<br>

Secret of 32 bit generate a token to create a token these are params required -
- claims ( header, payload and signature)
- set subject -  username
- issued at -- current time timestamp
- expiration -- current time +timestamp
- sign with - type of algo using to encrypt token

Spring Security - It uses UserDetailsService to load user information by using loadUserByUserName method
Also it will be required to generate token and to validate token

**Steps to implement jwt token:**

1) Make sure spring-boot-starter-security is there in pom.xml
2) Create Class JWTAthenticationEntryPoint that implement AuthenticationEntryPoint. Method of this class is called whenever as exception is thrown due to unauthenticated user trying to access the resource that required authentication.
3) Create JwtHelper  class ,This class contains method related to perform operations with jwt token like generateToken, validateToken etc.
4) Create JWTAuthenticationFilter that extends OncePerRequestFilter and override method and write the logic to check the token that is comming in header. We have to write 5 important logic
- Get Token from request
- Validate Token
- GetUsername from token
- Load user associated with this token
- set authentication
5) Configure spring security filter chain in AuthConfig file

**GATEWAY FILTER** -
 
Create a Pre Filter: Implement a pre filter to intercept incoming requests and validate the token.
<br>
Add Filter in your application.yml to all the routes and do not include in the Auth-security-service.
----------------------
**CIRCUIT BREAKER**

<br>
1) Closed State - When microservices run and interact smoothly, circuit breaker is Closed. It keeps continuously monitoring the number of failures occurring within the configured time period. If the failure rate exceeds the specified threshold, Its state will change to Open state. If not, it will reset the failure count and timeout period.
2) Open State - During Open state, circuit breaker will block the interacting flow between microservices. Request callings will fail, and exceptions will be thrown. Open state remains until the timeout ends, then change to Half_Open state.
3) Half Open - In Half_Open state, circuit breaker will allow a limited of number requests to pass through. If the failure rate is greater than the specified threshold, it switches again to Open state. Otherwise, it is Closed state.

![img_2.png](img_2.png)

- Dependency Used <br>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
        </dependency>

@CircuitBreaker(name = "anyname" , fallbackMethod= "method_name")
It can be in service or controller where you are making a call


![img_2.png](img_2.png)

<br> 

--------------------------------------------

**DISTRIBUTED TRACING** is a method used to profile and monitoring applications. It helps to pinpoint where failure occurs and what cause poor performance
We used to track the logs in distributed systems by spanID and TraceID.
TraceId will remain same through out the request and has a unique traceID and spanID is given to individual microservice. It's a segment of a trace within a service.
One service can output multiple spans to a single trace.

![img_3.png](img_3.png)
**Dependency Used-**
      
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-actuator</artifactId>
      </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing-bridge-brave</artifactId>
        </dependency>
        <dependency>
            <groupId>io.zipkin.reporter2</groupId>
            <artifactId>zipkin-reporter-brave</artifactId>
        </dependency>

Micrometer dependency is used in place of Sleuth which will help to trace the logs
and Zipkin is a distributed tracing system. It helps gather timing data needed to troubleshoot latency problems in microservice architectures. It manages both the collection and lookup of this data. 

and to run Zipkin we need to run the below command:-

    docker run -d -p 9411:9411 openzipkin/zipkin

In Application.yml, we need to below properties to enable & see traces in zipkin server 
and trace percentage from range 1 to 0 where 1 is 100%

    management:
      tracing:
        enabled: true
        sampling:
          probability: 1.0
----------------------
**EVENT DRIVEN ARCHITECTURE**
Whenever we are doing asynchronous communication , where service will fire request and forget. This type of asynchornous communication can achieve using Event Driven Architecture.
EDA is a design pattern for building scalable and loosely coupled systems by enabling communication between different components through events.

**APACHE KAFKA**
It's a distributed Messaging Event Service. It provides a `kafkaTemplate` to send messages. It also provides support for Message driven POJOs with `@KafkaListener`and a `listener container`
This libraries promote the use of dependency Injection and declarative.


The Apache Kafka architecture is designed to provide a highly scalable, fault-tolerant, and distributed messaging system.

Dependency Used

        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>

**KAFKA ARCHITECTURE**

![img_4.png](img_4.png)

1. Producer:

Producers are applications that produce data and publish it to Kafka topics.
They send messages to Kafka brokers over the network.
Producers can choose which topic to publish to and optionally specify a partition or key for message routing using Kafka template.

2. Broker:

Brokers are individual Kafka server instances responsible for storing and serving messages.
They receive messages from producers, store them on disk, and serve them to consumers.
Kafka brokers operate in a distributed cluster, allowing for scalability and fault tolerance.
They can be thought of as individual nodes in the Kafka cluster.

3. Consumer:

Consumers are applications that subscribe to Kafka topics and consume messages.
They pull messages from Kafka brokers and process them according to their application logic.
Consumers can be part of a consumer group, allowing for parallel message processing and load balancing across multiple instances.

4. Topic:

A topic is a category or stream name to which records (messages) are published by producers.
Each topic can have multiple partitions, which allows for parallel processing and scalability.
Topics can be created and configured dynamically.

5. Partition:

A partition is a unit of parallelism and scalability within a topic.
Each partition is an ordered, immutable sequence of messages.
Messages within a partition are assigned a unique offset, starting from 0.
Partitions allow Kafka to distribute and parallelize message processing across multiple brokers and consumers.

6. ZooKeeper:

ZooKeeper is a distributed coordination service used by Kafka for managing and coordinating broker nodes.
It helps with tasks such as leader election, configuration management, and maintaining metadata about the Kafka cluster.
ZooKeeper is a critical component of Kafka's architecture but is gradually being replaced by the Kafka Controller in newer versions of Kafka.

**KAFKA CONFIGURATION**

    kafka:
      template:
        default-topic: notificationTopic
      consumer:
        bootstrap-servers: localhost:9092
        group-id: notificationId
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        properties:
          spring.json.type.mapping:
            event: com.shutterbug.notificationservice.event.OrderPlaceEvent

CODE
 To Publish message

    kafkaTemplate.send("notificationTopic", new OrderPlaceEvent(order.getOrderNumber()));
To Consume message

    @KafkaListener(topics = "notificationTopic", groupId = "notificationId")
      public void handleMessage( OrderPlaceEvent orderPlaceEvent) {
      // send out email or slack
      log.info("Received Order from Kafka "+ orderPlaceEvent.getOrderNumber());
      }

-----------------------------------

FEIGN CLIENT

The Feign is a declarative HTTP web client developed by Netflix which replace RestTemplate and WebClient for anytype of http request. If you want to use Feign , create an interface and annotate it.

**Dependency Used** :-

         <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
         </dependency>
Steps:-
1. Enable Feign Client in your main App by using annotation @EnableFeignClients
2. Create an interface and annotate with @FeignClient(name = "Application_Name_Microservice")
3. Within interface create your method with annotation based on your request type like @GetMapping("path") , @PostMapping("path") with path 
4. Inject your interface in your service to use the method to call 


-------------------------------

**CONFIG SERVER**

It follows client-server architecture with which we will externalise all our local configuration in server/cloud. Some common configurations which has been repeated can be moved to config server. 
Central Management  for configuration via GIT,SVN etc.

Steps: 
1. We will create config server service who will be responsible to get all the configurations from server and return to the respective microservice client

**Dependency Used to Create Config Server** 

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>

2. We will add annotation with @EnableConfigServer
3. We will add  following properties in application.yml 
        
           cloud:
             config:
               server:
                 git:
                   uri: https://github.com/rjtkumar007/SpringMicroServiceAppConfig.git
                   clone-on-start: true
          
4. Now to retrieve configuration in services we will include cloud config client depenedency to utilize the properties while runtime

Dependency used

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-client</artifactId>
        </dependency>

Add below properties in your application.yml to fetch from cloud config server 
            
            spring: 
              config:
                import: configserver: http://localhost:8085


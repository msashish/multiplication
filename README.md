## Goal

    As a user of the application, I want to solve a random multiplication problem using mental calculation so I exercise my brain.

    Target: Present a challenge (multiplication challenge to start with) everyday to user for training their brain.
    
## Monolith Development steps

    1.	Create a basic service with business logic (services in spring)
    
    2.	Create a basic API (spring controller via rest api) to access this service.
    
    3.	Create a basic web page to ask the users to solve that calculation.
    
    Let’s define these three business entities
        Challenge: Contains the two factors of a multiplication challenge [Challenge domain]
        User: Identifies the person who will try to solve a Challenge [User domain]
        Challenge Attempt: Represents the attempt from a User to solve the operation from a Challenge [Challenge domain]
    Finding domain boundaries (bounded contexts):
        User
        Challenge
        
    4. Coding steps:
        Create domains as packages - challenge and user
        Create domain models first - Challenge.java, User.java, ChallengeAttempt.java
        For ChallengeAttempt, use reference to link with User but use factors directly and no reference to Challenge
        We use a DTO to model the data needed from the presentation layer to create an attempt
                    Write ChallengeAttempDTO
                    This will be used in verification service
                    DTO can also be used in UTs as "given" and then passed as arguments for "when"
        Write Services 
            Generate challenges - ChallengeGeneratorService  Interface, ChallengeGeneratorServiceImpl
            Verify Challenges - ChallengeService Interface and ChallengeServiceImpl
            Write ChallengeGeneratorServiceTest
            Write ChallengeServiceTest 
        Write API layer i.e Controllers
            Define REST endpoints:
                GET /challenges/random will return a randomly generated challenge.
                POST /attempts/ will be our endpoint to send an attempt to solve a challenge.
            Write empty ChallengeAttemptController
            Write ChallengeAttemptControllerTest for valid and invalid scenario and let it fail
            Fill the methods in ChallengeAttemptController 
                Change ChallengeAttemptDTO to add validation constraints. Then, add @Valid in ChallengeAttemptController
        Start application and test using HTTPie
            
            
        
## Build and run application

    mvn clean install --settings /Users/sheelava/.m2/settings.xml -Dmaven.repo.local=/Users/sheelava/.m2/local_repository  -Dmaven.test.skip=true  
    mvn spring-boot:run  -Dmaven.repo.local=/Users/sheelava/.m2/local_repository
    
    Test using HTTPie
        http localhost:8080/challenges/random
        http -b :8080/challenges/random
        http POST :8080/attempts factorA=58 factorB=92 userAlias=moises
        http POST :8080/attempts factorA=58 factorB=92 userAlias=moises guess=-400
        http POST :8080/attempts factorA=58 factorB=92 userAlias=moises guess=5336
    
## Run tests
    mvn test -Dtest=ChallengeServiceTest    
    mvn test -Dtest=ChallengeAttemptControllerTest   
    
    
## Key Learnings:

    1) Overall design using Spring
         Services - Have business logic coded. Acts as service layer
         Controllers - No business logic. Acts as API layer.
         
    2) @RestController is a combination of @Controller and @ResponseBody, which instructs Spring to put the result of 
        this method as the HTTP response body. As a default in Spring Boot the response will be serialized as
        JSON and included in the response body.
        
    3) Whenever we want to pass the body of a request to our method, we use the @RequestBody annotation. 
          If we use a custom class, Spring Boot will try to deserialize it, using the type passed to the method
        
    4) Sample Design of API layer using RestController    
         Request example: GET http://ourhost.com/challenges/5?factorA=40
            - GET is the HTTP verb.
            - http://ourhost.com/ is the host where the web server is running. In this example, the application is serving from the root context, /.
            - /challenges/ is an API context or collection created by appplication, to provide functionalities around this domain.
            - /5 is called a path variable. In this case, it represents the Challenge object with identifier 5.
            - factorA=40 is a request parameter and its value.
               
         Inside the method of RestController we can access path variable using @PathVariable and request parameter using @RequestParameter
           
    5) @RequiredArgsConstructor creates a constructor with a private and final variable as argument.
        Spring uses dependency injection to find a bean that implements the interface of variable and wire it to controller
        @Slf4j creates a logger named log 
        
    6) Testing a Spring controller requires a different approach as there is a web layer inbetween
        We want to test features - validation, request mapping, or error handling, which are configured by us but provided by Spring Boot. 
           
           Without running the embedded server:
               We can use @SpringBootTest without parameters or, even better, @WebMvcTest to instruct Spring to selectively load only the 
               required configuration instead of the whole application context. 
               Then, we simulate requests with a dedicated tool included in the Spring Test module, MockMvc.
           
           Running the embedded server:
               In this case, we use @SpringBootTest with its webEnvironment parameter set to RANDOM_PORT or DEFINED_PORT. 
                Then, we have to make real HTTP calls to the server. Spring Boot includes a class TestRestTemplate with some 
                useful features to perform these test requests.
    
    7) @AutoConfigureJsonTesters tells Spring to configure beans of type JacksonTester for some fields we declare in the test. 
    In our case, we use @Autowired to inject two JacksonTester beans from the test context. Spring Boot, when instructed via 
    this annotation, takes care of building these utility classes. 
    A JacksonTester may be used to serialize and deserialize objects using the same configuration (i.e., ObjectMapper) as the app would do in runtime.
    
## Useful Links

    Book: https://learning.oreilly.com/library/view/learn-microservices-with/9781484261316/
    
    spring-boot github:
        https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-starters/spring-boot-starter-web/build.gradle
        https://tpd.io/starter-web-deps 
    
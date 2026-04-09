# Corneast
Corneast is an AP distributed token middleware.  

## Architecture

```
                                                                                                                                                 
                                          Architecture of Corneast                                                                               
                                                                                                                                                 
                                          drawn by Alioth Null                                                                                   
                                                                                                                                                 
                                                                                                                                                 
                                                                                                                                                 
                                                                                                                                                 
      ┌────────┐                                     ┌──────────────────────────────────────────────┐                                            
      │        │                                     │                      core                    │     ┌──────────────────────────────┐       
      │        │                                     │ ┌─────────┐ ┌──────────────────────────────┐ │     │                              │       
      │        │                                     │ │         │ │ ┌──────┐  ┌──────┐  ┌──────┐ │ │     │                              │       
      │        │                                     │ │         │ │ │      │  │      │  │      ┼─┼─┼─────►       idempotent redis       │       
      │        │                                     │ │         │ │ │child │  │child │  │child │ │ │     │                              │       
      │        │                                     │ │         ┼─┼─►handler──►handler──►handler │ │     │       redis cluster          │       
      │        │                                     │ │         │ │ │      │  │      │  │      │ │ │     │                              │       
      │        │                                     │ │         │ │ │      │  │      │  │      ◄─┼─┼─────┼       master-slave           │       
      │        │                                     │ │         │ │ │      │  │      │  │      │ │ │     │                              │       
      │        │                                     │ │         │ │ └──────┘  └──────┘  └───┬──┘ │ │     │                              │       
      │        │                                     │ │         │ │          route┌─────────┘    │ │     │                              │       
      │        │          tcp, protobuf              │ │ netty   │ │     ┌─────────┼─────────┐    │ │     │                              │       
      │ client ┼─────────────────────────────────────► │         │ │ ┌───▼──┐  ┌───▼──┐  ┌───▼──┐ │ │     └──────────────────────────────┘       
      │        │                                     │ │bossGroup│ │ │      │  │      │  │      │ │ │     ┌──────────────────────────────┐       
      │        │                                     │ │         │ │ │      │  │      │  │      │ │ │     │                              │       
      │        │                                     │ │         │ │ │stra- │  │stra- │  │stra- │ │ │     │                              │       
      │        │                                     │ │         │ │ │tegy  │  │tegy  │  │tegy  │ │ │     │      storage redis           │       
      │        │                                     │ │         │ │ │      │  │      │  │      │ │ │     │                              │       
      │        │                                     │ │         │ │ │      │  │      │  │      │ │ │     │      horizontal scaling      │       
      │        │                                     │ │         │ │ └──┬───┘  └───┬──┘  └───┬──┘ │ │     │                              │       
      │        │                                     │ │         │ │    │          │         │    │ │     │      master-slave            │       
      │        ◄───────────────────┐                 │ │         │ │    └──────────┴─────────┴────┼─┼─────►                              │       
      │        │                   │                 │ │         │ │ ┌──────────────────────────┐ │ │     │      sentinel                │       
      │        │                   │                 │ │         │ │ │       child handlers     ◄─┼─┼─────┼                              │       
      │        │                   │                 │ │         │ │ └──────────────────────────┘ │ │     │                              │       
      │        │                   │                 │ └─────────┘ └──────────────────────────────┘ │     └──────────────────────────────┘       
      └────────┘                   │                 └─────────────▲────────────────────────────────┘                                            
                                   │                               │                                                                             
                                   │                               │                                                                             
                                   │                               │                                                                             
                                   │                               │                                                                             
                           ┌───────▼───────────────────────────────▼───┐                                                                         
                           │                                           │                                                                         
                           │                                           │                                                                         
                           │                                           │                                                                         
                           │               eureka server               │                                                                         
                           │                                           │                                                                         
                           │                                           │                                                                         
                           │                                           │                                                                         
                           └───────────────────────────────────────────┘                                                                         
                                                                                                                                                 
                                                                                                                                                 
                                                                                                                                                 
                                                                                                                                                 
```

## Building
Run `./mvnw clean install -DskipTests` from the root directory of the project.  

The built jar files will be located in the `target` directory of each module.  

## Running
```bash
java -jar corneast-<module>-<version>.jar
```

## Contributing
Contributions to Corneast are welcome!  

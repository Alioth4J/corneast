## corneast-client
### Usage
#### Create Request Objects
```java
RequestProto.RequestDTO requestDTO = new CorneastRequest(CorneastOperation.<operation>, <id>, <key>[, <tokenCount>]).instance;
```
See `CorneastRequest` for more constructor overloads and request object getting methods.  

You can also use `CorneastRequestBuilder`:  
```java
RequestProto.RequestDTO requestDTO = CorneastRequestBuilder.newBuilder()
        .setType(CorneastOperation.<operation>)
        .setId(<id>)
        .setKey(<key>)
       [.setTokenCount(<tokenCount>)]
        .build();
```

#### Create Client Object
```java
CorneastConfig config = new CorneastConfig();
config.setHost(<host>);
config.setPort(<port>);
Corneast[B|N|A]ioClient corneast[B|N|A]ioClient = Corneast[B|N|A]ioClient.of(config);
```

#### Send Requests and Receive Responses
```java
ResponseProto.ResponseDTO responseDTO = null;
try {
    responseDTO = corneastBioClient.send(registerReqDTO);
} catch (IOException e) {
    // handle exception
}
```

```java
ResponseProto.ResponseDTO responseDTO = null;
try {
    responseDTO = corneastNioClient.send(registerReqDTO);
} catch (IOException e) {
    // handle exception
}
```

```java
CompletableFuture<ResponseProto.ResponseDTO> responseFuture = null;
ResponseProto.ResponseDTO responseDTO = null;
try {
    responseFuture = corneastAioClient.send(registerReqDTO);
    responseDTO = responseFuture.get();
} catch (IOException | InterruptedException e) {
    // handle exception
} catch (ExecutionException e) {
    // handle exception
}
``` 

### Details
#### Operations
- REGISTER
- REDUCE
- RELEASE
- QUERY

#### Id
For idempotence.  

Setting `id` to `null` or `""` to disable idempotence.  

#### Key
A unique string stands for an item.  

#### TokenCount
The total number of a key that represents a kind of tokens.  

### Etc
#### Write Request Object to Files
Generate while `mvn test` in `ProtobufRequestGeneratorTests`.  

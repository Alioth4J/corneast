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

#### Send Requests And Receive Responses
```java
ResponseProto.ResponseDTO responseDTO = CorneastSocketClient.send(RequestProto.RequestDTO);
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

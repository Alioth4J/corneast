## corneast-client
### Usage
#### Create Request Objects
```java
new CorneastRequest(CorneastOperation.<operation>, <id>, <key>[, <tokenCount>]).instance;
```

See `CorneastRequest` for more constructor overloads and request object getting methods.  

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

# vertx-reactive-server 

Written in vert.x

These are some of vertx io streaming examples. Package:

* io.example.vertx.http - gives simple HTTP client/server streaming with HTTP/1.0. This does not use chunked encoding hence you need to set HTTP header content-length.

* io.example.vertx.chunked - gives sample client/server implementation of streaming client/server. Here the clients sends streaming data with HTTP/1.1 transfer-encoding set to chunked. Data is sent in a series of "chunks". Senders can begin transmitting dynamically-generated content before knowing the total size of that content. The size of each chunk is sent right before the chunk itself so that the receiver can tell when it has finished receiving data for that chunk. The data transfer is terminated by a final chunk of length zero.

 
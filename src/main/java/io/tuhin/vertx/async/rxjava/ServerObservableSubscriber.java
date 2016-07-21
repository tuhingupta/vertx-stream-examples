
package io.tuhin.vertx.async.rxjava;


import io.tuhin.vertx.util.Runner;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Tuhin Gupta
 * 2016 
 */
public class ServerObservableSubscriber extends AbstractVerticle {
	

	  public static void main(String[] args) 
	  {
	  
		  Runner.runExample(ServerObservableSubscriber.class);
	  
	  }


	/* (non-Javadoc)
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	  public void start() throws Exception {
		
		HttpServer server = vertx.createHttpServer();
		
		System.out.println(Thread.currentThread());
		
		//observables

		Observable<HttpServerRequest> observable = server.requestStream().toObservable();
		
		//work on input request using map 
		Observable<HttpServerRequest> mappedObservable = observable.map(new Func1<HttpServerRequest, HttpServerRequest>() {
			    @Override public HttpServerRequest call(HttpServerRequest req) {
			        try {
			            
			        	System.out.println("mappedObservable -> Do something kind of data manupulation to input request.");
			        } catch (Exception e) {
			            // this exception is a part of rx-java
			            
			        }
			        return req;
			    }
			});
		

		
		//subscribers to send response back 
		mappedObservable.subscribe(req -> {
								HttpServerResponse response = req.response();
								String contentType = req.getHeader("Content-Type");
			
								if(contentType != null){
									response.putHeader("Content-Type", contentType);
								}
								response.setChunked(true);
								
								//handling data event and end on request
								req.toObservable().
									subscribe(
											data -> {
												response.write(data);
												
											},
											error -> response.setStatusCode(500).end(),
											
											() -> response.end());
									});
		
		
		server.listenObservable(8998).
			subscribe(
					res->System.out.println("Listening on port 8998"), 
					error->System.out.println(error.getMessage()),
					()->{
						
						System.out.println("listening start completed"+Thread.currentThread());
					}
			);
		
		


	  }//start

}



package io.tuhin.vertx.async.rxjava;



import io.tuhin.vertx.util.Runner;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;

/**
 * @author Tuhin Gupta
 * 2016 
 */
public class AsyncServer extends AbstractVerticle {
	

	  public static void main(String[] args) 
	  {
	  
		  Runner.runExample(AsyncServer.class);
	  
	  }


	/* 
	 */
	@Override
	  public void start() throws Exception {
		
		System.out.println("Server started at localhost 8998");
		
		HttpServer server = vertx.createHttpServer();
		
		vertx.createHttpServer().requestHandler(request -> {
			
			System.out.println("vertx main "+Thread.currentThread());
			
			//here a separate thread would run, an alternate way is to use worker verticle
			//this would be a worker thread and not the main kernel vertx thread.
			//so main thread is still non blocking but we can execute blocking code using executeBlocking
			vertx.<String>executeBlocking(future -> {
				
					//This is an imaginary blocking operation
					String result = (new NestedThreadClass()).doThread();
					System.out.println("executeBlocking "+Thread.currentThread());
				
					//complete the async operation
					future.complete(result);
					
			}, result -> {//when the blocking action is complete and time to process response now.
				if(result.succeeded()){
					request.response().putHeader("content-type", "text/plain").end(result.result());
				} else {
					result.cause().printStackTrace();
					request.response().setStatusCode(500).end();
				}
			});
		}).listen(8998);

		
		


	  }//start
	
	/**
	 * 
	 * @author Tuhin Gupta
	 * 
	 * just a rock -\o/-
	 *
	 */
	public class NestedThreadClass{
		
		public String doThread(){
			
			try{
				System.out.println("NestedThreadClass "+Thread.currentThread());
				Thread.sleep(5000);
				
			}catch(Exception e){
				
			}
			
			return "Thread slept for 5000ms";
		}
	}

}


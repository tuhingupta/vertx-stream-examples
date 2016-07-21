package io.tuhin.vertx.chunked;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.tuhin.vertx.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;


/**
 * @author Tuhin Gupta
 * April 2016
 * 
 * Vertx.io server to recieve chunked request. 
 * See that header has Transfer-Encoding value set as chunked.
 * Example: Transfer-Encoding : chunked 
 */
public class StreamingServer extends AbstractVerticle {
	

	  public static void main(String[] args) 
	  {
	  
		  Runner.runExample(StreamingServer.class);
	  
	  }

	  
	  static String pattern1 = "{";
	  static String pattern2 = "}";
		
	  static Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));

	  /* (non-Javadoc)
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	  public void start() throws Exception {
		
		System.out.println("Server started at localhost:9977 ...");
		
		
		
	    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
	    	
	    	long byteswritten = 0;
	    	
			
			List<String> results = new ArrayList<String>();
			String extraBytes = null;
			
	    	int start = -1;
	    	int end = -1;

			@Override
			public void handle(HttpServerRequest request) {

				
				
				request.handler(new Handler<Buffer>(){


					@Override
					public void handle(Buffer buffer) 
					{
						
						/*
						 * Headers returned:-
						 * 
						 * Host : localhost:9977
						 * Transfer-Encoding : chunked
						 * 
						 * 
						for (String key : request.headers().names()) 
						{
							System.out.println(key+" : "+request.getHeader(key));
							
						}
						*/
						
						
						byteswritten += buffer.length();
						String inputString = extraBytes == null ? buffer.toString(): extraBytes + buffer.toString();
						extraBytes = null;
						
						//using string patterns
						Matcher m = p.matcher(inputString);
						
						
						
						while (m.find()) {
							if(start < 0){
								if(m.start() > 0)
									start = m.start();
							}

							
							try{
							
							//print output	
							System.out.println(m.group());
								
							}catch(Exception ex){
								ex.printStackTrace();
								
							}
							
							
							end = m.start() + m.group().length(); 

						}//while
						
						
						if(end >= 0 && end < inputString.length()){
							extraBytes = inputString.substring(end, inputString.length());
						}
					
						
						
					}
					
				
				});//request.handler
				
				request.endHandler(new Handler<Void>() {

					@Override
					public void handle(Void event) {
						
						System.out.println("...end handler.");
						
						
						
						try {
							
							
							request.response().setStatusCode(202).setStatusMessage("bytes written " + byteswritten);
							request.response().end();
							extraBytes = null;
							results = new ArrayList<String>();
							byteswritten = 0;
							
						
						} catch (Exception e) {
							
							e.printStackTrace();
						}finally{

							extraBytes = null;
							results = new ArrayList<String>();
							byteswritten = 0;
						}
						
						
						
					}//handle
				
				});
				
				
			}
  	
	    	
	    }).listen(9977);
	  }

}

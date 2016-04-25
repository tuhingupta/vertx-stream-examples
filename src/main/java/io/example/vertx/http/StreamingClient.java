package io.example.vertx.http;


import io.example.vertx.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.streams.Pump;

/**
 * @author Tuhin Gupta
 * April 2016
 * 
 * Example of vertx.io HTTP/1.0 non-chunked client. 
 * This requires that content-length be sent with the request, so that server know when the request is complete.  
 */
public class StreamingClient extends AbstractVerticle {
	
	
	  static String FILE_NAME = null;

	  
	  public static void main(String[] args) {
		  
		FILE_NAME = "upload.json";  
	    Runner.runExample(StreamingClient.class);
	  }

	  
	  //{"transaction_date":"2015-12-29","account_number":"201418777876948","transaction_amount":"803.02","last_name":"Willis","id":"405067","first_name":"Billy","email":"bwillis10@spotify.com"}
	 // {"transaction_date":"2015-05-30","account_number":"4905709701528306163","transaction_amount":"224.86","last_name":"Lee","id":"405068","first_name":"Douglas","email":"dleep6@posterous.com"}

	  public void start() {

		  HttpClientRequest request = vertx.createHttpClient(new HttpClientOptions()).put(9977, "localhost", "", resp -> {
		      System.out.println("Response " + resp.statusCode());
		      System.out.println("Response " + resp.statusMessage());
		      System.exit(0);
		    });


		    FileSystem fs = vertx.fileSystem();

		    fs.props(FILE_NAME, ares -> {
		      FileProps props = ares.result();
		      System.out.println("props is " + props);
		      
		      //this is an example of un-chunked stream over HTTP, 
		      //hence set content-length header.

		      long size = props.size();
		      request.headers().set("content-length", String.valueOf(size));
		      
		      fs.open(FILE_NAME, new OpenOptions(), ares2 -> {
		        AsyncFile file = ares2.result();
		        Pump pump = Pump.pump(file, request);
		        file.endHandler(v -> {
		        	request.end();
		        });
		        pump.start();
		      });
		    });

		  
	  }
	 
	}

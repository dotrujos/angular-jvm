package com.gabrielaraujo.angular;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.gabrielaraujo.angular.controller.NgControllerBootstrap;
import com.sun.net.httpserver.*;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class NgBootstrap {
	public static void start(List<Class<?>> controllers) {
		try {
			int port = 8080;
			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
			log.info("Starting Ng4j http server at {}", port);
			
			Path source = Paths.get("").toAbsolutePath();
			Path resources = source.resolve("src/main/resources"); 
			
			log.info("Pages context folder: {}", resources.toString());
			
			NgControllerBootstrap.SINGLETON.serve(server, controllers, resources);
			
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}

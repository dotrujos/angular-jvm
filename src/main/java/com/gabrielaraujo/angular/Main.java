package com.gabrielaraujo.angular;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

import com.gabrielaraujo.angular.controller.NgController;
import com.gabrielaraujo.angular.controller.NgPath;
import com.gabrielaraujo.angular.controller.NgResource;
import com.gabrielaraujo.angular.controller.NgResponse;

@NgController
public class Main {
	
	public static void main(String[] args) {
		var main = new Main();
		List<Class<?>> clazzes = List.of(Main.class);
		NgBootstrap.start(clazzes);
		
	}
	
	@NgResource
	@NgPath(path = "/index.ng")
	public NgResponse doGet() {
		var viewBag = new HashMap<String, String>();
		viewBag.put("helloWorld", "Hello World!");
		viewBag.put("date", OffsetDateTime.now().toString());
		
		return NgResponse.of("index.ng", viewBag);
	}
}

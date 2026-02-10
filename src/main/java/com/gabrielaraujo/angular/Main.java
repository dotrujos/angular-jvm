package com.gabrielaraujo.angular;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.HashMap;

public class Main {
	public native String replaceVariableOcurrencies(String html, String[] keys, String[] values);
	public String replaceVarialeOcurrencies(String html, HashMap<String, String> variables) {
		if (html == null || variables == null || variables.isEmpty()) {
            return html;
        }

        String[] keys = variables.keySet().toArray(new String[0]);
        String[] values = variables.values().toArray(new String[0]);

        return replaceVariableOcurrencies(html, keys, values);
	}
	
	static {
		System.loadLibrary("native");
	}
	
	public static void main(String[] args) {
		Main thisObj = new Main();
		try {
			String file = Files.readString(Path.of("/home/gabrielaraujo/Documentos/Developing/Angular-Server-Side/angular/src/main/resources/index.ng"));
			
			HashMap<String, String> variables = new HashMap<String, String>();
			variables.put("helloWorld", "hello world!");
			variables.put("date", OffsetDateTime.now().toString());
			
			file = thisObj.replaceVarialeOcurrencies(file, variables);
			System.out.println(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}

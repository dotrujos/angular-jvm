package com.gabrielaraujo.angular.controller;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
public class NgControllerBootstrap {
    public static NgControllerBootstrap SINGLETON = new NgControllerBootstrap();
    
    static {
        System.loadLibrary("native");
    }
    
    public void serve(HttpServer server, List<Class<?>> clazzes, Path templateDirectory) {
        clazzes = this.validateNgControllers(clazzes);
        
        if (clazzes.isEmpty()) {
            log.error("No valid controllers found. Stopping.");
            return;
        }
        
        for (Class<?> clazz : clazzes) {
            registerControllerRoutes(server, clazz, templateDirectory);
        }
    }
    
    public native String replaceVariableOcurrencies(String html, String[] keys, String[] values);
    
    public String replaceVariableOcurrencies(String html, HashMap<String, String> variables) {
        if (html == null || variables == null || variables.isEmpty()) {
            return html;
        }

        String[] keys = variables.keySet().toArray(new String[0]);
        String[] values = variables.values().toArray(new String[0]);

        return replaceVariableOcurrencies(html, keys, values);
    }
    
    private void registerControllerRoutes(HttpServer server, Class<?> clazz, Path templateDirectory) {
        try {
            Object controllerInstance = clazz.getDeclaredConstructor().newInstance();

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(NgResource.class) && method.isAnnotationPresent(NgPath.class)) {
                    
                    NgPath ngPath = method.getAnnotation(NgPath.class);
                    String webPath = ngPath.path();
                    
                    if (!method.getReturnType().equals(NgResponse.class)) {
                        log.error("The method {} in {} must return NgResponse. Ignoring.", method.getName(), clazz.getName());
                        continue;
                    }

                    method.setAccessible(true);
                    
                    server.createContext(webPath, exchange -> {
                        log.info("Received request: " + webPath);
                        try {
                            // Invoke the method
                            NgResponse response = (NgResponse) method.invoke(controllerInstance);
                            
                            Path filePath = templateDirectory.resolve(response.getTemplateResource());
                            
                            if (Files.exists(filePath)) {
                                String file = Files.readString(filePath);
                                if (!response.getViewBag().isEmpty()) {
                                    file = replaceVariableOcurrencies(file, response.getViewBag());
                                }
                                byte[] fileBytes = file.getBytes(StandardCharsets.UTF_8);
                               
                                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                                
                                int status = response.getStatusCode() != null ? response.getStatusCode() : 200;
                                
                                exchange.sendResponseHeaders(status, fileBytes.length);
                                
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(fileBytes);
                                }
                            } else {
                                log.error("Template not found: " + filePath.toAbsolutePath());
                                String errorMsg = "404 - Template not found";
                                exchange.sendResponseHeaders(404, errorMsg.length());
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(errorMsg.getBytes());
                                }
                            }

                        } catch (Exception e) {
                            log.error("Error processing request at: " + webPath, e);
                            try {
                                exchange.sendResponseHeaders(500, -1);
                            } catch (IOException ex) {
                                // PASS
                            }
                        } finally {
                            exchange.close();
                        }
                    });
                    
                    log.info("Registered path: {} -> method: {}", webPath, method.getName());
                }
            }
        } catch (Exception e) {
            log.error("Failed to register controller: " + clazz.getName(), e);
        }
    }
    
    private List<Class<?>> validateNgControllers(List<Class<?>> clazzes) {
        return clazzes.stream().map(clazz -> {
            
            if (!clazz.isAnnotationPresent(NgController.class)) {
                log.error("No @NgController at provided class {}", clazz.getName());
                return null;
            }
                
            this.validateNgResources(clazz);
            
            return clazz;
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    }
    
    private Class<?> validateNgResources(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(NgResource.class)) {
                if (!method.isAnnotationPresent(NgPath.class)) {
                    log.error("No @NgPath at method {} in {}", method.getName(), clazz.getPackageName());
                    return null;
                }
                NgPath ngPath = method.getAnnotation(NgPath.class);
                if (ngPath.path() == null || ngPath.path().isEmpty()) {
                    log.error("No path = '' provided in @NgPath at method {} in {}", method.getName(), clazz.getPackageName());
                    return null;
                }
            }
        }
        return clazz;
    }
}
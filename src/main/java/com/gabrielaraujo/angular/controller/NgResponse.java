package com.gabrielaraujo.angular.controller;

import java.util.HashMap;

public class NgResponse {
	private final String templateResource;
	private final HashMap<String, String> viewBag;
	private Integer statusCode;
	
	public NgResponse(String templateResource) {
		super();
		this.templateResource = templateResource;
		this.viewBag = new HashMap<>();
	}

	public NgResponse(String templateResource, Integer statusCode) {
		super();
		this.templateResource = templateResource;
		this.statusCode = statusCode;
		this.viewBag = new HashMap<>();
	}
	
	public NgResponse(String templateResource, Integer statusCode, HashMap<String, String> viewBag) {
		super();
		this.templateResource = templateResource;
		this.statusCode = statusCode;
		this.viewBag = viewBag;
	}
	
	public NgResponse(String templateResource, HashMap<String, String> viewBag) {
		super();
		this.templateResource = templateResource;
		this.viewBag = viewBag;
	}
	
	public HashMap<String, String> getViewBag() {
		return viewBag;
	}

	public String getTemplateResource() {
		return templateResource;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public static NgResponse of(String templateResource) {
		return new NgResponse(templateResource);
	}
	
	public static NgResponse of(String templateResource, HashMap<String, String> viewBag) {
		return new NgResponse(templateResource, viewBag);
	}
}

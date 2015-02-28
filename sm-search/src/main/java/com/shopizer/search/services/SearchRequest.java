package com.shopizer.search.services;

import java.util.Collection;

public class SearchRequest {
	
	private String collection;
	
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}


	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	private String json;
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	private int size = -1;
	private int start;

	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}

}

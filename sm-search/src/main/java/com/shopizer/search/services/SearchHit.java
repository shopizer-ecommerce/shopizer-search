package com.shopizer.search.services;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.search.highlight.HighlightField;

public class SearchHit {
	
	//private org.elasticsearch.search.SearchHit searchit;
	private String id;
	private String index;
	private float score;
	private Map<String,Object> metaEntries = new HashMap<String,Object>();
	
	public Map<String,Object> getMetaEntries() {
		return metaEntries;
	}

	public SearchHit(org.elasticsearch.search.SearchHit searchit) {
		
		this.id = searchit.getId();
		this.score = searchit.getScore();
		this.index = searchit.getIndex();
		metaEntries.put("source", searchit.getSource());
		
		if(searchit.getHighlightFields()!=null && searchit.getHighlightFields().size()>0) {
			metaEntries.put("highlightFields", searchit.getHighlightFields());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSource() {
		return (Map<String, Object>)metaEntries.get("source");
	}
	
	public String getId() {
		return id;
	}
	
	public String getIndex() {
		return index;
	}
	
	public float getScore() {
		return score;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, HighlightField> getHighlightFields() {
		return (Map<String, HighlightField>)metaEntries.get("highlightFields");
	}
	
	

}

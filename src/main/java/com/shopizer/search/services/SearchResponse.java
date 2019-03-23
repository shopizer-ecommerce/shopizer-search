package com.shopizer.search.services;

import java.util.Collection;
import java.util.Map;

/**
 * Object used for autocomplete and regular search
 * 
 * @author Carl Samson
 *
 */
public class SearchResponse {

  private String inputSearchJson;
  private Collection<String> ids;
  private int count;


  private Collection<SearchHit> searchHits;
  private Map<String, Facet> facets;

  public Map<String, Facet> getFacets() {
    return facets;
  }

  public void setFacets(Map<String, Facet> facets) {
    this.facets = facets;
  }

  private String[] inlineSearchList;


  public String[] getInlineSearchList() {
    return inlineSearchList;
  }

  public void setInlineSearchList(String[] inlineSearchList) {
    this.inlineSearchList = inlineSearchList;
  }

  public Collection<SearchHit> getSearchHits() {
    return searchHits;
  }

  public void setSearchHits(Collection searchHits) {
    this.searchHits = searchHits;
  }

  public String getInputSearchJson() {
    return inputSearchJson;
  }

  public void setInputSearchJson(String inputSearchJson) {
    this.inputSearchJson = inputSearchJson;
  }

  public Collection<String> getIds() {
    return ids;
  }

  public void setIds(Collection<String> ids) {
    this.ids = ids;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }



}

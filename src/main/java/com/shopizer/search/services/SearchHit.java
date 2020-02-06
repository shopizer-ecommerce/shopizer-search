package com.shopizer.search.services;

import java.util.HashMap;
import java.util.Map;

public class SearchHit {

  private String id;
  private String index;
  private String internalId;
  private Map<String, Object> item = new HashMap<String, Object>();


  public Map<String, Object> getItem() {
    return item;
  }

  public SearchHit(Map<String, Object> item, String id, String internalId) {

    this.id = id;
    this.internalId = internalId;
    this.item = item;


  }



  public String getId() {
    return id;
  }

  public String getIndex() {
    return index;
  }

  public String getInternalId() {
    return internalId;
  }



}

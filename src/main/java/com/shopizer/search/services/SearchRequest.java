package com.shopizer.search.services;

import java.util.ArrayList;
import java.util.List;

public class SearchRequest {

  @Deprecated()
  private List<String> collections = new ArrayList<String>();
  private String index;
  private String match;

  public List<String> getCollections() {
    return collections;
  }

  public void addCollection(String collection) {
    if (this.collections == null) {
      this.collections = new ArrayList<String>();
    }
    this.collections.add(collection);
  }


  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  
  @Deprecated
  /**
   * Set match string
   */
  private String json;

  public String getJson() {
    return json;
  }

  @Deprecated()
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

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public String getMatch() {
    return match;
  }

  public void setMatch(String match) {
    this.match = match;
  }

}

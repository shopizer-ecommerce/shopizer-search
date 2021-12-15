package com.shopizer.search.services.impl;

import java.util.Collection;
import java.util.Set;
import com.shopizer.search.services.IndexKeywordRequest;
import com.shopizer.search.services.SearchRequest;
import com.shopizer.search.services.SearchResponse;

public interface SearchDelegate {

  public boolean indexExist(String indexName) throws Exception;


  /**
   * Creates a structure that represents the object and the content to be indexed
   * 
   * @param mappingJson string
   * @param settingsJson settings definition
   * @param index name
   * @throws Exception when index cannot be created
   */
  void createIndice(String mappingJson, String settingsJson, String index)
      throws Exception;

  /**
   * Will index an object in json format in a collection of indexes
   * 
   * @param json string
   * @param index name
   * @param id of object to index
   * @throws Exception when index cannot be created
   */
  void index(String json, String index, String id) throws Exception;

  /**
   * @param index name of index
   * @param id of object to delete
   * @throws Exception thrown
   */
  void delete(String index, String id) throws Exception;

  /**
   * 
   * @param ids collection of ids
   * @param index name of index
   * @throws Exception thrown
   */
  void bulkDeleteIndex(Collection<String> ids, String index) throws Exception;

  /**
   * Index keywords in bulk
   * 
   * @param bulks indexes
   * @param index name
   * @throws Exception when index cannot be created
   */
  void bulkIndexKeywords(Collection<IndexKeywordRequest> bulks, String index)
      throws Exception;

  /**
   * 
   * @param index name of index
   * @param id of object to retrieve
   * @return GetResponse
   * @throws Exception thrown
   */
  com.shopizer.search.services.GetResponse getObject(String index, String id)
      throws Exception;


  /**
   * Search for a term
   * 
   * @param request Search request
   * @return SearchResponse
   * @throws Exception when search fails
   */
  SearchResponse search(SearchRequest request) throws Exception;

  /**
   * 
   * @param collection index name
   * @param word autocomplete word
   * @param size number of items to retrieve
   * @return Set list of unique completion words
   * @throws Exception thrown
   */
  Set<String> searchAutocomplete(String collection, String word, int size) throws Exception;

}

package com.shopizer.search.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.shopizer.search.services.worker.KeywordIndexerImpl;
import com.shopizer.search.services.worker.ObjectIndexerImpl;
import com.shopizer.search.services.workflow.DeleteObjectWorkflow;
import com.shopizer.search.services.workflow.GetWorkflow;
import com.shopizer.search.services.workflow.IndexWorkflow;
import com.shopizer.search.services.workflow.SearchWorkflow;
import com.shopizer.search.utils.SearchClient;


/**
 * This is the main class for indexing and searching services
 * 
 * @author Carl Samson
 *
 */

public class SearchService {


  private static Logger log = Logger.getLogger(SearchService.class);

  @Autowired
  private DeleteObjectWorkflow deleteWorkflow;

  @Autowired
  private IndexWorkflow indexWorkflow;

  @Autowired
  private GetWorkflow getWorkflow;

  @Autowired
  private SearchWorkflow searchWorkflow;

  @Autowired
  private ObjectIndexerImpl index;

  @Autowired
  private KeywordIndexerImpl keyword;

  @Autowired
  private SearchClient searchClient;

  public void initService() {
    log.debug("Initializing search service");

    try {
      index.init(searchClient);
      keyword.init(searchClient);
    } catch (Exception e) {
      log.error("Cannot initialize SearchService correctly, will be initialized lazily", e);
    }

  }



  /**
   * 
   * @param index name of index
   * @param id of object to delete
   * @throws Exception thrown
   */
  public void deleteObject(String index, String id) throws Exception {
    deleteWorkflow.deleteObject(index, id);

  }


  /**
   * 
   * @param index name of index
   * @param id of object
   * @return GetResponse
   * @throws Exception thrown
   */
  public com.shopizer.search.services.GetResponse getObject(String index, String id) throws Exception {

    return getWorkflow.getObject(index, id);
  }

  /**
   * 
   * @param json this is product to index
   * @param index name of index
   * @throws Exception thrown
   */
  public void index(String json, String index) throws Exception {

    indexWorkflow.index(json, index);
  }


  /**
   * 
   * @param index name of index
   * @param word to search for autocompletion
   * @param size number of suggestions
   * @return SearchResponse
   * @throws Exception thrown
   */
  public SearchResponse searchAutoComplete(String index, String word, int size)
      throws Exception {

    return searchWorkflow.searchAutocomplete(index, word, size);
  }

  /**
   * 
   * @param request search request
   * @return SearchResponse
   * @throws Exception thrown
   */
  public SearchResponse search(SearchRequest request) throws Exception {

    return searchWorkflow.search(request);
  }
}

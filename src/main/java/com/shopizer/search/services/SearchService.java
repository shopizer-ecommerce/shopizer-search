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



  public void deleteObject(String collection, String object, String id) throws Exception {
    deleteWorkflow.deleteObject(collection, object, id);

  }


  public com.shopizer.search.services.GetResponse getObject(String collection, String object,
      String id) throws Exception {

    return getWorkflow.getObject(collection, object, id);
  }

  /**
   * Index a document
   * 
   * @param json - input string
   * @param collection (name of the collection) Might be product_en or product_fr or any name of the
   *        index container
   * @param object to index that corresponds to the name of the entity to be indexed as defined in
   *        the indice file (product.json). In this case it will be product
   * @throws Exception indexing fails
   */

  public void index(String json, String collection, String object) throws Exception {

    indexWorkflow.index(json, collection, object);
  }


  public SearchResponse searchAutoComplete(String collection, String json, int size)
      throws Exception {

    return searchWorkflow.searchAutocomplete(collection, json, size);
  }

  public SearchResponse search(SearchRequest request) throws Exception {

    return searchWorkflow.search(request);
  }
}

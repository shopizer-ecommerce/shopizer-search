package com.shopizer.search.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.shopizer.search.services.IndexKeywordRequest;
import com.shopizer.search.utils.SearchClient;


/**
 * 
 * @author carlsamson
 *
 */
public class SearchDelegateImpl implements SearchDelegate {

  private List<String> aggregationList;

  public List<String> getAggregationList() {
    return aggregationList;
  }

  public void setAggregationList(List<String> aggregationList) {
    this.aggregationList = aggregationList;
  }

  private SearchClient searchClient = null;

  public SearchClient getSearchClient() {
    return searchClient;
  }

  public void setSearchClient(SearchClient searchClient) {
    this.searchClient = searchClient;
  }

  private static Logger log = Logger.getLogger(SearchDelegateImpl.class);

  @SuppressWarnings("unchecked")
  private Object readNode(JsonElement jsonElement) throws Exception {

    Object container = null;
    if (jsonElement.isJsonObject()) {
      Set<java.util.Map.Entry<String, JsonElement>> ens = ((JsonObject) jsonElement).entrySet();
      if (ens != null) {
        // Iterate JSON Elements with Key values
        for (java.util.Map.Entry<String, JsonElement> en : ens) {
          // System.out.println(en.getKey() + " : ");
          if (container == null) {
            container = new HashMap<String, Object>();
          }
          ((Map) container).put(en.getKey(), readNode(en.getValue()));
        }
      }
    }

    // Check whether jsonElement is Arrary or not
    else if (jsonElement.isJsonArray()) {
      JsonArray jarr = jsonElement.getAsJsonArray();
      // Iterate JSON Array to JSON Elements
      container = new ArrayList<Object>();
      for (JsonElement je : jarr) {
        ((List) container).add(readNode(je));
      }
    }

    // Check whether jsonElement is NULL or not
    else if (jsonElement.isJsonNull()) {
      // print null

    }
    // Check whether jsonElement is Primitive or not
    else if (jsonElement.isJsonPrimitive()) {
      // print value as String
      container = jsonElement.getAsString();
    }


    return container;
  }

  public static Map<String, Object> toMap(JsonObject object) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();

    Set<String> keys = object.keySet();
    for (String key : keys) {
      Object value = object.get(key);

      if (value instanceof JsonArray) {
        value = toList((JsonArray) value);
      }

      else if (value instanceof JsonObject) {
        value = toMap((JsonObject) value);
      }

      else if (value instanceof JsonPrimitive) {
        value = ((JsonPrimitive) value).getAsString();
      }

      map.put(key, value);
    }
    return map;
  }

  public static List<Object> toList(JsonArray array) throws Exception {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.size(); i++) {
      Object value = array.get(i);
      if (value instanceof JsonArray) {
        value = toList((JsonArray) value);
      }

      else if (value instanceof JsonObject) {
        value = toMap((JsonObject) value);
      }

      else if (value instanceof JsonPrimitive) {
        value = ((JsonPrimitive) value).getAsString();
      }

      list.add(value);
    }
    return list;
  }



  /*
   * (non-Javadoc)
   * 
   * @see com.shopizer.search.services.impl.SearchService#indexExist(java.lang.String)
   */
  @Override
  public boolean indexExist(String indexName) throws Exception {
    RestHighLevelClient client = searchClient.getClient();



    GetIndexRequest request = new GetIndexRequest(indexName);
    boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
    return exists;

  }


  // https://github.com/searchbox-io/Jest/blob/master/jest/src/test/java/io/searchbox/indices/CreateIndexIntegrationTest.java
  /*
   * (non-Javadoc)
   * 
   * @see com.shopizer.search.services.impl.SearchService#createIndice(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void createIndice(String mapping, String settings, String indexName) throws Exception {


    RestHighLevelClient client = searchClient.getClient();

    CreateIndexRequest request = new CreateIndexRequest(indexName);

    if (!StringUtils.isBlank(settings)) {
      request.settings(settings, XContentType.JSON);
    }

    if (!StringUtils.isBlank(mapping)) {
      request.mapping(mapping, XContentType.JSON);

    }

    CreateIndexResponse createIndexResponse =
        client.indices().create(request, RequestOptions.DEFAULT);


    if (!createIndexResponse.isAcknowledged()) {
      log.error("An error occured while creating an index " + indexName);
    }

  }


  /*
   * (non-Javadoc)
   * 
   * @see com.shopizer.search.services.impl.SearchService#index(java.lang.String, java.lang.String,
   * java.lang.String, java.lang.String) collection = indexName (tweeter) object = name of item
   * indexed (tweet) id = unique item identifier json = XContent of what to index
   */
  @Override
  public void index(String json, String collection, String id) throws Exception {

    // JestClient client = searchClient.getClient();
    RestHighLevelClient client = searchClient.getClient();

    IndexRequest request = new IndexRequest(collection);
    request.id(id);
    request.source(json, XContentType.JSON);

    IndexResponse response = client.index(request, RequestOptions.DEFAULT);

    if (response.getResult() != DocWriteResponse.Result.CREATED
        && response.getResult() != DocWriteResponse.Result.UPDATED) {
      log.error(
          "An error occured while indexing a document " + json + " " + response.getResult().name());
    }


  }

  /*
   * (non-Javadoc)
   * 
   * @see com.shopizer.search.services.impl.SearchService#delete(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public void delete(String collection, String id) throws Exception {

    if (this.indexExist(collection)) {

      RestHighLevelClient client = searchClient.getClient();

      DeleteRequest request = new DeleteRequest(collection, id);

      DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);

      ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();

      if (shardInfo.getFailed() > 0) {
        log.error(
            "An issue occured while deleting document with id " + id + " from index " + collection);
      }


    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.shopizer.search.services.impl.SearchService#bulkDeleteIndex(java.util.Collection,
   * java.lang.String)
   */
  @Override
  public void bulkDeleteIndex(Collection<String> ids, String collection) throws Exception {


    if (this.indexExist(collection)) {

      RestHighLevelClient client = searchClient.getClient();

      ActionListener<DeleteResponse> listener = new ActionListener<DeleteResponse>() {
        @Override
        public void onResponse(DeleteResponse deleteResponse) {}

        @Override
        public void onFailure(Exception e) {
          log.error("An issue occured while deleting document from index " + collection + " "
              + e.getMessage());

        }
      };

      if (ids != null && ids.size() > 0) {

        for (String id : ids) {
          DeleteRequest request = new DeleteRequest(collection, id);

          client.deleteAsync(request, RequestOptions.DEFAULT, listener);
        }


      }
    }


  }



  /*
   * (non-Javadoc)
   * 
   * @see com.shopizer.search.services.impl.SearchService#bulkIndexKeywords(java.util.Collection,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void bulkIndexKeywords(Collection<IndexKeywordRequest> bulks, String collection)
      throws Exception {


    RestHighLevelClient client = searchClient.getClient();


    ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
      @Override
      public void onResponse(IndexResponse indexResponse) {}

      @Override
      public void onFailure(Exception e) {
        log.error("An issue occured while indexing document from index " + collection + " "
            + e.getMessage());

      }
    };



    for (IndexKeywordRequest key : bulks) {

      StringBuilder jsonBuilder = new StringBuilder();
      jsonBuilder.append("{\"_id_\":").append("\"").append(key.getId()).append("\",")
          .append("\"original\":").append("\"").append(key.getOriginal()).append("\",")
          .append("\"keyword\":").append("\"").append(key.getKeyword()).append("\"}");

      IndexRequest request = new IndexRequest(collection);
      // request.id(id);
      request.source(jsonBuilder.toString(), XContentType.JSON);

      client.indexAsync(request, RequestOptions.DEFAULT, listener);

    }

  }


  /*
   * (non-Javadoc)
   * 
   * @see com.shopizer.search.services.impl.SearchService#getObject(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public com.shopizer.search.services.GetResponse getObject(String index, String id)
      throws Exception {

    RestHighLevelClient client = searchClient.getClient();


    GetRequest getRequest = new GetRequest(index, id);

    GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

    if (getResponse.isExists()) {

      JsonParser parser = new JsonParser();
      JsonObject crunchifyObject = (JsonObject) parser.parse(getResponse.getSourceAsString());

      Map<String, Object> fields = toMap(crunchifyObject);

      com.shopizer.search.services.GetResponse response =
          new com.shopizer.search.services.GetResponse(fields);
      response.setObjectJson(getResponse.getSourceAsString());

      return response;


    } else {

      log.error("Object not found on collection " + index + " from id " + id);
      return null;

    }



  }


  @Override
  public Set<String> searchAutocomplete(String indexName, String keyword, int size)
      throws Exception {

    Set<String> keywords = new HashSet<String>();
    if (StringUtils.isBlank(keyword)) {
      return keywords;
    }

    keyword = keyword.trim();

    RestHighLevelClient client = searchClient.getClient();

    SearchRequest searchRequest = new SearchRequest(indexName);// search in only a single index

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    @SuppressWarnings("rawtypes")
    SuggestionBuilder termSuggestionBuilder =
        SuggestBuilders.completionSuggestion("keyword").text(keyword);
    SuggestBuilder suggestBuilder = new SuggestBuilder();
    suggestBuilder.addSuggestion("keyword-suggest", termSuggestionBuilder);
    searchSourceBuilder.suggest(suggestBuilder);
    searchRequest.source(searchSourceBuilder);
    
    SearchResponse searchResponse = null;
    try {
      
      searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      
    } catch(Exception e) {
      log.error("Cannot search in index [" + indexName + "] index might not exist ",e);
      return keywords;
    }
    
    if(searchResponse == null) {
      log.warn("Empty searchResponse");
      return keywords;
    }


    RestStatus status = searchResponse.status();
    if (status.getStatus() != 200) {
      throw new Exception(
          "Ann error occured when searching index " + indexName + " " + status.getStatus());
    }



    Suggest suggest = searchResponse.getSuggest();
    CompletionSuggestion completionSuggestion = suggest.getSuggestion("keyword-suggest");
    for (CompletionSuggestion.Entry entry : completionSuggestion.getEntries()) {
      for (CompletionSuggestion.Entry.Option option : entry) {
        Map<String, Object> originalFields = option.getHit().getSourceAsMap();
        String suggestText = (String) originalFields.get("original");
        keywords.add(suggestText);
      }
    }


    return keywords;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.shopizer.search.services.impl.SearchService#search(com.shopizer.search.services.
   * SearchRequest)
   */
  @Override
  public com.shopizer.search.services.SearchResponse search(
      com.shopizer.search.services.SearchRequest request) throws Exception {



    RestHighLevelClient client = searchClient.getClient();

    String index = null;

    if (!CollectionUtils.isEmpty(request.getCollections())) {
      index = request.getCollections().get(0);
    }
    if (!StringUtils.isBlank(request.getIndex())) {
      index = request.getIndex();
    }

    SearchRequest searchRequest = new SearchRequest(index);// search in only a single index

    MultiMatchQueryBuilder qb = new MultiMatchQueryBuilder(request.getMatch(), "name",
        "description", "categories", "manufacturer", "tags");
    qb.fuzziness(Fuzziness.AUTO);

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(qb);
    if (request.getSize() > -1) {
      searchSourceBuilder.size(request.getSize());
      searchSourceBuilder.from(request.getStart());
    }


    // searchSourceBuilder.sort(new FieldSortBuilder("name").order(SortOrder.ASC));

    // based on aggregationList

    for (String aggregationName : aggregationList) {
      TermsAggregationBuilder agg =
          AggregationBuilders.terms(aggregationName).field(aggregationName);
      searchSourceBuilder.aggregation(agg);
    }

    searchRequest.source(searchSourceBuilder);
    SearchResponse searchResponse = null;
    
    try {
      searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    } catch(Exception e) {
      log.error("Cannot search in index [" + index + "] index might not exist ",e);
      return new com.shopizer.search.services.SearchResponse();
    }
    
    if(searchResponse == null) {
      log.warn("Empty searchResponse");
      return new com.shopizer.search.services.SearchResponse();
    }


    RestStatus status = searchResponse.status();
    if (status.getStatus() != 200) {
      throw new Exception(
          "Ann error occured when searcging index " + index + " " + status.getStatus());
    }

    com.shopizer.search.services.SearchResponse response = this.buildSearchHits(searchResponse);

    Map<String, com.shopizer.search.services.Facet> facetsMap =
        new HashMap<String, com.shopizer.search.services.Facet>();



    for (String aggregationName : aggregationList) {
      com.shopizer.search.services.Facet f = new com.shopizer.search.services.Facet();
      f.setName(aggregationName);
      facetsMap.put(f.getName(), f);


      Terms termAgg = searchResponse.getAggregations().get(aggregationName);
      for (Terms.Bucket entry : termAgg.getBuckets()) {
        String key = entry.getKeyAsString(); // bucket key
        long docCount = entry.getDocCount(); // Doc count
        com.shopizer.search.services.Entry e = new com.shopizer.search.services.Entry();
        e.setName(key);
        e.setCount(docCount);
        f.getEntries().add(e);

      }
    }

    response.setFacets(facetsMap);
    return response;


  }

  @SuppressWarnings("unchecked")
  private com.shopizer.search.services.SearchResponse buildSearchHits(SearchResponse searchResponse)
      throws Exception {



    com.shopizer.search.services.SearchResponse response =
        new com.shopizer.search.services.SearchResponse();


    SearchHits hits = searchResponse.getHits();
    TotalHits totalHits = hits.getTotalHits();
    // the total number of hits, must be interpreted in the context of totalHits.relation
    long numHits = totalHits.value;

    response.setCount(numHits);

    SearchHit[] searchHits = hits.getHits();
    List<com.shopizer.search.services.SearchHit> buildHits =
        new ArrayList<com.shopizer.search.services.SearchHit>();
    @SuppressWarnings("rawtypes")
    List ids = new ArrayList();
    for (SearchHit hit : searchHits) {
      // do something with the SearchHit
      String sourceAsString = hit.getSourceAsString();

      JsonParser parser = new JsonParser();
      JsonObject crunchifyObject = (JsonObject) parser.parse(sourceAsString);

      Map<String, Object> item = toMap(crunchifyObject);

      String _id = hit.getId();

      if (_id == null) {
        throw new Exception("Indexed items don't have _id");
      }


      String sku = null;

      JsonElement _skuElement = crunchifyObject.get("sku");
      if (_skuElement != null) {
        sku = _skuElement.getAsString();
      }

      com.shopizer.search.services.SearchHit h =
          new com.shopizer.search.services.SearchHit(item, _id, sku);


      buildHits.add(h);
      ids.add(_id);

    }

    response.setIds(ids);
    response.setSearchHits(buildHits);

    return response;


  }

}

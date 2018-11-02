package com.shopizer.search.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopizer.search.services.IndexKeywordRequest;
import com.shopizer.search.services.SearchRequest;
import com.shopizer.search.services.SearchResponse;
import com.shopizer.search.utils.SearchClient;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.params.Parameters;

/**
 * https://www.found.no/foundation/java-clients-for-elasticsearch/
 * https://github.com/searchbox-io/Jest/blob/master/jest/README.md
 * HTTP Proxy https://www.elastic.co/blog/playing-http-tricks-nginx
 * Backup - restore http://tech.domain.com.au/2014/12/elasticsearch-snapshots-backup-restore-s3/
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
	                    //System.out.println(en.getKey() + " : ");
	                	if(container==null) {
	                		container = new HashMap<String, Object>();
	                	}
	                	((Map)container).put(en.getKey(), readNode(en.getValue()));
	                }
	            }
	        } 
	        
	        // Check whether jsonElement is Arrary or not
	        else if (jsonElement.isJsonArray()) {
	                    JsonArray jarr = jsonElement.getAsJsonArray();
	                    // Iterate JSON Array to JSON Elements
	                    container = new ArrayList<Object>();
	                    for (JsonElement je : jarr) {
	                        ((List)container).add(readNode(je));
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
	
	private Map<String, Object> getResults(JsonObject jsonObject, String path) throws Exception {
		
		Map<String, Object> fields = new HashMap<String, Object>();
		JsonElement jsonElement = jsonObject.get(path);
		Set<java.util.Map.Entry<String, JsonElement>> ens = ((JsonObject) jsonElement).entrySet();
        if (ens != null) {
            // Iterate JSON Elements with Key values
            for (java.util.Map.Entry<String, JsonElement> en : ens) {
                fields.put(en.getKey(),readNode(en.getValue()));
            }
        }
        
        return fields;
		
		
	}
	

	
	/* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#indexExist(java.lang.String)
	 */
	@Override
	public boolean indexExist(String indexName) throws Exception {
		JestClient client = searchClient.getClient();
		

		@SuppressWarnings("rawtypes")
		IndicesExists.Builder builder = new IndicesExists.Builder(indexName);
		
		
		if(!StringUtils.isBlank(searchClient.getAuthenticationHeader())) {
			builder.setHeader("Authorization", searchClient.getAuthenticationHeader());
		}
		
		Action action = builder.build();
		
		@SuppressWarnings("unchecked")
		JestResult result = client.execute(action);

		return result.isSucceeded();

	}
	
	
	//https://github.com/searchbox-io/Jest/blob/master/jest/src/test/java/io/searchbox/indices/CreateIndexIntegrationTest.java
	/* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#createIndice(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createIndice(String mapping, String settings,String index,String object) throws Exception {
		
	
		
		//Client client = searchClient.getClient();
		JestClient client = searchClient.getClient();
		
		
		CreateIndex.Builder createIndex = new CreateIndex.Builder(index);
				//.Builder(index);
		if(!StringUtils.isEmpty(settings)) {

			
			createIndex.settings(
			        Settings.builder()
			        .loadFromSource(settings,XContentType.JSON)
			        .build().getAsMap());

		}
				
		client.execute(createIndex.build());
		
		PutMapping.Builder putMapping = new PutMapping.Builder(
				index,
				object,
				mapping
		);
		
		
		JestResult result = client.execute(putMapping.build());
		
		if(result!=null && !StringUtils.isBlank(result.getErrorMessage())) {
			log.error("An error occured while creating an index " + result.getErrorMessage());
		}

		
	}
	
	
    /* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#index(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void index(String json, String collection, String object, String id) throws Exception {
		
		JestClient client = searchClient.getClient();

        
        Index index = new Index.Builder(json).index(collection).type(object).id(id).build();
        JestResult result = client.execute(index);
        
		if(result!=null && !StringUtils.isBlank(result.getErrorMessage())) {
			log.error("An error occured while indexing a document " + json + " " + result.getErrorMessage());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#delete(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void delete(String collection, String object, String id) throws Exception {
		
		if(this.indexExist(collection)) {
			
			JestClient client = searchClient.getClient();

			Delete builder = new Delete.Builder(id)
            .index(collection)
            .type(object)
            .build();
			
			JestResult result = client.execute(builder);
			
			if(result!=null && !result.isSucceeded() && !StringUtils.isBlank(result.getErrorMessage())) {
				log.error("Cannot delete from " + collection + " with id " + id);
			}

		}
	}
	
	/* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#bulkDeleteIndex(java.util.Collection, java.lang.String)
	 */
	@Override
	public void bulkDeleteIndex(Collection<String> ids, String collection, String object) throws Exception {
		

		if(this.indexExist(collection)) {
			
			JestClient client = searchClient.getClient();

			if(ids!=null && ids.size()>0) {

				
				Bulk.Builder bulk = new Bulk.Builder()
			    .defaultIndex(collection);
				
				for(String s : ids) {
					

					bulk.defaultType(object)
					.addAction(new Delete.Builder(s).index(collection).type(object).build());

					
				}
				
				JestResult results = client.execute(bulk.build());
				
				if(!results.isSucceeded()) {
					log.error("ES response has failures " + results.getErrorMessage());
				}

				
			}
		}
		

	}
	

	
	/* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#bulkIndexKeywords(java.util.Collection, java.lang.String, java.lang.String)
	 */
	@Override
	public void bulkIndexKeywords(Collection<IndexKeywordRequest> bulks, String collection, String object) throws Exception {
		


			JestClient client = searchClient.getClient();
			

			Bulk.Builder bulk = new Bulk.Builder()
		    .defaultIndex(collection)
		    .defaultType(object);

			for(IndexKeywordRequest key : bulks) {

				
				String id = key.getKey();
				if(id.length()>25) {
					id = id.substring(0,25);
				}
				id = id.trim().toLowerCase();
				
				StringBuilder jsonBuilder = new StringBuilder();
				jsonBuilder.append("{\"_id_\":").append("\"").append(key.getId()).append("\",")
						   .append("\"keyword\":").append("\"").append(key.getKey()).append("\"}");
				
		        Index index = new Index.Builder(jsonBuilder.toString()).index(collection).type(object).id(id).build();

                bulk.addAction(index);
			}
			 
			log.debug("Adding to collection " + collection);
			         
			JestResult results = client.execute(bulk.build());
			
			if(!results.isSucceeded()) {
				log.error("ES response has failures " + results.getErrorMessage());
			}


		
	}
	

	/* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#getObject(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public com.shopizer.search.services.GetResponse getObject(String collection, String object, String id) throws Exception {
		
		JestClient client = searchClient.getClient();
		
		Get get = new Get.Builder(collection, id).type(object).build();

		JestResult result = client.execute(get);
		
		
		
		/**
		 * This method throws an exception
		 */
		//GetResponse response = searchClient.getClient().prepareGet(collection, object, id)
		//.setOperationThreaded(true) 
		//.setFields("_source")
        //.execute() 
        //.actionGet();
				
		com.shopizer.search.services.GetResponse response = null;
		if(result!=null && StringUtils.isBlank(result.getErrorMessage())) {
			
				
				JsonObject jsonObject = result.getJsonObject();
				
				Map<String, Object> fields = this.getResults(jsonObject, "_source");

				response = new com.shopizer.search.services.GetResponse(fields);
				response.setObjectJson(result.getJsonString());
				
		} else {
				log.error("Error wile performing a get method " + result.getErrorMessage());
		}

		return response;
		
	}
	
	/* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#search(com.shopizer.search.services.SearchRequest)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SearchResponse search(SearchRequest request) throws Exception {

		   
		
		    JestClient client = searchClient.getClient();
		
		    SearchResponse response = new SearchResponse();

			Search.Builder search = new Search.Builder(request.getJson());
                // multiple index or types can be added.
            for(String index : request.getCollections()) {
            	search.addIndex(index);
            }


			
			//org.elasticsearch.action.search.SearchRequestBuilder builder = searchClient.getClient().prepareSearch(request.getCollection())
	        
					
			//.setQuery("{ \"term\" : { \"keyword\" : \"dynamic\" }}")
			//.setQuery(request.getJson())
			//.addHighlightedField("description")
			//.addHighlightedField("tags")
			//with extra you can set everything
			//.setExtraSource(request.getJson())
	        //.setExplain(false);
			
			
			if(request.getSize()>-1) {
				search.setParameter(Parameters.SIZE, request.getSize());
				//builder.setSize(request.getSize());
			}
	        


	        //builder.setQuery(termQuery(field, term));
			
			SearchResult result = client.execute(search.build());
			
			if(result==null) {
				throw new Exception("Search result is null");
			}
			
			if(!result.isSucceeded() && !StringUtils.isBlank(result.getErrorMessage())) {
				log.error("An error occured while searching " + result.getErrorMessage());
				return response;
			}
	
	        //org.elasticsearch.action.search.SearchResponse rsp = builder.execute().actionGet();
	        //SearchHit[] docs = rsp.getHits().getHits();
			List<com.shopizer.search.services.SearchHit> hits = new ArrayList<com.shopizer.search.services.SearchHit>();
	        @SuppressWarnings("rawtypes")
			List ids = new ArrayList();
	        
	        
	        JsonArray responseHits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
	        response.setCount(responseHits.size());//TODO number of items instead ???
	        
	        
	        //for (SearchHit sd : docs) {
	        Iterator<JsonElement> elementsIterator = responseHits.iterator();
	        while (elementsIterator.hasNext()) {
	        	JsonElement element = elementsIterator.next();
	        	
	        	JsonObject jsonObject = element.getAsJsonObject();

	        	Map<String,Object> item = this.getResults(jsonObject, "_source");

	        	
	        	JsonElement _idElement = jsonObject.get("_id");
	        	String _id = _idElement.getAsString();

	
	          // if we use in mapping: "_source" : {"enabled" : false}
	          // we need to include all necessary fields in query and then to use doc.getFields()
	          // instead of doc.getSource()


	        	com.shopizer.search.services.SearchHit hit = new com.shopizer.search.services.SearchHit(item, _id);
	        	hits.add(hit);
	        	
	        	
	        	ids.add(item.get("id"));

	        }
	        
	        response.setIds(ids);
	        response.setSearchHits(hits);
	        
	        TermsAggregation terms = null;
	        Map<String,com.shopizer.search.services.Facet> facetsMap = new HashMap<String,com.shopizer.search.services.Facet>();
	        if(result.getAggregations()!=null)  {
	        	
	        	if(CollectionUtils.isNotEmpty(aggregationList)) {
	        		for(String agg : aggregationList) {
	        			terms = result.getAggregations().getTermsAggregation(agg);
	        			if(terms!=null) {
		        			List<TermsAggregation.Entry> entries = terms.getBuckets();
			        			com.shopizer.search.services.Facet f = new com.shopizer.search.services.Facet();
			        			f.setName(terms.getName());
			        			List<com.shopizer.search.services.Entry> facetEntries = new ArrayList<com.shopizer.search.services.Entry>();
			        			for(TermsAggregation.Entry ent : entries) {
			        				//ent.getName();
			        				com.shopizer.search.services.Entry entry = new com.shopizer.search.services.Entry();
			        				entry.setName(ent.getKey());
			        				entry.setCount(ent.getCount());
			        				facetEntries.add(entry);
			        			}
			        			f.setEntries(facetEntries);
			        			facetsMap.put(f.getName(), f);
		        			}
	        		}
	        	}	
	        	response.setFacets(facetsMap);
	        	
	        }
	        
	        /*
	        List<TermsFacet> termsFacets = result.getFacets(TermsFacet.class);
	        

	        //TermsAggregation terms = result.getAggregations().getTermsAggregation("terms1");
	        
	        
	        //Facets facets = rsp.getFacets();
	        if(termsFacets!=null) {
	        	Map<String,com.shopizer.search.services.Facet> facetsMap = new HashMap<String,com.shopizer.search.services.Facet>();
	        	for (TermsFacet facet : termsFacets) {

	        	     //if (facet instanceof TermsFacet) {
	        	         //TermsFacet ff = (TermsFacet) facet;
	        	         com.shopizer.search.services.Facet f = new com.shopizer.search.services.Facet();
	        	         f.setName(facet.getName());
	        	         List<com.shopizer.search.services.Entry> entries = new ArrayList<com.shopizer.search.services.Entry>();
	        	         for(Term o : facet.terms()) {
	        	        	 com.shopizer.search.services.Entry entry = new com.shopizer.search.services.Entry();
	        	        	 entry.setName(o.getName());
	        	        	 entry.setCount(o.getCount());
	        	        	 entries.add(entry);
	        	         }
	        	         f.setEntries(entries);
	        	         facetsMap.put(facet.getName(), f);
	        	}
	        	response.setFacets(facetsMap);
	        }
*/	        
	        response.setSearchHits(hits);
	        return response;

		
	}

	/* (non-Javadoc)
	 * @see com.shopizer.search.services.impl.SearchService#searchAutocomplete(java.lang.String, java.lang.String, int)
	 */
	
	@Override
	public Set<String> searchAutocomplete(String collection,String json,int size) throws Exception {

		    JestClient client = searchClient.getClient();

			Search.Builder search = new Search.Builder(json);
			search.addIndex(collection);

			SearchResult result = client.execute(search.build());
			
			if(result==null) {
				throw new Exception("Search result is null");
			}
			
			if(!result.isSucceeded() && !StringUtils.isBlank(result.getErrorMessage())) {
				log.error("An error occured while searching " + result.getErrorMessage());
				return null;
			}
			
	        JsonArray responseHits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
	        
	        Set<String> keywords = new HashSet<String>();
	        
	        
	        //for (SearchHit sd : docs) {
	        Iterator<JsonElement> elementsIterator = responseHits.iterator();
	        while (elementsIterator.hasNext()) {
	        	JsonElement element = elementsIterator.next();
	        	
	        	JsonObject jsonObject = element.getAsJsonObject();

	        	Map<String,Object> item = this.getResults(jsonObject, "_source");
	        	
	        	keywords.add((String)item.get("keyword"));
	        	
	        	
	        }

	
	        //org.elasticsearch.action.search.SearchResponse rsp = builder.execute().actionGet();
	        //SearchHit[] docs = rsp.getHits().getHits();
	        //for (SearchHit sd : docs) {
	          //to get explanation you'll need to enable this when querying:
	          //System.out.println(sd.getExplanation().toString());
	
	          // if we use in mapping: "_source" : {"enabled" : false}
	          // we need to include all necessary fields in query and then to use doc.getFields()
	          // instead of doc.getSource()
	        	//Map source = sd.getSource();
	        	//System.out.println(sd.getType());
	        	//System.out.println(sd.getExplanation().toString());
	        	//System.out.println(sd.fields().toString());
	        	//System.out.println(sd.getMatchedFilters().length);
	        	//SearchHitField f = sd.field("keyword");
	        	//String f = (String)source.get("keyword");
	            //System.out.println(sd.sourceAsString());
	            //System.out.println(sd.getScore());
	        	
	        	//returnList.add(sd.sourceAsString());
	        	//returnList.add(f.toLowerCase());
	        //}
        

		
		return keywords;
		
	}

}

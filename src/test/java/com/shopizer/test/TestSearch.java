package com.shopizer.test;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopizer.search.services.SearchRequest;
import com.shopizer.search.services.SearchResponse;
import com.shopizer.search.services.SearchService;

/**
 * search api Elasticsearch < 6
 * 
 * Get all indexed data
 * curl -XGET 'http://localhost:9200/product_en_default/_search?pretty=1' 
 * 
 * Search
 * curl -XGET 'http://localhost:9200/product_en_default/_search' -d '{"query":{"query_string":{"fields" : ["name^5", "description", "tags"], "query" : "*spr*"}},"facets" : { "categories" : { "terms" : {"field" : "categories"}}}}'
 * 
 * @author carlsamson
 *
 */
@ContextConfiguration(locations = {
		"classpath:spring/spring-context-test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
})


public class TestSearch {
	
	@Inject
	private SearchService searchService;
	
	@Test
	public void testSearch() throws Exception {

		String q ="{\"query\":{\"query_string\":{\"fields\" : [\"name^5\", \"description\", \"tags\"], \"query\" : \"*watch*\"}}}";

		String query = q;
		
		System.out.println(query);

		SearchRequest request = new SearchRequest();
		request.addCollection("product_en_default");
		request.setJson(query);
		request.setSize(20);
		request.setStart(0);

		SearchResponse resp= searchService.search(request);
		
		ObjectMapper mapper = new ObjectMapper();
		String indexData = mapper.writeValueAsString(resp);
		System.out.println(indexData);
		
		
	}
	
	@Test
	public void testSearchWithAggregations() throws Exception {
		

	    String aggregations = "\"aggs\" : { \"categories\" : { \"terms\" : {\"field\" : \"categories\"}}}";
		
		String q ="{\"query\":{\"query_string\":{\"fields\" : [\"name^5\", \"description\", \"tags\"], \"query\" : \"*watch*\"}}";

		String query = q + "," + aggregations + "}";
		//String query = q + "}";


		SearchRequest request = new SearchRequest();
		request.addCollection("product_en_default");
		request.setJson(query);
		request.setSize(20);
		request.setStart(0);
		
		
		System.out.println(query);

		SearchResponse resp= searchService.search(request);
		
		ObjectMapper mapper = new ObjectMapper();
		String indexData = mapper.writeValueAsString(resp);
		System.out.println(indexData);
		
		
	}

}

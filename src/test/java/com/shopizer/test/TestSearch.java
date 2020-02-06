package com.shopizer.test;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


import com.shopizer.search.services.SearchRequest;
import com.shopizer.search.services.SearchResponse;
import com.shopizer.search.services.SearchService;
import org.junit.Assert;
import org.junit.Ignore;

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

@Ignore
public class TestSearch {
	
	@Inject
	private SearchService searchService;
	
	@Test
	public void testSearch() throws Exception {


		SearchRequest request = new SearchRequest();
		request.setSize(-1);
		request.setStart(0);
		request.setIndex("product_en_default");
		request.setMatch("thai pillow");

		SearchResponse resp = searchService.search(request);

		Assert.assertNotNull(resp);
		
		
	}

}

package com.shopizer.test;

import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.shopizer.search.services.SearchRequest;
import com.shopizer.search.services.SearchResponse;
import com.shopizer.search.services.SearchService;


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
	@Ignore
	public void testSearch() throws Exception {
		
		//String json="{\"wildcard\":{\"keyword\":\"Spr*\"}}";
		
		//String json ="{\"query\":{\"match\" : {\"_all\" : \"sp\" }}}";
		
		/**

	var query = '\"query\":{\"query_string\" : {\"fields\" : [\"name^3\", \"description\", \"tags\"], \"query\" : \"*' + q + '*", \"use_dis_max\" : true }}';

		 */
		
		
		
		String json ="{\"query\":{\"query_string\":{\"fields\" : [\"name^5\", \"description\", \"tags\"], \"query\" : \"*spr*\", \"use_dis_max\" : true }}}";


		//String json ="{\"query\":{\"filtered\":{\"query\":{\"text\":{\"_all\":\"beach\"}},\"filter\":{\"numeric_range\":{\"age\":{\"from\":\"22\",\"to\":\"45\",\"include_lower\":true,\"include_upper\":true}}}}},\"highlight\":{\"fields\":{\"description\":{}}},\"facets\":{\"tags\":{\"terms\":{\"field\":\"tags\"}}}}";

		SearchRequest request = new SearchRequest();
		request.setCollection("product_en_default");
		request.setJson(json);
		request.setSize(20);
		request.setStart(0);
		
		
		System.out.println(json);

		SearchResponse resp= searchService.search(request);
		
		ObjectMapper mapper = new ObjectMapper();
		String indexData = mapper.writeValueAsString(resp);
		System.out.println(indexData);
		
		
	}

}

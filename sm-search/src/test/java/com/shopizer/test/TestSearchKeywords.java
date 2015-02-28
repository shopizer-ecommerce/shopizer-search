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

import com.shopizer.search.services.SearchResponse;
import com.shopizer.search.services.SearchService;


@ContextConfiguration(locations = {
		"classpath:spring/spring-context-test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
})


public class TestSearchKeywords {
	
	@Inject
	private SearchService searchService;
	
	@Test
	@Ignore
	public void testSearchKeywords() throws Exception {
		
		//String json="{\"wildcard\":{\"keyword\":\"s*\"}}";
		//String json="{\"term\":{\"keyword\":\"s\"}}";
		
		String json="{\"match\" : {\"keyword\" : {\"query\" : \"sp\",\"type\" : \"phrase_prefix\"}}}";
		
		//String json="{\"match\" : {\"keyword\" : {\"query\" : \"ci\",\"type\" : \"phrase_prefix\"}}}";
		
		//String json ="{\"query\":{\"filtered\":{\"query\":{\"text\":{\"_all\":\"beach\"}},\"filter\":{\"numeric_range\":{\"age\":{\"from\":\"22\",\"to\":\"45\",\"include_lower\":true,\"include_upper\":true}}}}},\"highlight\":{\"fields\":{\"description\":{}}},\"facets\":{\"tags\":{\"terms\":{\"field\":\"tags\"}}}}";

		
		System.out.println(json);

		SearchResponse resp= searchService.searchAutoComplete("keyword_en_default",json,10);
		
		ObjectMapper mapper = new ObjectMapper();
		String indexData = mapper.writeValueAsString(resp);
		System.out.println(indexData);
		
		
	}

}

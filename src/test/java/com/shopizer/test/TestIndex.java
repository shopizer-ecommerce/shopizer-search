package com.shopizer.test;

import javax.inject.Inject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.shopizer.search.services.SearchService;


@ContextConfiguration(locations = {
		"classpath:spring/spring-context-test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class
})

/**
 * Elasticsearch < 6
 * 
 * Test if the server is running
 * curl -X GET 'http://localhost:9200'
 * 
 * Test if an indice exists
 * curl -XHEAD -i 'http://localhost:9200/product_en_default'
 * 
 * Get index infos
 * curl -XGET 'http://localhost:9200/product_en_default/'
 * 
 * Get all indexed data
 * curl -XGET 'http://localhost:9200/product_en_default/_search?pretty=1'
 * 
 * Faceted and filtered search
 * https://medium.com/hepsiburadatech/how-to-create-faceted-filtered-search-with-elasticsearch-75e2fc9a1ae3
 * 
 */
@Ignore
public class TestIndex {
	
	@Inject
	private SearchService searchService;
	
	@Test
	public void testIndex() throws Exception {
		
		String jsonData = "{\"id\":\"100\",\"name\":\"Thai cussion from asia\",\"price\":\"14.99\",\"categories\":[\"Imports\"],\"store\":\"default\",\"description\":\"Import from thailand\", \"tags\":[\"cotton \",\"liquidation\"], \"manufacturer\":\"mnufacture12\"}";
		String jsonData2 = "{\"id\":\"200\",\"name\":\"Asian pillow\",\"price\":\"24.99\",\"categories\":[\"Imports\"],\"store\":\"default\",\"description\":\"Import from asia\", \"tags\":[\"cotton \",\"pillow\"], \"manufacturer\":\"mnufacturer2\"}";

		searchService.index(jsonData2, "product_en_test");//as of es 7.5 3rd type is no more required
		searchService.index(jsonData, "product_en_test");
		
		System.out.println("Index done !");
		
	}

}

package com.shopizer.test;

import javax.inject.Inject;

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
 * @author carlsamson
 *
 */
public class TestIndex {
	
	@Inject
	private SearchService searchService;
	
	@Test
	public void testIndex() throws Exception {
		
		String jsonData = "{\"id\":\"2\",\"name\":\"Citizen tech model silver watch\",\"price\":\"453.99\",\"categories\":[\"Men watches\"],\"store\":\"default\",\"description\":\"Silver watch 2012 model\", \"tags\":[\"Silver watch\",\"Liquidation\"]}";
		String jsonData2 = "{\"id\":\"3\",\"name\":\"Tag Heur black watch\",\"price\":\"1483.99\",\"categories\":[\"Sport watches\"],\"store\":\"default\",\"description\":\"Black watch 2012 model\", \"tags\":[\"Black watch\",\"Liquidation\"]}";
		
		searchService.index(jsonData, "product_en_default", "product_en");
		searchService.index(jsonData2, "product_en_default", "product_en");
		
		System.out.println("Done !");
		
	}

}

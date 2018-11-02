package com.shopizer.search.services.impl;

import java.util.Collection;
import java.util.Set;

import com.shopizer.search.services.IndexKeywordRequest;
import com.shopizer.search.services.SearchRequest;
import com.shopizer.search.services.SearchResponse;

public interface SearchDelegate {

	public boolean indexExist(String indexName) throws Exception;


	/**
	 * Creates a structure that represents the object and the content to be indexed
	 * @param mappingJson string
	 * @param settingsJson settings definition
	 * @param collection name
	 * @param object representation of index
	 * @throws Exception when index cannot be created
	 */
	void createIndice(String mappingJson, String settingsJson, String collection,
			String object) throws Exception;

	/**
	 * Will index an object in json format in a collection
	 * of indexes
	 * @param json string
	 * @param collection name
	 * @param object to be indexed
	 * @param id of object to index
	 * @throws Exception when index cannot be created
	 */
	void index(String json, String collection, String object,
			String id) throws Exception;

	void delete(String collection, String object, String id)
			throws Exception;

	void bulkDeleteIndex(Collection<String> ids,
			String collection, String object) throws Exception;

	/**
	 * Index keywords in bulk
	 * @param bulks indexes
	 * @param collection name
	 * @param object to be indexed
	 * @throws Exception when index cannot be created
	 */
	void bulkIndexKeywords(
			Collection<IndexKeywordRequest> bulks, String collection,
			String object) throws Exception;

	com.shopizer.search.services.GetResponse getObject(
			String collection, String object, String id) throws Exception;


	/**
	 * Search for a term
	 * @param request Search request
	 * @return
	 * @throws Exception when search fails
	 */
    SearchResponse search(SearchRequest request)
			throws Exception;

	Set<String> searchAutocomplete(String collection,
			String json, int size) throws Exception;

}
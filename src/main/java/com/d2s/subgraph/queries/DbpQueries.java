package com.d2s.subgraph.queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

import org.data2semantics.query.QueryCollection;
import org.data2semantics.query.filters.DescribeFilter;
import org.data2semantics.query.filters.GraphClauseFilter;
import org.data2semantics.query.filters.QueryFilter;

import com.d2s.subgraph.queries.filters.SimpleBgpFilter;
import com.d2s.subgraph.queries.filters.SimpleDbpFilter;
import com.hp.hpl.jena.query.QueryParseException;

public class DbpQueries extends QueriesFetcher {
	public static String QUERY_FILE = "src/main/resources/dbpl_queries.log";
	public static String CSV_COPY = "src/main/resources/dbpl_queries.csv";
	public static String PARSE_QUERIES_FILE = "src/main/resources/dbpl_queries.arraylist";
	

	public DbpQueries(QueryFilter... filters) throws IOException {
		this(true, 0, filters);
	}

	public DbpQueries(boolean useCacheFile, int maxNumQueries, QueryFilter... filters) throws IOException {
		super();
		this.maxNumQueries = maxNumQueries;
		File cacheFile = new File(PARSE_QUERIES_FILE);
		if (useCacheFile && cacheFile.exists()) {
			System.out.println("WATCH OUT! getting queries from cache file. might be outdated!");
			readQueriesFromCacheFile(PARSE_QUERIES_FILE);
		}
		if (queryCollection.getTotalQueryCount() == 0 || (maxNumQueries > 0 && maxNumQueries != queryCollection.getTotalQueryCount())) {
			System.out.println("parsing dbpl query logs");
			this.filters = new ArrayList<QueryFilter>(Arrays.asList(filters));
			parseLogFile(new File(QUERY_FILE));
			saveQueriesToCacheFile(PARSE_QUERIES_FILE);
		}
		
	}


	private void parseLogFile(File textFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(textFile));
		String line;
		while ((line = br.readLine()) != null) {
			String matchSubString = "/sparql?query=";
			if (line.contains(matchSubString)) {
				
				int startIndex = line.indexOf(matchSubString);
				startIndex += matchSubString.length();
				String firstString = line.substring(startIndex);
				String encodedUrlQuery = firstString.split(" ")[0];
				// remove other args
				String encodedSparqlQuery = encodedUrlQuery.split("&")[0];

				addQueryToList(URLDecoder.decode(encodedSparqlQuery, "UTF-8"));
				if (queryCollection.getDistinctQueryCount() > maxNumQueries) {
					break;
				}
			}
		}
		br.close();
	}

	

	public static void main(String[] args) {

		try {

			DbpQueries queries = new DbpQueries(false, 10, new SimpleDbpFilter(), new DescribeFilter(), new SimpleBgpFilter(), new GraphClauseFilter());
			System.out.println(queries.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
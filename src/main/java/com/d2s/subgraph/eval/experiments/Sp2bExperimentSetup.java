package com.d2s.subgraph.eval.experiments;

import java.io.IOException;

import org.data2semantics.query.QueryCollection;
import org.data2semantics.query.filters.DescribeFilter;
import org.data2semantics.query.filters.GraphClauseFilter;

import com.d2s.subgraph.queries.QueriesFetcher;
import com.d2s.subgraph.queries.Query;
import com.d2s.subgraph.queries.Sp2bQueries;
import com.d2s.subgraph.queries.filters.SimpleBgpFilter;



public class Sp2bExperimentSetup implements ExperimentSetup {
	public static String GOLDEN_STANDARD_GRAPH = "http://sp2b";
	private static String GRAPH_PREFIX = "sp2b_";
	private static String EVAL_RESULTS_DIR = "sp2bResults";
	private static String QUERY_TRIPLES_DIR = "sp2bQueryTriples";
	private static String QUERY_RESULTS_DIR = "sp2bQueryResults";
	private static boolean PRIVATE_QUERIES = false;
	private static int MAX_NUM_QUERIES = 0;
	private QueriesFetcher queriesFetcher;
	
	public Sp2bExperimentSetup() throws IOException {
		queriesFetcher = new Sp2bQueries(new DescribeFilter(), new SimpleBgpFilter(), new GraphClauseFilter());
		queriesFetcher.setMaxNQueries(MAX_NUM_QUERIES);
	}
	
	public String getGoldenStandardGraph() {
		return GOLDEN_STANDARD_GRAPH;
	}
	public String getGraphPrefix() {
		return GRAPH_PREFIX;
	}
	public QueryCollection<Query> getQueryCollection() {
		return queriesFetcher.getQueryCollection();
	}
	

	public String getEvalResultsDir() {
		return EVAL_RESULTS_DIR;
	}
	
	public String getQueryTriplesDir() {
		return QUERY_TRIPLES_DIR;
	}
	
	public int getMaxNumQueries() {
		return MAX_NUM_QUERIES;
	}

	public String getQueryResultsDir() {
		return QUERY_RESULTS_DIR;
	}
	public boolean privateQueries() {
		return PRIVATE_QUERIES;
	}
}

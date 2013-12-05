package com.d2s.subgraph.eval.experiments;

import java.io.IOException;

import org.data2semantics.query.QueryCollection;
import org.data2semantics.query.filters.ConstructFilter;
import org.data2semantics.query.filters.DescribeFilter;
import org.data2semantics.query.filters.GraphClauseFilter;

import com.d2s.subgraph.queries.QueriesFetcher;
import com.d2s.subgraph.queries.Query;
import com.d2s.subgraph.queries.SwdfQueries;
import com.d2s.subgraph.queries.filters.SimpleBgpFilter;



public class SwdfExperimentSetup extends ExperimentSetup {
	public static String GOLDEN_STANDARD_GRAPH = "http://swdf";
	private static String GRAPH_PREFIX = "df_";
	private static String QUERY_TRIPLES_DIR = "swdfQueryTriples";
	private static String QUERY_RESULTS_DIR = "swdfQueryResults";
	private static String EVAL_RESULTS_DIR = "swdfResults";
	private static boolean PRIVATE_QUERIES = true;
	private static int MAX_NUM_QUERIES = 500;
	private QueriesFetcher queriesFetcher;
	private boolean UNIQUE_QUERIES = true;
	private boolean USE_CACHE_FILE = true;
	
	public SwdfExperimentSetup() throws IOException {
		queriesFetcher = new SwdfQueries(this, USE_CACHE_FILE, MAX_NUM_QUERIES, new DescribeFilter(), new SimpleBgpFilter(), new GraphClauseFilter(), new ConstructFilter());
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
	public int getMaxNumQueries() {
		return MAX_NUM_QUERIES;
	}

	public String getQueryTriplesDir() {
		return QUERY_TRIPLES_DIR;
	}
	public String getQueryResultsDir() {
		return QUERY_RESULTS_DIR;
	}
	public boolean privateQueries() {
		return PRIVATE_QUERIES;
	}
	public boolean useUniqueQueries() {
		return UNIQUE_QUERIES ;
	}
}

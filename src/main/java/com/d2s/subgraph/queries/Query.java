package com.d2s.subgraph.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.data2semantics.query.QueryCollection;
import org.omg.CosNaming.IstringHelper;

import com.d2s.subgraph.eval.results.QueryResults;
import com.d2s.subgraph.querytriples.ExtractTriplePatternsVisitor;
import com.d2s.subgraph.querytriples.RewriteTriplePatternsVisitor;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QueryVisitor;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Prologue;
import com.hp.hpl.jena.sparql.syntax.Element;

public class Query extends org.data2semantics.query.Query {
	private int queryId;
	private boolean onlyDbo;
	private ResultSetRewindable goldenStandardResults = null;
	private ArrayList<HashMap<String, String>> answers = new ArrayList<HashMap<String, String>>();
	private QueryResults queryResults;
	private Date goldenStandardDuration;
	public Query(){}

	public Query(Prologue prologue) {
		super(prologue);
	}
	
	public int getQueryId() {
		return queryId;
	}
	
	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}
	
	public boolean isOnlyDbo() {
		return onlyDbo;
	}
	public void setOnlyDbo(boolean onlyDbo) {
		this.onlyDbo = onlyDbo;
	}
	
	public void setGoldenStandardDuration(Date date) {
		System.out.println("set duration: " + date.getTime());
		this.goldenStandardDuration = date;
	}
	public Date getGoldenStandardDuration() {
		return this.goldenStandardDuration;
	}
	
	public static Query create(String queryString, QueryCollection<Query> queryCollection) {
		
		Query query = new Query();
		query.setStrict(false);
		try {
			query = (Query)(QueryFactory.parse(query, queryString, null, Syntax.defaultQuerySyntax));
		} catch (QueryParseException e) {
			System.out.println(queryString);
			throw e;
		}
		query.setQueryCollection(queryCollection);
		query.generateQueryStats();
		return query;
	}
	
	public ArrayList<HashMap<String, String>> getAnswers() {
		return answers;
	}
	public void setAnswers(ArrayList<HashMap<String, String>> answers) {
		this.answers = answers;
	}
	public ResultSetRewindable getGoldenStandardResults() {
		return goldenStandardResults;
	}
	public void setGoldenStandardResults(ResultSetRewindable goldenStandardResults) {
		this.goldenStandardResults = goldenStandardResults;
	}
	public static Query create(String queryString) throws IOException {
		return create(queryString, new QueryCollection<Query>());
	}
	public QueryResults getResults() {
		return this.queryResults;
	}
	public void setResults(QueryResults results) {
		this.queryResults = results;
	}
	
	public Query getQueryWithFromClause(String fromGraph) throws IOException {
		//crude, using the tostring instead of clone, but this is how the jena clone works as well!
		Query queryWithFromClause = Query.create(this.toString());
		queryWithFromClause.addGraphURI(fromGraph);
		return queryWithFromClause;
	}
	
	/**
	 * remove projection variables
	 * add distinct
	 */
	public Query getQueryForTripleRetrieval() throws IOException {
		Query query = Query.create(this.toString());
		query.setQueryResultStar(true);
		query.setDistinct(true);
		query = query.getQueryWithoutOrderBy();
		return query;
	}
	
	public Query getQueryWithoutOrderBy() {
		//jena does not provide a way to easily -remove- an order by :(. 
		//Instead, we should rebuild the query from scratch, and leave out the order by
		Query query = new Query();
		query.setDistinct(isDistinct());
		query.setCount(getCount());
		query.setLimit(getLimit());
		query.setOffset(getOffset());
		query.setPrefixMapping(getPrefixMapping());
		query.setQueryResultStar(isQueryResultStar());
		
		query.setQueryPattern(getQueryPattern());
		if (isAskType()) query.setQueryAskType();
		if (isDescribeType()) query.setQueryDescribeType();
		if (isConstructType()) query.setQueryConstructType();
		if (isSelectType()) query.setQuerySelectType();
		return query;
		
	}
	
	public Set<Triple> fetchTriplesFromPatterns(QuerySolution solution) {
		Iterator<String> varNames = solution.varNames();
		Element queryElement;
		while (varNames.hasNext()) {
			
			String varName = varNames.next();
			System.out.println("processing var " + varName);
			RDFNode node = solution.get(varName);
			queryElement = getQueryPattern();
			if (queryElement == null) return null;
			queryElement.visit(new RewriteTriplePatternsVisitor(varName, node, this));
		}
		queryElement = getQueryPattern();
		ExtractTriplePatternsVisitor extractTriplesVisitor = new ExtractTriplePatternsVisitor();
		queryElement.visit(extractTriplesVisitor);
		return extractTriplesVisitor.getTriples();
		
	}
	
	
	public static void main(String[] args) throws IOException {
		Query query = Query.create("SELECT ?x WHERE {?x ?jh ?j} GROUP BY ?x LIMIT 10");
		
		/**
		 * fetch triples based on query patterns
		 */
//		QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", "SELECT * WHERE {?x ?jh ?j} LIMIT 1");
//		System.out.println("exec query");
//		ResultSet result = queryExecution.execSelect();
//		for (Triple triple: query.fetchTriplesFromPatterns(result.next())) {
//			System.out.println(triple.toString());
//		}
		
		
		/**
		 * rewrite queries for triple retrieval
		 */
		Query rewrittenQuery = query.getQueryForTripleRetrieval();
		System.out.println(rewrittenQuery);
	}


}

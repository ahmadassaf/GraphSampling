package com.d2s.subgraph.eval.results;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.d2s.subgraph.helpers.Helper;

import au.com.bytecode.opencsv.CSVWriter;



public class GraphResultsSample implements GraphResults{
	private HashMap<Integer, QueryResults> results = new HashMap<Integer, QueryResults>();
	private String graphName;
	private ArrayList<GraphResults> allGraphResults = new ArrayList<GraphResults>();
	
	public void add(QueryResults result) {
		//do nothing. (this object is more of a wrapper for other graph results objects)
	}
	
	/**
	 * aggregate these indivudal sample results to this one sample resultset
	 * @param individualSampleResults
	 */
	public void add(ArrayList<GraphResults> allGraphResults) {
		this.allGraphResults = allGraphResults;
		aggregateToSingleObject();
	}
	
	public void aggregateToSingleObject() {
		//set graph name
		String exampleGraphName = allGraphResults.get(0).getGraphName();
		String pattern = "(.*sample)(-\\d+)(.*)"; //remove the appended number from this run
		setGraphName(exampleGraphName.replaceAll(pattern, "$1$3")); 
		
		ArrayList<Integer> queryIds = allGraphResults.get(0).getQueryIds();
		for (int queryId: queryIds) {
			QueryResultsSample queryResultsSample = new QueryResultsSample();
			ArrayList<QueryResults> allQueryResults = new ArrayList<QueryResults>();
			for (GraphResults graphResults: allGraphResults) {
				allQueryResults.add(graphResults.get(queryId));
			}
			queryResultsSample.add(allQueryResults);
			results.put(queryId, queryResultsSample);
		}
	}
	
	
	public QueryResults get(int queryId) {
		return results.get(queryId);
	}
	public boolean contains(int queryId) {
		return results.containsKey(queryId);
	}
	public boolean queryIdExists(int queryId) {
		return results.containsKey(queryId);
	}
	
	public void writeAsCsv(String path) throws IOException {
		path += "/" + getGraphName().substring(7) + ".csv";//remove http://
		File csvFile = new File(path);
		CSVWriter writer = new CSVWriter(new FileWriter(csvFile), ';');
		writer.writeNext(new String[]{"queryId", "isAggregation", "isAsk", "isOnlyDbo", "isSelect", "recall", "query"});
		for (QueryResults result: results.values()) {
			ArrayList<String> columns = new ArrayList<String>();
			columns.add(Integer.toString(result.getQuery().getQueryId()));
			columns.add(Helper.boolAsString(result.getQuery().isAggregation()));
			columns.add(Helper.boolAsString(result.getQuery().isAsk()));
			columns.add(Helper.boolAsString(result.getQuery().isOnlyDbo()));
			columns.add(Helper.boolAsString(result.getQuery().isSelect()));
			columns.add(Double.toString(result.getRecall()));
			columns.add(result.getQuery().toString());
			writer.writeNext(columns.toArray(new String[columns.size()]));
		}
	    
		writer.close();
	}
	
	public double getAveragePrecision() {
		double totalPrecision = 0.0;
		for (QueryResults result: results.values()) {
			totalPrecision += result.getPrecision();
		}
		return totalPrecision / (double)results.size();
	}
	
	
	public double getAverageRecall() {
		double totalRecall = 0.0;
		for (QueryResults result: results.values()) {
			totalRecall += result.getRecall();
		}
		return totalRecall / (double)results.size();
	}
	
	public int getMaxQueryId() {
		return Collections.max(results.keySet());
	}

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String name) {
		this.graphName = name;
	}
	
	public HashMap<Integer, QueryResults> getAsHashMap() {
		return results;
	}
	
	public ArrayList<QueryResults> getAsArrayList() {
		return new ArrayList<QueryResults>(results.values());
	}
	public ArrayList<Integer> getQueryIds() {
		return new ArrayList<Integer>(results.keySet());
	}


	public static void main(String[] args)  {
		try {
//			EvaluateGraphs evaluate = new EvaluateGraphs(new DbpExperimentSetup());
			GraphResultsSample results = new GraphResultsSample();
			results.aggregateToSingleObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
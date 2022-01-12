package finalproject;

import java.util.HashMap;

import finalproject.MyWebGraph.WebVertex;

import java.util.ArrayList;

public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, LinkedList of Strings)	
	public MyWebGraph internet;
	public XmlParser parser;
	/*
	public static void main(String[] args) throws Exception {
		SearchEngine a = new SearchEngine("testAcyclic.xml");
		a.internet.addVertex("A");
		a.internet.addVertex("B");
		a.internet.addVertex("C");
		a.internet.addVertex("D");
		a.internet.addEdge("A", "C");
		a.internet.addEdge("A", "B");
		a.internet.addEdge("B", "A");
		a.internet.addEdge("B", "C");
		a.internet.addEdge("B", "D");
		a.internet.addEdge("C", "A");
		a.internet.addEdge("D", "C");
		a.assignPageRanks(0.01);
		System.out.println(a.internet.getPageRank("A"));
		System.out.println(a.internet.getPageRank("B"));
		System.out.println(a.internet.getPageRank("C"));
		System.out.println(a.internet.getPageRank("D"));
		ArrayList<String> urls = new ArrayList<String>();
		urls.add("A");
		urls.add("B");
		urls.add("C");
		urls.add("D");
		a.wordIndex.put("hello", urls);
		ArrayList<String> ordered = a.getResults("Hello");
		
	}
	*/

	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}
	
	/* 
	 * This does a graph traversal of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 * 
	 * 	This method will fit in about 30-50 lines (or less)
	 */
	public void crawlAndIndex(String url) throws Exception {
		// TODO : Add code here
		
		//add url as vertex of internet map
		this.internet.addVertex(url);
		//set visitied equal to true to avoid loops
		this.internet.setVisited(url, true);
		
		//Get all the words in on the url page 
		//Then iterate through all the words adding each word as key to hash map if it don't already exist
		//and adding url to value which is an arrayList of all urls that have the word.
		ArrayList<String> words = this.parser.getContent(url);
		if (words != null) {
			for (String currentWord : words) {
				String currentWordLower = currentWord.toLowerCase();
				
				//get current arrayList at key in wordIndex hash map
				ArrayList<String> currentUrls = wordIndex.get(currentWordLower);
				//if arrayList is null, create a new arraylist and add it to the hash map
				if (currentUrls == null) {
					currentUrls = new ArrayList<String>();
					currentUrls.add(url);
					wordIndex.put(currentWordLower, currentUrls);
				} else {
					//append to current arrayList
					if (!currentUrls.contains(url)) {
						currentUrls.add(url);
					}
				}
			}
		}
		
		//Get all adjacent urls
		ArrayList<String> adjacentUrls = this.parser.getLinks(url);
		//iterate through each adjacent url, add edge to map, and if vertex/url hasn't been visited
		//crawlAndIndex that url.
		if (adjacentUrls != null) {
			for (String adjacentUrl : adjacentUrls) {
				this.internet.addVertex(adjacentUrl);
				this.internet.addEdge(url, adjacentUrl);
				if (!this.internet.getVisited(adjacentUrl)) {
					this.crawlAndIndex(adjacentUrl);
				}
			}
		}
	}
	
	
	
	/* 
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex(). 
	 * To implement this method, refer to the algorithm described in the 
	 * assignment pdf. 
	 * 
	 * This method will probably fit in about 30 lines.
	 */
	public void assignPageRanks(double epsilon) {
		// TODO : Add code here
		//get all vertices in Internet graph, all urls
		ArrayList<String> vertices = this.internet.getVertices();
		//initialize each page rank to 1
		for (String currentVertex : vertices) {
			this.internet.setPageRank(currentVertex, 1.0);
		}
		
		//continue computing and changing page ranks while difference between prev and new 
		//page ranks is not within epsilon.
		boolean lessThanEpsilon = false;
		while(!lessThanEpsilon) {
			//get all new page ranks
			ArrayList<Double> newRanks =this.computeRanks(vertices);
			boolean first = true; //first comparison
			
			//set each new vertex to new page rank
			for(int i = 0; i < newRanks.size(); i++) {
				String vertex =vertices.get(i);
				double oldPageRank = this.internet.getPageRank(vertex);
				this.internet.setPageRank(vertex, newRanks.get(i));
				double newPageRank = newRanks.get(i);
				
				//check if change is within epsilon
				//(oldPageRank <= newPageRank && oldPageRank + epsilon >= newPageRank) || (oldPageRank > newPageRank && oldPageRank - epsilon < newPageRank)
				if (Math.abs(oldPageRank-newPageRank) < epsilon) {
					if (first) {
						lessThanEpsilon = true;
					}
				} else {
						lessThanEpsilon = false;
					
				}
				first = false;
			}
		}
	}

	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph 
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls. 
	 * Note that the double in the output list is matched to the url in the input list using 
	 * their position in the list.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		// TODO : Add code here 
		
		ArrayList<Double> newRanks = new ArrayList<Double>();
		if (vertices != null) {
			for (String currentVertex : vertices) {
				ArrayList<String> intoVertices = this.internet.getEdgesInto(currentVertex);
				Double sum = 0.0;
				if (intoVertices != null) {
					for (String intoVertex : intoVertices) {
						sum += this.internet.getPageRank(intoVertex)/this.internet.getOutDegree(intoVertex);
					}
				}
				Double finalSum = 0.5*sum + 0.5;
				newRanks.add(finalSum);
			}
		}
		return newRanks;
	}

	
	/* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 * 
	 * This method should take about 25 lines of code.
	 */
	public ArrayList<String> getResults(String query) {
		// TODO: Add code here
		query = query.toLowerCase();
		
		ArrayList<String> urls = this.wordIndex.get(query);
		HashMap<String, Double> sortedUrls = new HashMap<String, Double>();
		
		ArrayList<String> finalUrls = new ArrayList<String>();
		
		if (urls != null) {
			for (String url : urls) {
				Double pageRank = this.internet.getPageRank(url);
				sortedUrls.put(url, pageRank);
			}
			
			//Sorting.slowSort(sortedUrls);
			finalUrls = Sorting.fastSort(sortedUrls);
		}
		
		return finalUrls;
	}
}

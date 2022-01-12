package finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry; // You may need it to implement fastSort

public class Sorting {

	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n^2) as it uses bubble sort, where n is the number 
	 * of pairs in the map. 
	 */
    public static <K, V extends Comparable> ArrayList<K> slowSort (HashMap<K, V> results) {
        ArrayList<K> sortedUrls = new ArrayList<K>();
        sortedUrls.addAll(results.keySet());	//Start with unsorted list of urls

        int N = sortedUrls.size();
        for(int i=0; i<N-1; i++){
			for(int j=0; j<N-i-1; j++){
				if(results.get(sortedUrls.get(j)).compareTo(results.get(sortedUrls.get(j+1))) <0){
					K temp = sortedUrls.get(j);
					sortedUrls.set(j, sortedUrls.get(j+1));
					sortedUrls.set(j+1, temp);					
				}
			}
        }
        return sortedUrls;                    
    }
    
    
	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n*log(n)), where n is the number 
	 * of pairs in the map. 
	 */
    private <K, V extends Comparable> void merge(HashMap<K, V> results, ArrayList<K> sortedUrls, int start, int mid, int end) {
    	int size1 = mid + 1 - start;
    	int size2 = end - mid;
    	int index1 = 0;
    	int index2 = 0;
    	
    	ArrayList<K> left = new ArrayList<K>();
    	ArrayList<K> right = new ArrayList<K>();
    	
    	while(index1 < size1) {
    		left.add(sortedUrls.get(start+index1));
    		index1++;
    	}
    	while (index2 < size2) {
    		right.add(sortedUrls.get(mid+1 + index2));
    		index2++;
    	}
    	index1=0;
    	index2=0;
    	int startAdd = start;
    	
    	while (index1 < size1 && index2 < size2) {
    		int compareVal = results.get(left.get(index1)).compareTo(results.get(right.get(index2)));
    		if (compareVal == 0) {
    			sortedUrls.set(startAdd, left.get(index1));
    			startAdd++;
    			sortedUrls.set(startAdd, right.get(index2));
    			startAdd++;
    			index1++;
    			index2++;
    		} else if (compareVal > 0) {
    			sortedUrls.set(startAdd, left.get(index1));
    			startAdd++;
    			index1++;
    		} else {
    			sortedUrls.set(startAdd, right.get(index2));
    			startAdd++;
    			index2++;
    		}
    	}
    	
    	while (index1 < size1) {
    		sortedUrls.set(startAdd, left.get(index1));
    		startAdd++;
    		index1++;
    	}
    	
    	while (index2 < size2) {
    		sortedUrls.set(startAdd, right.get(index2));
    		startAdd++;
    		index2++;
    	}
    	
    	
    	
    }
    private <K, V extends Comparable> void mergeSort(HashMap<K, V> results, ArrayList<K> sortedUrls, int start, int end) {
    	if (start < end) {
    		int mid = (start +end)/2;
    		mergeSort(results, sortedUrls, start, mid);
    		mergeSort(results, sortedUrls, mid+1, end);
    		merge(results, sortedUrls, start, mid, end);
    	}
    }
    public static <K, V extends Comparable> ArrayList<K> fastSort(HashMap<K, V> results) {
    	// ADD YOUR CODE HERE
    	ArrayList<K> sortedUrls = new ArrayList<K>();
        sortedUrls.addAll(results.keySet());	//Start with unsorted list of urls
    	Sorting sortObj = new Sorting();
        sortObj.mergeSort(results, sortedUrls, 0, sortedUrls.size()-1);
        return sortedUrls;
    }
    

}
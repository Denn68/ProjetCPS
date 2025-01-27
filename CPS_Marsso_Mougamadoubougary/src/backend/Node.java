package backend;

import java.util.HashMap;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;

public class Node {

	public Node (int min, int max) {
		this.intervalMin = min;
		this.intervalMax = max;
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
	}
	
	private HashMap<Integer, ContentDataI> tableHachage;
	private int intervalMin;
	private int intervalMax;
	
	public boolean contains(ContentKeyI arg0) {
		if(arg0.hashCode() >= intervalMin && arg0.hashCode() <= intervalMax) {
			return true;
		}
		return false;
	}
	
	public ContentDataI getData(ContentKeyI arg0) {
		return tableHachage.get(arg0.hashCode());
	}
	
	public ContentDataI putData(ContentKeyI arg0, ContentDataI arg1) {
		return tableHachage.put(arg0.hashCode(), arg1);
	}
	
	public ContentDataI removeData(ContentKeyI arg0) {
		return tableHachage.remove(arg0.hashCode());
	}
	
	public void clearComputation() {
		tableHachage.clear();
	}
}

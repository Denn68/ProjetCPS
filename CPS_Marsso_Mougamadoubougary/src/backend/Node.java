package backend;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Predicate;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class Node 
implements ContentAccessSyncI, MapReduceSyncI{

	public Node (int min, int max, Node suivant) {
		this.intervalMin = min;
		this.intervalMax = max;
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
		this.suivant = suivant;
	}
	
	private HashMap<Integer, ContentDataI> tableHachage;
	private int intervalMin;
	private int intervalMax;
	private Node suivant;
	
	public boolean contains(ContentKeyI arg0) {
		if(arg0.hashCode() >= intervalMin && arg0.hashCode() <= intervalMax) {
			return true;
		}
		return false;
	}

	@Override
	public void clearMapReduceComputation(String arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <R extends Serializable> void mapSync(String attribute, SelectorI selector, ProcessorI<R> processor) throws Exception {
		this.tableHachage.values().stream()
		.filter(((Predicate<ContentDataI>) selector))
		.map(processor);
		
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String attribute, ReductorI<A, R> reductor, CombinatorI<A> combinator, A filteredMap)
			throws Exception {
		return this.tableHachage.values().stream()
				.reduce(filteredMap, (u,d) -> reductor.apply(u,(R) d), combinator);
	}

	@Override
	public void clearComputation(String arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ContentDataI getSync(String attribute, ContentKeyI key) throws Exception {
		if (this.contains(key)) {
			return tableHachage.get(key.hashCode());
		} else if (suivant != null){
			return suivant.getSync(attribute, key);
		} else {
			throw new IllegalArgumentException("La clé est n'est pas dans l'intervalle de la table");
		}
	}

	@Override
	public ContentDataI putSync(String attribute, ContentKeyI key, ContentDataI data) throws Exception {
		if (this.contains(key)) {
			return tableHachage.put(key.hashCode(), data);
		} else if (suivant != null){
			return suivant.putSync(attribute, key, data);
		} else {
			throw new IllegalArgumentException("La clé est n'est pas dans l'intervalle de la table");
		}
	}

	@Override
	public ContentDataI removeSync(String attribute, ContentKeyI key) throws Exception {
		if (this.contains(key)) {
			return tableHachage.remove(key.hashCode());
		} else if (suivant != null){
			return suivant.removeSync(attribute, key);
		} else {
			throw new IllegalArgumentException("La clé est n'est pas dans l'intervalle de la table");
		}
	}
}

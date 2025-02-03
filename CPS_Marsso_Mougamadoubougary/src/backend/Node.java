package backend;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

	public Node (int min, int max) {
		this.intervalMin = min;
		this.intervalMax = max;
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
		this.memoryTable = new HashMap<>();
		this.estPasser = false;
	}
	
	private HashMap<Integer, ContentDataI> tableHachage;
	private HashMap<String, Stream<ContentDataI>> memoryTable;
	private int intervalMin;
	private int intervalMax;
	private Node suivant;
	private boolean estPasser;
	
	
	public void setSuivant(Node suivant) {
		this.suivant = suivant; 
	}
	
	public void setEstPasser(boolean estPasser) {
		this.estPasser = estPasser; 
	}
	
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
	public <R extends Serializable> void mapSync(String computationUri, SelectorI selector, ProcessorI<R> processor) throws Exception {
		if (this.suivant.estPasser) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		}
		this.memoryTable.put(computationUri, ((Stream<ContentDataI>) this.tableHachage.values().stream()
		.filter(((Predicate<ContentDataI>) selector))
		.map(processor)));
		this.suivant.mapSync(computationUri, selector, processor);
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String computationUri, ReductorI<A, R> reductor, CombinatorI<A> combinator, A filteredMap)
			throws Exception {
		if (this.suivant.estPasser) {
			return this.memoryTable.get(computationUri).reduce(filteredMap, (u,d) -> reductor.apply(u,(R) d), combinator);
		}
		return combinator.apply(memoryTable.get(computationUri).reduce(filteredMap, (u,d) -> reductor.apply(u,(R) d), combinator), this.suivant.reduceSync(computationUri, reductor, combinator, filteredMap));
	}

	@Override
	public void clearComputation(String arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ContentDataI getSync(String attribute, ContentKeyI key) throws Exception {
		
		if (this.contains(key)) {
			return tableHachage.get(key.hashCode());
		} else if(this.suivant.estPasser) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else {
			return suivant.getSync(attribute, key);
		}
	}

	@Override
	public ContentDataI putSync(String attribute, ContentKeyI key, ContentDataI data) throws Exception {
		if (this.contains(key)) {
			return tableHachage.put(key.hashCode(), data);
		} else if(this.suivant.estPasser) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else{
			return suivant.putSync(attribute, key, data);
		}
	}

	@Override
	public ContentDataI removeSync(String attribute, ContentKeyI key) throws Exception {
		if (this.contains(key)) {
			return tableHachage.remove(key.hashCode());
		} else if(this.suivant.estPasser) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		}else {
			return suivant.removeSync(attribute, key);
		}
	}
}

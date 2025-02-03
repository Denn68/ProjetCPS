package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.sorbonne_u.components.endpoints.POJOEndPoint;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.mapreduce.endpoints.POJOContentNodeCompositeEndPoint;

public class Node 
implements ContentAccessSyncI, MapReduceSyncI{

	public Node (int min, int max, POJOContentNodeCompositeEndPoint endPointServer, POJOContentNodeCompositeEndPoint endPointClient) {
		this.intervalMin = min;
		this.intervalMax = max;
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
		this.memoryTable = new HashMap<>();
		this.listOfUri = new ArrayList<String>();
		endPointServer.initialiseServerSide(this);
		this.mapReduceClient = endPointClient.getMapReduceEndpoint();
		this.contentAccessClient = endPointClient.getContentAccessEndpoint();
		this.mapReduceClient.initialiseClientSide(endPointClient);
		this.contentAccessClient.initialiseClientSide(endPointClient);
		
	}
	
	private HashMap<Integer, ContentDataI> tableHachage;
	private HashMap<String, Stream<ContentDataI>> memoryTable;
	private int intervalMin;
	private int intervalMax;
	private Node suivant;
	private POJOEndPoint <MapReduceSyncI> mapReduceClient;
	private POJOEndPoint <ContentAccessSyncI> contentAccessClient;
	private List<String> listOfUri;

	
	
	public void setSuivant(Node suivant) {
		this.suivant = suivant; 
	}
	
	public boolean contains(ContentKeyI arg0) {
		if(arg0.hashCode() >= intervalMin && arg0.hashCode() <= intervalMax) {
			return true;
		}
		return false;
	}

	@Override
	public void clearMapReduceComputation(String computationUri) throws Exception {
		this.listOfUri.remove(computationUri);
		if(this.suivant.listOfUri.contains(computationUri)) {
			this.suivant.clearMapReduceComputation(computationUri);
		}
	}

	@Override
	public <R extends Serializable> void mapSync(String computationUri, SelectorI selector, ProcessorI<R> processor) throws Exception {
		this.memoryTable.put(computationUri, ((Stream<ContentDataI>) this.tableHachage.values().stream()
		.filter(((Predicate<ContentDataI>) selector))
		.map(processor)));
		this.listOfUri.add(computationUri);
		if (this.suivant.listOfUri.contains(computationUri)) {
			return ;
		}
		this.suivant.mapSync(computationUri, selector, processor);
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String computationUri, ReductorI<A, R> reductor, CombinatorI<A> combinator, A filteredMap)
			throws Exception {
		this.listOfUri.add(computationUri);
		if (this.suivant.listOfUri.contains(computationUri)) {
			return this.memoryTable.get(computationUri).reduce(filteredMap, (u,d) -> reductor.apply(u,(R) d), combinator);
		}
		return combinator.apply(memoryTable.get(computationUri).reduce(filteredMap, (u,d) -> reductor.apply(u,(R) d), combinator), this.suivant.reduceSync(computationUri, reductor, combinator, filteredMap));
	}

	@Override
	public void clearComputation(String computationUri) throws Exception {
		this.listOfUri.remove(computationUri);
		if(this.suivant.listOfUri.contains(computationUri)) {
			this.suivant.clearMapReduceComputation(computationUri);
		}
	}

	@Override
	public ContentDataI getSync(String computationUri, ContentKeyI key) throws Exception {
		
		if (this.contains(key)) {
			return tableHachage.get(key.hashCode());
		} else if(this.suivant.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else {
			this.listOfUri.add(computationUri);
			return suivant.getSync(computationUri, key);
		}
	}

	@Override
	public ContentDataI putSync(String computationUri, ContentKeyI key, ContentDataI data) throws Exception {
		if (this.contains(key)) {
			return tableHachage.put(key.hashCode(), data);
		} else if(this.suivant.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else{
			this.listOfUri.add(computationUri);
			return suivant.putSync(computationUri, key, data);
		}
	}

	@Override
	public ContentDataI removeSync(String computationUri, ContentKeyI key) throws Exception {
		if (this.contains(key)) {
			return tableHachage.remove(key.hashCode());
		} else if(this.suivant.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		}else {
			this.listOfUri.add(computationUri);
			return suivant.removeSync(computationUri, key);
		}
	}
}

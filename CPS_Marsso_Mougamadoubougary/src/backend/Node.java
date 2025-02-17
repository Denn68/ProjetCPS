package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class Node 
extends AbstractComponent
implements ContentAccessSyncCI, MapReduceSyncCI{

	protected Node(int nbThreads, int nbSchedulableThreads, int min, int max, BCMCompositeEndPoint endPointServer, BCMCompositeEndPoint endPointClient) {
		super(nbThreads, nbSchedulableThreads);
		this.intervalMin = min;
		this.intervalMax = max;
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
		this.memoryTable = new HashMap<>();
		this.listOfUri = new ArrayList<String>();
		endPointServer.initialiseServerSide(this);
		this.endPointClient = endPointClient;
	}
	
	private HashMap<Integer, ContentDataI> tableHachage;
	private HashMap<String, Stream<ContentDataI>> memoryTable;
	private int intervalMin;
	private int intervalMax;
	private List<String> listOfUri;
	private BCMCompositeEndPoint endPointClient;
	
	public boolean contains(ContentKeyI arg0) {
		if(arg0.hashCode() >= intervalMin && arg0.hashCode() <= intervalMax) {
			return true;
		}
		return false;
	}

	@Override
	public void clearMapReduceComputation(String computationUri) throws Exception {
		if(!endPointClient.clientSideInitialised()) {
			endPointClient.initialiseClientSide(endPointClient);
		}
		if(this.listOfUri.contains(computationUri)) {
			this.listOfUri.remove(computationUri);
			this.endPointClient.getEndPoint(MapReduceSyncCI.class).getClientSideReference().clearMapReduceComputation(computationUri);
		}
	}

	@Override
	public <R extends Serializable> void mapSync(String computationUri, SelectorI selector, ProcessorI<R> processor) throws Exception {
		if(!endPointClient.clientSideInitialised()) {
			endPointClient.initialiseClientSide(endPointClient);
		}
		if(this.listOfUri.contains(computationUri)) {
			return;
		}
		this.memoryTable.put(computationUri, ((Stream<ContentDataI>) this.tableHachage.values().stream()
		.filter(((Predicate<ContentDataI>) selector))
		.map(processor)));
		this.listOfUri.add(computationUri);
		this.endPointClient.getEndPoint(MapReduceSyncCI.class).getClientSideReference().mapSync(computationUri, selector, processor);
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String computationUri, ReductorI<A, R> reductor, CombinatorI<A> combinator, A filteredMap)
			throws Exception {
		if(this.listOfUri.contains(computationUri)) {
			return filteredMap;
		}
		if(!endPointClient.clientSideInitialised()) {
			endPointClient.initialiseClientSide(endPointClient);
		}
		this.listOfUri.add(computationUri);
		return combinator.apply(memoryTable.get(computationUri).reduce(filteredMap, (u,d) -> reductor.apply(u,(R) d), combinator), 
				this.endPointClient.getEndPoint(MapReduceSyncCI.class).getClientSideReference().reduceSync(computationUri, reductor, combinator, filteredMap));
	}

	@Override
	public void clearComputation(String computationUri) throws Exception {
		if(!endPointClient.clientSideInitialised()) {
			endPointClient.initialiseClientSide(endPointClient);
		}
		if(this.listOfUri.contains(computationUri)) {
			this.listOfUri.remove(computationUri);
			this.endPointClient.getEndPoint(MapReduceSyncCI.class).getClientSideReference().clearMapReduceComputation(computationUri);
		}
	}

	@Override
	public ContentDataI getSync(String computationUri, ContentKeyI key) throws Exception {
		if(!endPointClient.clientSideInitialised()) {
			endPointClient.initialiseClientSide(endPointClient);
		}
		if (this.contains(key)) {
			return tableHachage.get(key.hashCode());
		} else if(this.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else {
			this.listOfUri.add(computationUri);
			return this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().getSync(computationUri, key);
		}
	}

	@Override
	public ContentDataI putSync(String computationUri, ContentKeyI key, ContentDataI data) throws Exception {
		if(!endPointClient.clientSideInitialised()) {
			endPointClient.initialiseClientSide(endPointClient);
		}
		if (this.contains(key)) {
			return tableHachage.put(key.hashCode(), data);
		} else if(this.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else{
			this.listOfUri.add(computationUri);
			return this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().putSync(computationUri, key, data);
		}
	}

	@Override
	public ContentDataI removeSync(String computationUri, ContentKeyI key) throws Exception {
		if(!endPointClient.clientSideInitialised()) {
			endPointClient.initialiseClientSide(endPointClient);
		}
		if (this.contains(key)) {
			return tableHachage.remove(key.hashCode());
		} else if(this.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		}else {
			this.listOfUri.add(computationUri);
			return this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().removeSync(computationUri, key);
		}
	}
}

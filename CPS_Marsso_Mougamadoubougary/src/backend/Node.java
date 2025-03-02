package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import frontend.DHTServicesEndpoint;

@OfferedInterfaces(offered = { ContentAccessSyncCI.class, MapReduceSyncCI.class })
@RequiredInterfaces(required = { ContentAccessSyncCI.class, MapReduceSyncCI.class })
public class Node 
extends AbstractComponent
implements ContentAccessSyncCI, MapReduceSyncCI{

	public Node(int nbThreads, int nbSchedulableThreads, int min, int max, 
			CompositeEndPoint compositeEndPointServer, CompositeEndPoint compositeEndPointClient) throws ConnectionException {
		super(nbThreads, nbSchedulableThreads);
		this.intervalMin = min;
		this.intervalMax = max;
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
		this.memoryTable = new HashMap<>();
		this.listOfUri = new ArrayList<String>();
		compositeEndPointServer.initialiseServerSide(this);
		this.compositeEndPointClient = compositeEndPointClient;
	}
	
	private HashMap<Integer, ContentDataI> tableHachage;
	private HashMap<String, Stream<ContentDataI>> memoryTable;
	private int intervalMin;
	private int intervalMax;
	private List<String> listOfUri;
	private CompositeEndPoint compositeEndPointClient;
	
	public boolean contains(ContentKeyI arg0) {
		if(arg0.hashCode() >= intervalMin && arg0.hashCode() <= intervalMax) {
			return true;
		}
		return false;
	}

	@Override
	public void clearMapReduceComputation(String computationUri) throws Exception {
		if(!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getMapReduceEndpoint().initialiseClientSide(this);
		}
		if(this.listOfUri.contains(computationUri)) {
			this.listOfUri.remove(computationUri);
			this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().clearMapReduceComputation(computationUri);
		}
	}

	@Override
	public <R extends Serializable> void mapSync(String computationUri, SelectorI selector, ProcessorI<R> processor) throws Exception {
		if(!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getMapReduceEndpoint().initialiseClientSide(this);
		}
		if(this.listOfUri.contains(computationUri)) {
			return;
		}
		this.memoryTable.put(computationUri, ((Stream<ContentDataI>) this.tableHachage.values().stream()
		.filter(((Predicate<ContentDataI>) selector))
		.map(processor)));
		this.listOfUri.add(computationUri);
		this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().mapSync(computationUri, selector, processor);
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String computationUri, ReductorI<A, R> reductor, CombinatorI<A> combinator, A filteredMap)
			throws Exception {
		if(this.listOfUri.contains(computationUri)) {
			return filteredMap;
		}
		if(!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getMapReduceEndpoint().initialiseClientSide(this);
		}
		this.listOfUri.add(computationUri);
		return combinator.apply(memoryTable.get(computationUri).reduce(filteredMap, (u,d) -> reductor.apply(u,(R) d), combinator), 
				this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().reduceSync(computationUri, reductor, combinator, filteredMap));
	}

	@Override
	public void clearComputation(String computationUri) throws Exception {
		if(!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getMapReduceEndpoint().initialiseClientSide(this);
		}
		if(this.listOfUri.contains(computationUri)) {
			this.listOfUri.remove(computationUri);
			this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().clearMapReduceComputation(computationUri);
		}
	}

	@Override
	public ContentDataI getSync(String computationUri, ContentKeyI key) throws Exception {
		if(!this.compositeEndPointClient.getContentAccessEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getContentAccessEndpoint().initialiseClientSide(this);
		}
		if (this.contains(key)) {
			return tableHachage.get(key.hashCode());
		} else if(this.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else {
			this.listOfUri.add(computationUri);
			return this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().getSync(computationUri, key);
		}
	}

	@Override
	public ContentDataI putSync(String computationUri, ContentKeyI key, ContentDataI data) throws Exception {
		if(!this.compositeEndPointClient.getContentAccessEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getContentAccessEndpoint().initialiseClientSide(this);
		}
		if (this.contains(key)) {
			return tableHachage.put(key.hashCode(), data);
		} else if(this.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else{
			this.listOfUri.add(computationUri);
			return this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().putSync(computationUri, key, data);
		}
	}

	@Override
	public ContentDataI removeSync(String computationUri, ContentKeyI key) throws Exception {
		if(!this.compositeEndPointClient.getContentAccessEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getContentAccessEndpoint().initialiseClientSide(this);
		}
		if (this.contains(key)) {
			return tableHachage.remove(key.hashCode());
		} else if(this.listOfUri.contains(computationUri)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		}else {
			this.listOfUri.add(computationUri);
			return this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().removeSync(computationUri, key);
		}
	}
}

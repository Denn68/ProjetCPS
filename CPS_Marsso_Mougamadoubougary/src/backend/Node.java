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
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import frontend.DHTServicesEndpoint;

@OfferedInterfaces(offered = { ContentAccessCI.class, MapReduceCI.class})
@RequiredInterfaces(required = { ContentAccessCI.class, MapReduceCI.class, ResultReceptionCI.class, MapReduceResultReceptionCI.class })
public class Node 
extends AbstractComponent
implements ContentAccessI, MapReduceI{

	protected Node(int nbThreads, int nbSchedulableThreads, int min, int max, 
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
	public void start() {
		try {
			super.start();
		} catch (ComponentStartException e) {
			e.printStackTrace();
		}
		if(!this.compositeEndPointClient.clientSideInitialised()) {
			try {
				this.compositeEndPointClient.initialiseClientSide(this);
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!this.compositeEndPointClient.getContentAccessEndpoint().clientSideInitialised()) {
			try {
				this.compositeEndPointClient.getContentAccessEndpoint().initialiseClientSide(this);
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
			try {
				this.compositeEndPointClient.getMapReduceEndpoint().initialiseClientSide(this);
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	public ContentDataI getSync(String computationURI, ContentKeyI key) throws Exception {
		if(!this.compositeEndPointClient.getContentAccessEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getContentAccessEndpoint().initialiseClientSide(this);
		}
		if (this.contains(key)) {
			return tableHachage.get(key.hashCode());
		} else if(this.listOfUri.contains(computationURI)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else {
			this.listOfUri.add(computationURI);
			return this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().getSync(computationURI, key);
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

	@Override
	public <R extends Serializable, I extends MapReduceResultReceptionCI> void map(String computationURI,
			SelectorI selector, ProcessorI<R> processor) throws Exception {
		if(!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getMapReduceEndpoint().initialiseClientSide(this);
		}
		if(this.listOfUri.contains(computationURI)) {
			return;
		}
		this.memoryTable.put(computationURI, ((Stream<ContentDataI>) this.tableHachage.values().stream()
		.filter(((Predicate<ContentDataI>) selector))
		.map(processor)));
		this.listOfUri.add(computationURI);
		this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().map(computationURI, selector, processor);
		
	}

	@Override
	public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void reduce(String computationURI,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc, EndPointI<I> callerNode)
			throws Exception {
		if(!callerNode.clientSideInitialised()) {
			callerNode.initialiseClientSide(this);
		}
		if(this.listOfUri.contains(computationURI)) {
			return filteredMap;
		}
		if(!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
			this.compositeEndPointClient.getMapReduceEndpoint().initialiseClientSide(this);
		}
		this.listOfUri.add(computationURI);
		return combinator.apply(memoryTable.get(computationURI).reduce(filteredMap, (u,d) -> reductor.apply(u,(R) d), combinator), 
				this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().reduceSync(computationUri, reductor, combinator, filteredMap));
		
	}

	@Override
	public <I extends ResultReceptionCI> void put(String computationURI, ContentKeyI key, ContentDataI data,
			EndPointI<I> caller) throws Exception {
		if(!caller.clientSideInitialised()) {
			caller.initialiseClientSide(this);
		}
		if (this.contains(key)) {
			caller.getClientSideReference().acceptResult(computationURI, this.tableHachage.put(key.hashCode(), data));
		} else if(this.listOfUri.contains(computationURI)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else{
			this.listOfUri.add(computationURI);
			this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().put(computationURI, key, data, caller);
		}
	}

	@Override
	public <I extends ResultReceptionCI> void remove(String computationURI, ContentKeyI key, EndPointI<I> caller)
			throws Exception {
		if(!caller.clientSideInitialised()) {
			caller.initialiseClientSide(this);
		}
		if (this.contains(key)) {
			caller.getClientSideReference().acceptResult(computationURI, this.tableHachage.remove(key.hashCode()));
		} else if(this.listOfUri.contains(computationURI)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else{
			this.listOfUri.add(computationURI);
			this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().remove(computationURI, key, caller);
		}
		
	}

	@Override
	public <I extends ResultReceptionCI> void get(String computationURI, ContentKeyI key, EndPointI<I> caller) 
			throws Exception {
		if(!caller.clientSideInitialised()) {
			caller.initialiseClientSide(this);
		}
		if (this.contains(key)) {
			caller.getClientSideReference().acceptResult(computationURI, this.tableHachage.get(key.hashCode()));
		} else if(this.listOfUri.contains(computationURI)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else{
			this.listOfUri.add(computationURI);
			this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().get(computationURI, key, caller);
		}
	}
}

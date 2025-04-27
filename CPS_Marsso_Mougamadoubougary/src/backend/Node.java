package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints.ContentNodeCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.LoadPolicyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.mapreduce.utils.SerializablePair;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;

@OfferedInterfaces(offered = { ContentAccessCI.class, MapReduceCI.class, MapReduceResultReceptionCI.class, ParallelMapReduceCI.class})
@RequiredInterfaces(required = { ContentAccessCI.class, MapReduceCI.class, ResultReceptionCI.class, MapReduceResultReceptionCI.class, ParallelMapReduceCI.class })
public class Node
extends AbstractComponent
implements ContentAccessI, MapReduceI, MapReduceResultReceptionI, ParallelMapReduceI, DHTManagementI{
	
	public static final String			MAPREDUCE_RESULT_HANDLER_URI = "mrh" ;
	public static final String			MAPREDUCE_HANDLER_URI = "mh" ;
	public static final String			CONTENT_ACCESS_HANDLER_URI = "cah" ;

	protected Node(int nbThreads, int nbSchedulableThreads, int min, int max, 
			CompositeEndPoint compositeEndPointServer,
			CompositeEndPoint compositeEndPointClient, 
			MapReduceResultReceptionEndpoint mapReduceResultEndPointServer) throws ConnectionException {
		super(nbThreads, nbSchedulableThreads);
		this.intervalMin = min;
		this.intervalMax = max;
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
		this.memoryTable = new HashMap<>();
		this.listOfUri = new ArrayList<String>();
		this.listOfMapReduceUri = new ArrayList<String>();
		
		this.createNewExecutorService(MAPREDUCE_RESULT_HANDLER_URI, 10, false);
		this.createNewExecutorService(MAPREDUCE_HANDLER_URI, 10, false);
		this.createNewExecutorService(CONTENT_ACCESS_HANDLER_URI, 10, false);
		mapReduceResultEndPointServer.setExecutorServiceIndex(this.getExecutorServiceIndex(MAPREDUCE_RESULT_HANDLER_URI));
		compositeEndPointServer.setContentAccessExecutorServiceIndex(this.getExecutorServiceIndex(CONTENT_ACCESS_HANDLER_URI));
		compositeEndPointServer.setMapReduceExecutorServiceIndex(this.getExecutorServiceIndex(MAPREDUCE_HANDLER_URI));
			
		compositeEndPointServer.initialiseServerSide(this);
		mapReduceResultEndPointServer.initialiseServerSide(this);
		this.mapResultEndPoint = mapReduceResultEndPointServer;
		this.compositeEndPointClient = compositeEndPointClient;
	}
	
	private final ConcurrentHashMap<String, CompletableFuture<Void>> clearCompletion = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<Void>> mapCompletion = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<Serializable>> reduceCompletion = new ConcurrentHashMap<>();
	private HashMap<Integer, ContentDataI> tableHachage;
	private HashMap<String, Stream<? extends Serializable>> memoryTable;
	private int intervalMin;
	private int intervalMax;
	private List<String> listOfUri;
	private List<String> listOfMapReduceUri;
	private CompositeEndPoint compositeEndPointClient;
	private MapReduceResultReceptionEndpoint mapResultEndPoint;
	private String id;
	
	public boolean contains(ContentKeyI arg0) {
		if(arg0.hashCode() >= intervalMin && arg0.hashCode() <= intervalMax) {
			return true;
		}
		return false;
	}
	
	@Override
	public void start() throws ComponentStartException {
	    super.start();

	    try {
	        if (!compositeEndPointClient.clientSideInitialised()) {
	            compositeEndPointClient.initialiseClientSide(this);
	        }

	        ContentAccessEndpoint contentAccessEndpoint = (ContentAccessEndpoint) compositeEndPointClient.getContentAccessEndpoint();
	        if (!contentAccessEndpoint.clientSideInitialised()) {
	            contentAccessEndpoint.initialiseClientSide(this);
	        }

	        MapReduceEndpoint mapReduceEndpoint = (MapReduceEndpoint) compositeEndPointClient.getMapReduceEndpoint();
	        if (!mapReduceEndpoint.clientSideInitialised()) {
	            mapReduceEndpoint.initialiseClientSide(this);
	        }

	        id = URIGenerator.generateURI();

	    } catch (Exception e) {
	        throw new ComponentStartException("Erreur lors de l'initialisation des clients", e);
	    }
	}



	@Override
	public void clearMapReduceComputation(String computationUri) throws Exception {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
	        try {
	            if (this.memoryTable.containsKey(computationUri)) {
	                this.memoryTable.remove(computationUri);
	                this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().clearMapReduceComputation(computationUri);
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("Erreur dans clearMapReduceComputation", e);
	        }
	    });

	    clearCompletion.put(computationUri, future);
	}

	@Override
	public <R extends Serializable> void mapSync(
	        String computationUri,
	        SelectorI selector,
	        ProcessorI<R> processor
	) throws Exception {
		if (this.listOfMapReduceUri.add(computationUri)) {
            Stream<R> resultStream = this.tableHachage.values().stream()
                    .filter(selector)
                    .map(processor);
            this.memoryTable.put(computationUri, resultStream);

            this.compositeEndPointClient.getMapReduceEndpoint()
                    .getClientSideReference()
                    .mapSync(computationUri, selector, processor);
        }
	}


	@Override
	public <A extends Serializable, R> A reduceSync(
	        String computationUri,
	        ReductorI<A, R> reductor,
	        CombinatorI<A> combinator,
	        A filteredMap
	) throws Exception {
	    if (this.listOfMapReduceUri.remove(computationUri)) {
	        A localResult = memoryTable.get(computationUri)
	                .reduce(filteredMap, (u, d) -> reductor.apply(u, (R) d), combinator);

	        A remoteResult = this.compositeEndPointClient
	                .getMapReduceEndpoint()
	                .getClientSideReference()
	                .reduceSync(computationUri, reductor, combinator, filteredMap);

	        return combinator.apply(localResult, remoteResult);
	    } else {
	        return filteredMap;
	    }
	}


	@Override
	public void clearComputation(String computationUri) throws Exception {
		if(this.listOfUri.contains(computationUri)) {
			this.listOfUri.remove(computationUri);
			this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().clearMapReduceComputation(computationUri);
		}
	}
	
	@Override
	public ContentDataI getSync(String computationURI, ContentKeyI key) throws Exception {
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
	public <R extends Serializable> void map(String computationURI, SelectorI selector, ProcessorI<R> processor)
			throws Exception {
		clearCompletion.getOrDefault(computationURI, CompletableFuture.completedFuture(null))
        .thenRun(() -> {
            try {
                if (this.listOfMapReduceUri.contains(computationURI)) {
                    return;
                }
                this.listOfMapReduceUri.add(computationURI);
                
                this.memoryTable.put(computationURI, this.tableHachage.values().stream()
                        .filter(((Predicate<ContentDataI>) selector))
                        .map(processor));
                
                CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                mapCompletion.put(this.id + computationURI, future);
                this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().map(computationURI, selector, processor);
            } catch (Exception e) {
                throw new RuntimeException("Erreur dans map()", e);
            }
        });
	}

	@Override
	public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void reduce(String computationURI,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc, EndPointI<I> caller)
			throws Exception {
	    mapCompletion.getOrDefault(computationURI, CompletableFuture.completedFuture(null))
	        .thenRun(() -> {
	            try {
	                if (!caller.clientSideInitialised()) {
	                    caller.initialiseClientSide(this);
	                }
	                if (this.mapCompletion.containsKey(this.id + computationURI)) {
	                    
	                	A res1 = memoryTable.get(computationURI).reduce(currentAcc, (u,d) -> reductor.apply(u,(R) d), combinator);
	                	this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().reduce(computationURI, reductor, combinator, identityAcc, currentAcc, this.mapResultEndPoint);
	                	this.mapCompletion.remove(this.id + computationURI);
	                	
	                	CompletableFuture<Serializable> future = new CompletableFuture<>();
	                	reduceCompletion.put(computationURI + this.id, future);
	                    
	                	A res2 = (A) future.get();
	                	
	                	caller.getClientSideReference().acceptResult(computationURI, this.id, 
	                			combinator.apply(res1, res2));

	                    caller.cleanUpClientSide();	                    
	                } else {
	                	caller.getClientSideReference().acceptResult(computationURI, this.id, currentAcc);
	                    caller.cleanUpClientSide();
	                }
	            } catch (Exception e) {
	                throw new RuntimeException("Erreur dans reduce()", e);
	            }
	        });
	}

	@Override
	public <I extends ResultReceptionCI> void put(String computationURI, ContentKeyI key, ContentDataI data,
			EndPointI<I> caller) throws Exception {
		if(!caller.clientSideInitialised()) {
			caller.initialiseClientSide(this);
		}
		if (this.contains(key)) {
			caller.getClientSideReference().acceptResult(computationURI, this.tableHachage.put(key.hashCode(), data));
			caller.cleanUpClientSide();
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
			caller.cleanUpClientSide();
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
			caller.cleanUpClientSide();
		} else if(this.listOfUri.contains(computationURI)) {
			throw new IllegalArgumentException("La clé n'est pas dans l'intervalle de la table");
		} else{
			this.listOfUri.add(computationURI);
			this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().get(computationURI, key, caller);
		}
	}
	
	@Override
	public void acceptResult(String computationURI, String emitterId, Serializable acc) throws Exception {
	    CompletableFuture<Serializable> future = (CompletableFuture<Serializable>) this.reduceCompletion.remove(computationURI + this.id);
	    future.complete(acc);
	    this.mapCompletion.remove(this.id + computationURI);
	}
	
	@Override
	public <R extends Serializable> void parallelMap(String computationURI, SelectorI selector, ProcessorI<R> processor,
			ParallelismPolicyI parallelismPolicy) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void parallelReduce(String computationURI,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc,
			ParallelismPolicyI parallelismPolicy, EndPointI<I> caller) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialiseContent(NodeContentI content) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NodeStateI getCurrentState() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeContentI suppressNode() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <CI extends ResultReceptionCI> void split(String computationURI, LoadPolicyI loadPolicy,
			EndPointI<CI> caller) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <CI extends ResultReceptionCI> void merge(String computationURI, LoadPolicyI loadPolicy,
			EndPointI<CI> caller) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void computeChords(String computationURI, int numberOfChords) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SerializablePair<ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI>, Integer> getChordInfo(
			int offset) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
}

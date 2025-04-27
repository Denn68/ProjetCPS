package frontend;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import backend.CompositeEndPoint;
import backend.CompositeMapContentManagementEndpoint;
import backend.LoadParallelismPolicy;
import backend.MapReduceResultReceptionEndpoint;
import backend.ParallelismPolicy;
import backend.ResultReceptionEndpoint;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.LoadPolicyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;

@OfferedInterfaces(offered = { DHTServicesCI.class, MapReduceResultReceptionCI.class, ResultReceptionCI.class })
@RequiredInterfaces(required = { ContentAccessCI.class, ParallelMapReduceCI.class, DHTManagementCI.class })
public class Facade
extends AbstractComponent
implements DHTServicesI, MapReduceResultReceptionI, ResultReceptionI{
	
	public static final String			CONTENT_ACCESS_RESULT_HANDLER_URI = "carh" ;
	public static final String			MAPREDUCE_RESULT_HANDLER_URI = "mrh" ;
	public static final String			MAPREDUCE_METHOD = "mapreduce" ;
	public static final String			GET_METHOD = "get" ;
	public static final String			PUT_METHOD = "put" ;
	public static final String			REMOVE_METHOD = "remove" ;
	public static final String 			MERGE_METHOD = "merge";
	public static final String 			SPLIT_METHOD = "split";
	public static final String 			COMPUTE_CHORD_METHOD = "compute-chords";

	protected Facade(int nbThreads,
			int nbSchedulableThreads, 
			DHTServicesEndpoint dhtEndPointServer,
			CompositeMapContentManagementEndpoint compositeEndPointClient,
			ResultReceptionEndpoint contentResultEndPointServer,
			MapReduceResultReceptionEndpoint mapReduceResultEndPointServer) throws ConnectionException {
		
		super(nbThreads, nbSchedulableThreads);
		dhtEndPointServer.initialiseServerSide(this);
		
		this.createNewExecutorService(CONTENT_ACCESS_RESULT_HANDLER_URI, 10, false);
		this.createNewExecutorService(MAPREDUCE_RESULT_HANDLER_URI, 10, false);
		contentResultEndPointServer.setExecutorServiceIndex(this.getExecutorServiceIndex(CONTENT_ACCESS_RESULT_HANDLER_URI));
		mapReduceResultEndPointServer.setExecutorServiceIndex(this.getExecutorServiceIndex(MAPREDUCE_RESULT_HANDLER_URI));
		
		contentResultEndPointServer.initialiseServerSide(this);
		mapReduceResultEndPointServer.initialiseServerSide(this);
		this.contentResultEndPointServer = contentResultEndPointServer;
		this.mapReduceResultEndPointServer = mapReduceResultEndPointServer;
		this.compositeEndPointClient = compositeEndPointClient;
		this.contentMemoryTable = new ConcurrentHashMap<>();
		this.mapMemoryTable = new ConcurrentHashMap<>();
		this.getExecutorServiceIndex(STANDARD_REQUEST_HANDLER_URI);
		
		this.lockSplitAndMerge = new ReentrantReadWriteLock();
	}
	
	private Map<String, CompletableFuture<Serializable>> contentMemoryTable;
	private Map<String, CompletableFuture<Serializable>> mapMemoryTable;
	private ResultReceptionEndpoint contentResultEndPointServer;
	private MapReduceResultReceptionEndpoint mapReduceResultEndPointServer;
	protected final ReentrantReadWriteLock lockSplitAndMerge;
	
	@Override
	public void start() throws ComponentStartException {
	    super.start();

	    try {
	        if (!this.compositeEndPointClient.clientSideInitialised()) {
	            this.compositeEndPointClient.initialiseClientSide(this);
	        }

	        if (!this.compositeEndPointClient.getContentAccessEndpoint().clientSideInitialised()) {
	            this.compositeEndPointClient.getContentAccessEndpoint().initialiseClientSide(this);
	        }

	        if (!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
	            this.compositeEndPointClient.getMapReduceEndpoint().initialiseClientSide(this);
	        }
	        
	        if (!this.compositeEndPointClient.getMapReduceEndpoint().clientSideInitialised()) {
	            this.compositeEndPointClient.getDHTManagementEndpoint().initialiseClientSide(this);
	        }

	    } catch (Exception e) {
	        throw new ComponentStartException("Erreur lors de l'initialisation des clients", e);
	    }
	}

	private CompositeMapContentManagementEndpoint compositeEndPointClient;
	
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		this.lockSplitAndMerge.readLock().lock();
		String uri = URIGenerator.generateURI(GET_METHOD);
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    this.contentMemoryTable.put(uri, future);
	    
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			clearComputation(uri);
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			get(uri, key, ((EndPointI<ResultReceptionCI>) this.contentResultEndPointServer));
		
		
        try {
        	this.lockSplitAndMerge.readLock().unlock();
            return (ContentDataI) future.get(); 
        } catch (InterruptedException | ExecutionException e) {
        	throw new Exception("Échec de la récupération du contenu pour la clé : " + key, e);
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
		ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
		this.lockSplitAndMerge.readLock().lock();
		String uri = URIGenerator.generateURI(MAPREDUCE_METHOD);
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    this.mapMemoryTable.put(uri, future);
	    
		this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().
			parallelMap(uri, selector, processor, new ParallelismPolicy(0));
		this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().
			parallelReduce(uri, reductor, combinator, identity, identity, new ParallelismPolicy(0),
					this.mapReduceResultEndPointServer);

        try {
        	A res = (A) future.get();
        	this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().
        		clearMapReduceComputation(uri);
        	this.lockSplitAndMerge.readLock().unlock();
            return res; 
        } catch (InterruptedException | ExecutionException e) {
        	throw new Exception("Échec de MapReduce", e); 
        }
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		this.lockSplitAndMerge.readLock().lock();
		String uri = URIGenerator.generateURI(PUT_METHOD);
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    synchronized (this.contentMemoryTable) {
	        this.contentMemoryTable.put(uri, future);
	    }
	    
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			clearComputation(uri);
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			put(uri, key, data, ((EndPointI<ResultReceptionCI>) this.contentResultEndPointServer));
		
        try {
        	this.lockSplitAndMerge.readLock().unlock();
            return (ContentDataI) future.get(); 
        } catch (InterruptedException | ExecutionException e) {
        	throw new Exception("Échec de l'insertion du contenu pour la clé : " + key, e); 
        }
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		this.lockSplitAndMerge.readLock().lock();
		String uri = URIGenerator.generateURI(REMOVE_METHOD);
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    this.contentMemoryTable.put(uri, future);

		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			clearComputation(uri);
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			remove(uri, key, ((EndPointI<ResultReceptionCI>) this.contentResultEndPointServer));
		
		
        try {
        	this.lockSplitAndMerge.readLock().unlock();
            return (ContentDataI) future.get(); 
        } catch (InterruptedException | ExecutionException e) {
        	throw new Exception("Échec du retrait du contenu pour la clé : " + key, e); 
        }
	}

	@Override
	public void acceptResult(String computationURI, Serializable result) throws Exception {
		synchronized (this.contentMemoryTable) {
	        if (this.contentMemoryTable.containsKey(computationURI) && this.contentMemoryTable.get(computationURI) instanceof CompletableFuture) {
	            CompletableFuture<Serializable> future = (CompletableFuture<Serializable>) this.contentMemoryTable.remove(computationURI);
	            future.complete((ContentDataI) result);
	        } 
	        else {
	        	System.out.println("L'uri n'existe pas");
	        }
		}
	}

	@Override
	public void acceptResult(String computationURI, String emitterId, Serializable acc) throws Exception {
		synchronized (this.mapMemoryTable) {
	        if (this.mapMemoryTable.containsKey(computationURI) && this.mapMemoryTable.get(computationURI) instanceof CompletableFuture) {
	            CompletableFuture<Serializable> future = (CompletableFuture<Serializable>) this.mapMemoryTable.remove(computationURI);
	            future.complete(acc);
	        } 
	        else {
	        	System.out.println("L'uri n'existe pas");
	        }
		}
	}
	
	
	public void split() throws Exception {
		this.lockSplitAndMerge.readLock().lock();
		String splitUri = URIGenerator.generateURI(SPLIT_METHOD);
		try {
			CompletableFuture<Serializable> f = new CompletableFuture<Serializable>();
			
			this.contentMemoryTable.put(splitUri, f);
			this.compositeEndPointClient.getDHTManagementEndpoint().getClientSideReference().split(splitUri, new LoadParallelismPolicy(), 
					this.contentResultEndPointServer);

			this.contentMemoryTable.get(splitUri);
			
			this.compositeEndPointClient.getDHTManagementEndpoint().getClientSideReference().computeChords(URIGenerator.generateURI(COMPUTE_CHORD_METHOD), 4);
		}finally {
			this.contentMemoryTable.remove(splitUri);
			this.lockSplitAndMerge.readLock().unlock();
		}


	}
	
	public void merge() throws Exception {
		this.lockSplitAndMerge.writeLock().lock();
		String mergeUri = URIGenerator.generateURI(MERGE_METHOD);
		try {
			CompletableFuture<Serializable> f = new CompletableFuture<Serializable>();
			
			this.contentMemoryTable.put(mergeUri, f);
			this.compositeEndPointClient.getDHTManagementEndpoint().getClientSideReference().merge(mergeUri, new LoadParallelismPolicy(),
					this.contentResultEndPointServer);
			this.contentMemoryTable.get(mergeUri).get();
			
			this.compositeEndPointClient.getDHTManagementEndpoint().getClientSideReference()
			.computeChords(URIGenerator.generateURI(COMPUTE_CHORD_METHOD), 4);
		}finally {
			this.contentMemoryTable.remove(mergeUri);
			this.lockSplitAndMerge.readLock().unlock();
		}

	}

}

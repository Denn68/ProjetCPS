package frontend;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import backend.CompositeEndPoint;
import backend.MapReduceResultReceptionEndpoint;
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
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;

@OfferedInterfaces(offered = { DHTServicesCI.class, MapReduceResultReceptionCI.class, ResultReceptionCI.class })
@RequiredInterfaces(required = { ContentAccessCI.class, MapReduceCI.class })
public class Facade
extends AbstractComponent
implements DHTServicesI, MapReduceResultReceptionI, ResultReceptionI{
	
	public static final String			CONTENT_ACCESS_RESULT_HANDLER_URI = "carh" ;
	public static final String			MAPREDUCE_RESULT_HANDLER_URI = "mrh" ;
	public static final String			MAPREDUCE_METHOD = "mapreduce" ;
	public static final String			GET_METHOD = "get" ;
	public static final String			PUT_METHOD = "put" ;
	public static final String			REMOVE_METHOD = "remove" ;

	protected Facade(int nbThreads,
			int nbSchedulableThreads, 
			DHTServicesEndpoint dhtEndPointServer,
			CompositeEndPoint compositeEndPointClient,
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
	}
	
	private Map<String, CompletableFuture<Serializable>> contentMemoryTable;
	private Map<String, CompletableFuture<Serializable>> mapMemoryTable;
	private ResultReceptionEndpoint contentResultEndPointServer;
	private MapReduceResultReceptionEndpoint mapReduceResultEndPointServer;
	
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

	    } catch (Exception e) {
	        throw new ComponentStartException("Erreur lors de l'initialisation des clients", e);
	    }
	}

	private CompositeEndPoint compositeEndPointClient;
	
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		String uri = URIGenerator.generateURI(GET_METHOD);
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    this.contentMemoryTable.put(uri, future);
	    
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			clearComputation(uri);
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			get(uri, key, ((EndPointI<ResultReceptionCI>) this.contentResultEndPointServer));
		
		
        try {
            return (ContentDataI) future.get(); 
        } catch (InterruptedException | ExecutionException e) {
        	throw new Exception("Échec de la récupération du contenu pour la clé : " + key, e);
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
		ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
		String uri = URIGenerator.generateURI(MAPREDUCE_METHOD);
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    this.mapMemoryTable.put(uri, future);
	    
		this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().
			map(uri, selector, processor);
		this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().
			reduce(uri, reductor, combinator, identity, identity, this.mapReduceResultEndPointServer);

        try {
        	A res = (A) future.get();
        	this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().
        		clearMapReduceComputation(uri);
            return res; 
        } catch (InterruptedException | ExecutionException e) {
        	throw new Exception("Échec de MapReduce", e); 
        }
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
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
            return (ContentDataI) future.get(); 
        } catch (InterruptedException | ExecutionException e) {
        	throw new Exception("Échec de l'insertion du contenu pour la clé : " + key, e); 
        }
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		String uri = URIGenerator.generateURI(REMOVE_METHOD);
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    this.contentMemoryTable.put(uri, future);

		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			clearComputation(uri);
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().
			remove(uri, key, ((EndPointI<ResultReceptionCI>) this.contentResultEndPointServer));
		
		
        try {
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

}

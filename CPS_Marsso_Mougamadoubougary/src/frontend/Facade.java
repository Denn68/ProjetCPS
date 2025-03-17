package frontend;

import java.io.Serializable;
import java.util.HashMap;
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

	protected Facade(int nbThreads, int nbSchedulableThreads, 
			DHTServicesEndpoint dhtEndPointServer, CompositeEndPoint compositeEndPointClient,
			ResultReceptionEndpoint contentResultEndPointServer, MapReduceResultReceptionEndpoint mapReduceResultEndPointServer) throws ConnectionException {
		super(nbThreads, nbSchedulableThreads);
		dhtEndPointServer.initialiseServerSide(this);
		contentResultEndPointServer.initialiseServerSide(this);
		mapReduceResultEndPointServer.initialiseServerSide(this);
		this.contentResultEndPointServer = contentResultEndPointServer;
		this.mapReduceResultEndPointServer = mapReduceResultEndPointServer;
		this.compositeEndPointClient = compositeEndPointClient;
		this.contentMemoryTable = new HashMap<>();
		this.mapMemoryTable = new HashMap<>();
	}
	
	private final ConcurrentHashMap<String, CompletableFuture<ContentDataI>> pendingRequests = new ConcurrentHashMap<>();
	private HashMap<String, CompletableFuture<Serializable>> contentMemoryTable;
	private HashMap<String, CompletableFuture<Serializable>> mapMemoryTable;
	private ResultReceptionEndpoint contentResultEndPointServer;
	private MapReduceResultReceptionEndpoint mapReduceResultEndPointServer;
	
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
	private CompositeEndPoint compositeEndPointClient;
	
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		String uri = URIGenerator.generateURI("get");
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().clearComputation(uri);
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().get(uri, key, ((EndPointI<ResultReceptionCI>) this.contentResultEndPointServer));
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    synchronized (this.contentMemoryTable) {
	        this.contentMemoryTable.put(uri, future);
	    }

        try {
            return (ContentDataI) future.get(); 
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null; 
        }
	}

	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
			String uri = URIGenerator.generateURI("mapreduce");
			this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().clearMapReduceComputation(uri);
			this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().mapSync(uri, selector, processor);
			return this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().reduceSync(uri, reductor, combinator, identity);
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		String uri = URIGenerator.generateURI("put");
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().clearComputation(uri);
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().put(uri, key, data, ((EndPointI<ResultReceptionCI>) this.contentResultEndPointServer));
		

	    CompletableFuture<Serializable> future = new CompletableFuture<>();

	    synchronized (this.contentMemoryTable) {
	        this.contentMemoryTable.put(uri, future);
	    }

        try {
            return (ContentDataI) future.get(); 
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null; 
        }
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		String uri = URIGenerator.generateURI("remove");
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().clearComputation(uri);
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().remove(uri, key, ((EndPointI<ResultReceptionCI>) this.contentResultEndPointServer));
		
		CompletableFuture<Serializable> future = new CompletableFuture<>();

	    synchronized (this.contentMemoryTable) {
	        this.contentMemoryTable.put(uri, future);
	    }

        try {
            return (ContentDataI) future.get(); 
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null; 
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
		// TODO Auto-generated method stub
		
	}

}

package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationCI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints.ContentNodeCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.LoadPolicyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI.NodeContentI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI.NodeStateI;
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
import fr.sorbonne_u.cps.mapreduce.utils.IntInterval;
import fr.sorbonne_u.cps.mapreduce.utils.SerializablePair;
import fr.sorbonne_u.cps.mapreduce.utils.URIGenerator;

@OfferedInterfaces(offered = { ContentAccessCI.class, MapReduceCI.class, ResultReceptionCI.class, MapReduceResultReceptionCI.class, ParallelMapReduceCI.class, DHTManagementCI.class, DynamicComponentCreationCI.class})
@RequiredInterfaces(required = { ContentAccessCI.class, MapReduceCI.class, ResultReceptionCI.class, MapReduceResultReceptionCI.class, ParallelMapReduceCI.class })
public class Node
extends AbstractComponent
implements ContentAccessI, MapReduceI, MapReduceResultReceptionI, ParallelMapReduceI, DHTManagementI{
	
	private boolean parallel;
	
	public static final String			MAPREDUCE_RESULT_HANDLER_URI = "mrh" ;
	public static final String			MAPREDUCE_HANDLER_URI = "mh" ;
	public static final String			CONTENT_ACCESS_HANDLER_URI = "cah" ;

	protected Node(int nbThreads, int nbSchedulableThreads, int min, int max, 
			CompositeEndPoint compositeEndPointServer,
			CompositeEndPoint compositeEndPointClient, 
			MapReduceResultReceptionEndpoint mapReduceResultEndPointServer) throws ConnectionException {
		super(nbThreads, nbSchedulableThreads);
		this.interval = new IntInterval(min, max);
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
		this.memoryTable = new HashMap<>();
		this.listOfUri = new ArrayList<String>();
		this.listOfMapReduceUri = new ArrayList<String>();

		this.mapReduceLock = new ReentrantReadWriteLock();
		this.splitLock = new ReentrantReadWriteLock();
		this.hashMapLock = new ReentrantReadWriteLock();
		
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
		
		this.parallel = false;
	}
	

	private CompositeEndPoint compositeEndPointClient;
	private MapReduceResultReceptionEndpoint mapResultEndPoint;
	
	// Etape 4 constructor
	
	public static final String			MAPREDUCE_PARALLEL_HANDLER_URI = "mrph" ;
	public static final String			DHT_PARALLEL_HANDLER_URI = "dph" ;
	public static final String			CONTENT_ACCESS_PARALLEL_HANDLER_URI = "caph" ;
	private String JVMUri;
	protected final ReentrantReadWriteLock mapReduceLock;
	protected final ReentrantReadWriteLock splitLock;
	protected final  ReentrantReadWriteLock hashMapLock;
	
	protected Node(String JVMUri, int nbThreads, int nbSchedulableThreads, int min, int max, 
			CompositeMapContentManagementEndpoint compositeEndPointManagementServer,
			CompositeMapContentManagementEndpoint compositeEndPointManagementClient,
			MapReduceResultReceptionEndpoint mapReduceResultEndPointServer) throws ConnectionException {
		super(nbThreads, nbSchedulableThreads);
		this.interval = new IntInterval(min, max);
		this.tableHachage = new HashMap<Integer, ContentDataI>(max-min);
		this.mapReduceReception = new HashMap<String, ArrayList<CompletableFuture<Serializable>>>();
		this.memoryTable = new HashMap<>();
		this.listOfUri = new ArrayList<String>();
		this.listOfMapReduceUri = new ArrayList<String>();
		this.mapReduceLock = new ReentrantReadWriteLock();
		this.splitLock = new ReentrantReadWriteLock();
		this.hashMapLock = new ReentrantReadWriteLock();
		
		this.compositeEndPointManagementClient = compositeEndPointManagementClient;
		this.compositeEndPointManagementServer = compositeEndPointManagementServer;
		this.mapReduceResultEndPointServer = mapReduceResultEndPointServer;
		
		this.compositeEndPointManagementServer.setContentAccessExecutorServiceIndex(
				this.createNewExecutorService(CONTENT_ACCESS_PARALLEL_HANDLER_URI, 10, true));
		
		this.compositeEndPointManagementServer.setDHTManagementExecutorServiceIndex(
				this.createNewExecutorService(DHT_PARALLEL_HANDLER_URI, 10, true));
		
		this.compositeEndPointManagementServer.setMapReduceExecutorServiceIndex(
				this.createNewExecutorService(MAPREDUCE_PARALLEL_HANDLER_URI, 10, true));
		
		this.mapReduceResultEndPointServer.setExecutorServiceIndex(
				this.createNewExecutorService(MAPREDUCE_RESULT_HANDLER_URI, 10, true));
		
		this.compositeEndPointManagementServer.initialiseServerSide(this);
		this.mapReduceResultEndPointServer.initialiseServerSide(this);
		
		this.parallel = true;
		this.JVMUri = JVMUri;
		
	}
	
	CompositeMapContentManagementEndpoint compositeEndPointManagementServer;
	CompositeMapContentManagementEndpoint compositeEndPointManagementClient;
	MapReduceResultReceptionEndpoint mapReduceResultEndPointServer;
	
	private final ConcurrentHashMap<String, CompletableFuture<Void>> clearCompletion = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<Void>> mapCompletion = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<Serializable>> reduceCompletion = new ConcurrentHashMap<>();
	private HashMap<Integer, ContentDataI> tableHachage;
	private HashMap<String, Stream<? extends Serializable>> memoryTable;
	private HashMap<String, ArrayList<CompletableFuture<Serializable>>> mapReduceReception;
	private IntInterval interval;
	private List<String> listOfUri;
	private List<String> listOfMapReduceUri;
	private String id;
	private DynamicComponentCreationOutboundPort porttoNewNode;

	private ArrayList<SerializablePair<ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI>, Integer>> chords;
	
	public boolean contains(ContentKeyI arg0) {
		return this.interval.in(arg0.hashCode());
	}
	
	@Override
	public void start() throws ComponentStartException {
	    super.start();
	    
	    if(this.parallel) {
	    	try {
		        if (!compositeEndPointManagementClient.clientSideInitialised()) {
		        	compositeEndPointManagementClient.initialiseClientSide(this);
		        }
		        
		        this.porttoNewNode = new DynamicComponentCreationOutboundPort(this);
				this.porttoNewNode.localPublishPort();
				this.doPortConnection(this.porttoNewNode.getPortURI(), this.JVMUri + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
						DynamicComponentCreationConnector.class.getCanonicalName());
				this.chords = new ArrayList<SerializablePair<ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI>, Integer>>();

				AbstractComponent.checkImplementationInvariant(this);
				AbstractComponent.checkInvariant(this);

	
		        id = URIGenerator.generateURI();
	
		    } catch (Exception e) {
		        throw new ComponentStartException("Erreur lors de l'initialisation des clients", e);
		    }
		} else {
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
	
	protected class NodeContent implements NodeContentI {

	    private static final long serialVersionUID = 1L;

	    public HashMap<Integer, ContentDataI> content;

	    public IntInterval interval;

	    public String nodeUri;

	    protected CompositeMapContentManagementEndpoint compositeEndPointManagementClient;

	    protected NodeContent(HashMap<Integer, ContentDataI> content, IntInterval interval,
	    		CompositeMapContentManagementEndpoint compositeEndPointManagementClient, String nodeUri) {
	        this.content = content;
	        this.interval = interval;
	        this.nodeUri = nodeUri;
	        this.compositeEndPointManagementClient = compositeEndPointManagementClient;

	    }

	}

	public class NodeState implements NodeStateI {

	    private static final long serialVersionUID = 1L;

	    protected int contentDataSize;

	    public NodeState(int contentDataSize) {
	        this.contentDataSize = contentDataSize;
	    }
	}
	
	@Override
	public <R extends Serializable> void parallelMap(String computationURI, SelectorI selector, ProcessorI<R> processor,
			ParallelismPolicyI parallelismPolicy) throws Exception {
		
		if (!listOfMapReduceUri.contains(computationURI)) {
			listOfMapReduceUri.add(computationURI);
			
			int nbChords = ((ParallelismPolicy) parallelismPolicy).getNbreChords();

			int debut = Math.min(chords.size(), nbChords);

			ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI> next_endpoint;

			int increment = 1;

			for (int i = debut; i < chords.size(); i++) {
				next_endpoint = chords.get(i).first();

				this.mapReduceLock.writeLock().lock();
				if (!next_endpoint.clientSideInitialised()) {
					next_endpoint.initialiseClientSide(this);
				}

				next_endpoint.getMapReduceEndpoint().getClientSideReference().parallelMap(computationURI, selector,
						processor,	new ParallelismPolicy(nbChords + increment));

				increment++;
				next_endpoint.cleanUpClientSide();
				this.mapReduceLock.writeLock().unlock();

			}

			this.hashMapLock.readLock().lock();
			try {

				CompletableFuture<Serializable> futureStream = new CompletableFuture<Serializable>();
				reduceCompletion.put(computationURI, futureStream);
				reduceCompletion.get(computationURI)
						.complete((Serializable) tableHachage.values().stream().filter(selector).map(processor));

			} finally {
				this.hashMapLock.readLock().unlock();
			}

		}
	}

	@Override
	public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void parallelReduce(String computationURI,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc,
			ParallelismPolicyI parallelismPolicy, EndPointI<I> caller) throws Exception {
		if (!listOfMapReduceUri.contains(computationURI)) {
			listOfMapReduceUri.add(computationURI);

			Stream<? extends Serializable> futureStream = null;
			memoryTable.putIfAbsent(computationURI, futureStream);

			A localReduce = memoryTable.get(computationURI).reduce(currentAcc, (u,d) -> reductor.apply(u,(R) d), combinator);
			localReduce = combinator.apply(currentAcc, localReduce);

			if (!(parallelismPolicy instanceof ParallelismPolicy)) {
				throw new IllegalArgumentException("Unsupported parallelism policy");
			}

			int debut = Math.min(chords.size(), ((ParallelismPolicy) parallelismPolicy).getNbreChords());

			ArrayList<CompletableFuture<Serializable>> listeResults = new ArrayList<CompletableFuture<Serializable>>();

			mapReduceReception.put(computationURI, listeResults);

			ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI> next_endpoint;

			int increment = 1;

			this.mapReduceLock.writeLock().lock();

			for (int i = debut; i < chords.size(); i++) {

				mapReduceReception.get(computationURI).add(new CompletableFuture<Serializable>());
				next_endpoint = chords.get(i).first();

				if (!next_endpoint.clientSideInitialised()) {
					next_endpoint.initialiseClientSide(this);
				}

				next_endpoint.getMapReduceEndpoint().getClientSideReference().parallelReduce(computationURI, reductor,
						combinator, identityAcc, identityAcc,
						new ParallelismPolicy(((ParallelismPolicy) parallelismPolicy).getNbreChords() + increment),
						this.mapReduceResultEndPointServer);
				increment++;
				next_endpoint.cleanUpClientSide();

			}
			this.mapReduceLock.writeLock().unlock();

			for (CompletableFuture<Serializable> result : mapReduceReception.get(computationURI)) {
				@SuppressWarnings("unchecked")
				A resTemporaire = (A) result.get();
				localReduce = combinator.apply(resTemporaire, localReduce);
			}

			if (!caller.clientSideInitialised()) {
				caller.initialiseClientSide(this);
			}

			caller.getClientSideReference().acceptResult(computationURI, "nom du noeud qui envoie", localReduce);
			
			caller.cleanUpClientSide();

		}
	}

	@Override
	public void initialiseContent(NodeContentI content) throws Exception {
		this.tableHachage.putAll(((NodeContent) content).content);
		this.interval = ((NodeContent) content).interval.clone();
	}

	@Override
	public NodeStateI getCurrentState() throws Exception {
		return new NodeState(this.tableHachage.size());
	}

	@Override
	public NodeContentI suppressNode() throws Exception {
		NodeContent nodeContent = new NodeContent(this.tableHachage, this.interval,
				(CompositeMapContentManagementEndpoint) this.compositeEndPointManagementClient
						.copyWithSharable(), this.id);

		this.finalise();
		return nodeContent;
	}

	@Override
	public <CI extends ResultReceptionCI> void split(String computationURI, LoadPolicyI loadPolicy,
			EndPointI<CI> caller) throws Exception {
		if (!listOfUri.contains(computationURI)) {
			listOfUri.add(computationURI);
			
			System.out.println("SPLIT du noeud " + this.interval.first());

			if (loadPolicy.shouldSplitInTwoAdjacentNodes(tableHachage.size())) {

				System.out.println("Fission du noeud " + this.interval.first());

				assert this.porttoNewNode != null;
				assert this.porttoNewNode.connected();

				CompositeMapContentManagementEndpoint nouvelEndpointEntreNoeuds = new CompositeMapContentManagementEndpoint(3);

				String nouveauNoeudURI = this.porttoNewNode.createComponent(Node.class.getCanonicalName(),
						new Object[] { this.JVMUri, 10, 10, -1, 0,
								(CompositeMapContentManagementEndpoint) nouvelEndpointEntreNoeuds.copyWithSharable(),
								(CompositeMapContentManagementEndpoint) this.compositeEndPointManagementClient
										.copyWithSharable() });
				this.compositeEndPointManagementClient.cleanUpClientSide();
				this.compositeEndPointManagementClient = (CompositeMapContentManagementEndpoint) nouvelEndpointEntreNoeuds
						.copyWithSharable();

				this.compositeEndPointManagementClient.initialiseClientSide(this);

				HashMap<Integer, ContentDataI> firstPart = new HashMap<>();
				HashMap<Integer, ContentDataI> secondPart = new HashMap<>();

				int mid = (int) ((this.interval.first() + this.interval.last()) / 2.0);

				for (Map.Entry<Integer, ContentDataI> entry : this.tableHachage.entrySet()) {
					if (entry.getKey() < mid + 1) {
						firstPart.put(entry.getKey(), entry.getValue());
					} else {
						secondPart.put(entry.getKey(), entry.getValue());
					}
				}

				this.tableHachage = firstPart;
				NodeContent contenuSplit = new NodeContent(secondPart, interval.split(), null, this.id);

				this.porttoNewNode.startComponent(nouveauNoeudURI);

				this.compositeEndPointManagementClient.getDHTManagementEndpoint().getClientSideReference()
						.initialiseContent(contenuSplit);

			}
			this.compositeEndPointManagementClient.getDHTManagementEndpoint().getClientSideReference()
					.split(computationURI, loadPolicy, caller);

		} else {
			if (!caller.clientSideInitialised()) {
				caller.initialiseClientSide(this);
			}
			
			caller.getClientSideReference().acceptResult(computationURI, null);
			caller.cleanUpClientSide();
		}
	}

	@Override
	public <CI extends ResultReceptionCI> void merge(String computationURI, LoadPolicyI loadPolicy,
			EndPointI<CI> caller) throws Exception {
		System.out.println("Merge du noeud " + this.interval.first());
		
		if (!listOfUri.contains(computationURI)) {
			listOfUri.add(computationURI);
			
			if (!(this.getChordInfo(1).second() < this.interval.first())) {
				NodeState stateSuivant = (NodeState) this.compositeEndPointManagementClient
						.getDHTManagementEndpoint().getClientSideReference().getCurrentState();

				if (loadPolicy.shouldMergeWithNextNode(tableHachage.size(), stateSuivant.contentDataSize)) {

					NodeContent contentSuivant = (NodeContent) this.compositeEndPointManagementClient
							.getDHTManagementEndpoint().getClientSideReference().suppressNode();

					System.out.println("Fusion du noeud: " + this.interval.first() + " et du noeud: " + contentSuivant.nodeUri );
					this.tableHachage.putAll(contentSuivant.content);

					this.interval.merge(contentSuivant.interval);
					
					this.compositeEndPointManagementClient.cleanUpClientSide();

					this.compositeEndPointManagementClient = (CompositeMapContentManagementEndpoint) contentSuivant.compositeEndPointManagementClient
							.copyWithSharable();

					this.compositeEndPointManagementClient.initialiseClientSide(this);

				}
			}
			
			this.compositeEndPointManagementClient.getDHTManagementEndpoint().getClientSideReference()
					.merge(computationURI, loadPolicy, caller);

		} else {
			if (!caller.clientSideInitialised()) {
				caller.initialiseClientSide(this);
			}
			caller.getClientSideReference().acceptResult(computationURI, null);
			caller.cleanUpClientSide();
		}
	}

	@Override
	public void computeChords(String computationURI, int numberOfChords) throws Exception {
		if (!listOfUri.contains(computationURI)) {
			listOfUri.add(computationURI);
			this.chords.clear();
			int offset = 1;
			
			for (int i = 0; i < numberOfChords; i++) {
				if (this.getChordInfo(offset).second() >= this.interval.first()) {
					this.chords.add(this.getChordInfo(offset));
				}
				offset = offset * 2;
			}
			this.compositeEndPointManagementClient.getDHTManagementEndpoint().getClientSideReference()
					.computeChords(computationURI, numberOfChords);
		}
	}

	@Override
	public SerializablePair<ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI>, Integer> getChordInfo(
			int offset) throws Exception {
		if (offset > 0) {
			return this.compositeEndPointManagementClient.getDHTManagementEndpoint()
					.getClientSideReference().getChordInfo(offset - 1);
		}
		return new SerializablePair<ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI>, Integer>(
				(CompositeMapContentManagementEndpoint) this.compositeEndPointManagementServer
						.copyWithSharable(),
				this.interval.first());
	}

	
}

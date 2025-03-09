package frontend;

import java.io.Serializable;

import backend.CompositeEndPoint;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

@OfferedInterfaces(offered = { DHTServicesCI.class })
@RequiredInterfaces(required = { ContentAccessSyncCI.class, MapReduceSyncCI.class })
public class Facade
extends AbstractComponent
implements DHTServicesI{

	protected Facade(int nbThreads, int nbSchedulableThreads, 
			DHTServicesEndpoint dhtEndPointServer, CompositeEndPoint compositeEndPointClient) throws ConnectionException {
		super(nbThreads, nbSchedulableThreads);
		dhtEndPointServer.initialiseServerSide(this);
		this.compositeEndPointClient = compositeEndPointClient;
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
	private CompositeEndPoint compositeEndPointClient;
	
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().clearComputation("get " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI());
		return this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().getSync("get " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI(), key);
	}

	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
			this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().clearMapReduceComputation("mapreduce " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI());
			this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().mapSync("mapreduce " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI(), selector, processor);
			this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().clearMapReduceComputation("mapreduce " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI());
			return this.compositeEndPointClient.getMapReduceEndpoint().getClientSideReference().reduceSync("mapreduce " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI(), reductor, combinator, identity);
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().clearComputation("put " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI());
		return this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().putSync("put " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI(), key, data); 
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().clearComputation("remove " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI());
		return this.compositeEndPointClient.getContentAccessEndpoint().getClientSideReference().removeSync("remove " + this.compositeEndPointClient.getContentAccessEndpoint().getInboundPortURI(), key);
	}

}

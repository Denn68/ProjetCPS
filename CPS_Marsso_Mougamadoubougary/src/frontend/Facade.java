package frontend;

import java.io.Serializable;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class Facade
extends AbstractComponent
implements DHTServicesCI{

	protected Facade(int nbThreads, int nbSchedulableThreads, BCMCompositeEndPoint clientEndPoint) {
		super(nbThreads, nbSchedulableThreads);
		clientEndPoint.initialiseClientSide(clientEndPoint);
		this.endPointClient = clientEndPoint;
	}
	
	private BCMCompositeEndPoint endPointClient;
	
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().clearComputation("get");
		return this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().getSync("get", key);
	}

	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
			this.endPointClient.getEndPoint(MapReduceSyncCI.class).getClientSideReference().clearMapReduceComputation("mapreduce");
			this.endPointClient.getEndPoint(MapReduceSyncCI.class).getClientSideReference().mapSync("mapreduce", selector, processor);
			this.endPointClient.getEndPoint(MapReduceSyncCI.class).getClientSideReference().clearMapReduceComputation("mapreduce");
			return this.endPointClient.getEndPoint(MapReduceSyncCI.class).getClientSideReference().reduceSync("mapreduce", reductor, combinator, identity);
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().clearComputation("put");
		return this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().putSync("put", key, data); 
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().clearComputation("remove");
		return this.endPointClient.getEndPoint(ContentAccessSyncCI.class).getClientSideReference().removeSync("remove", key);
	}

}

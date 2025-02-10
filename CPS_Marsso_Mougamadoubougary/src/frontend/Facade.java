package frontend;

import java.io.Serializable;
import fr.sorbonne_u.components.endpoints.POJOEndPoint;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.mapreduce.endpoints.POJOContentNodeCompositeEndPoint;

public class Facade 
implements DHTServicesCI{

	public Facade (POJOContentNodeCompositeEndPoint clientEndPoint) {
		clientEndPoint.initialiseClientSide(clientEndPoint);
		this.mapReduceClient = clientEndPoint.getMapReduceEndpoint();
		this.contentAccessClient = clientEndPoint.getContentAccessEndpoint();
	}
	
	private POJOEndPoint<ContentAccessSyncI> contentAccessClient;
	private POJOEndPoint<MapReduceSyncI> mapReduceClient;
	
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		this.contentAccessClient.getClientSideReference().clearComputation("get");
		return this.contentAccessClient.getClientSideReference().getSync("get", key);
	}

	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
			this.mapReduceClient.getClientSideReference().clearMapReduceComputation("mapreduce");
			this.mapReduceClient.getClientSideReference().mapSync("mapreduce", selector, processor);
			this.mapReduceClient.getClientSideReference().clearMapReduceComputation("mapreduce");
			return this.mapReduceClient.getClientSideReference().reduceSync("mapreduce", reductor, combinator, identity);
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		this.contentAccessClient.getClientSideReference().clearComputation("put");
		return this.contentAccessClient.getClientSideReference().putSync("put", key, data); 
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		this.contentAccessClient.getClientSideReference().clearComputation("remove");
		return this.contentAccessClient.getClientSideReference().removeSync("remove", key);
	}

}

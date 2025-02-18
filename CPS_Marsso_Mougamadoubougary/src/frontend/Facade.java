package frontend;

import java.io.Serializable;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class Facade
extends AbstractComponent
implements DHTServicesCI{

	public Facade(int nbThreads, int nbSchedulableThreads, DHTContentEndpoint contentEndPoint, DHTMapReduceEndpoint mapReduceEndPoint) {
		super(nbThreads, nbSchedulableThreads);
		contentEndPoint.initialiseClientSide(contentEndPoint);
		mapReduceEndPoint.initialiseClientSide(mapReduceEndPoint);
		this.contentEndPointClient = contentEndPoint;
		this.mapReduceEndPointClient = mapReduceEndPoint;
	}
	
	private DHTContentEndpoint contentEndPointClient;
	private DHTMapReduceEndpoint mapReduceEndPointClient;
	
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		this.contentEndPointClient.getClientSideReference().clearComputation("get");
		return this.contentEndPointClient.getClientSideReference().getSync("get", key);
	}

	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
			this.mapReduceEndPointClient.getClientSideReference().clearMapReduceComputation("mapreduce");
			this.mapReduceEndPointClient.getClientSideReference().mapSync("mapreduce", selector, processor);
			this.mapReduceEndPointClient.getClientSideReference().clearMapReduceComputation("mapreduce");
			return this.mapReduceEndPointClient.getClientSideReference().reduceSync("mapreduce", reductor, combinator, identity);
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		this.contentEndPointClient.getClientSideReference().clearComputation("put");
		return this.contentEndPointClient.getClientSideReference().putSync("put", key, data); 
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		this.contentEndPointClient.getClientSideReference().clearComputation("remove");
		return this.contentEndPointClient.getClientSideReference().removeSync("remove", key);
	}

}

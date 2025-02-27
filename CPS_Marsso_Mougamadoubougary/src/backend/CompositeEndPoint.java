package backend;

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;

public class CompositeEndPoint 
extends BCMCompositeEndPoint{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompositeEndPoint(int nbEndPoint) {
		super(nbEndPoint);
		ContentAccessEndpoint contentAccessEndpoint = new ContentAccessEndpoint();
		this.addEndPoint(contentAccessEndpoint);
		MapReduceEndpoint mapReduceEndpoint = new MapReduceEndpoint();
		this.addEndPoint(mapReduceEndpoint);
	}
	
	public ContentAccessEndpoint getContentAccessEndpoint() {
		return (ContentAccessEndpoint) this.getEndPoint(ContentAccessSyncCI.class);
	}
	
	public MapReduceEndpoint getMapReduceEndpoint() {
		return (MapReduceEndpoint) this.getEndPoint(MapReduceSyncCI.class);
	}

}

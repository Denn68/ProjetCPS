package backend;

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;

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
		return (ContentAccessEndpoint) this.getEndPoint(ContentAccessCI.class);
	}
	
	public MapReduceEndpoint getMapReduceEndpoint() {
		return (MapReduceEndpoint) this.getEndPoint(MapReduceCI.class);
	}

}

package backend;

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.components.endpoints.EndPointI;
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
	
	public EndPointI<ContentAccessCI> getContentAccessEndpoint() {
		return this.getEndPoint(ContentAccessCI.class);
	}
	
	public EndPointI<MapReduceCI> getMapReduceEndpoint() {
		return this.getEndPoint(MapReduceCI.class);
	}
	
	public void setContentAccessExecutorServiceIndex(int contentAccessExecutorServiceIndex) {
		((ContentAccessEndpoint) this.retrieveEndPoint(ContentAccessCI.class)).setExecutorServiceIndex(contentAccessExecutorServiceIndex);
	}
	
	public void setMapReduceExecutorServiceIndex(int mapReduceExecutorServiceIndex) {
		((MapReduceEndpoint) this.retrieveEndPoint(MapReduceCI.class)).setExecutorServiceIndex(mapReduceExecutorServiceIndex);
	}

}

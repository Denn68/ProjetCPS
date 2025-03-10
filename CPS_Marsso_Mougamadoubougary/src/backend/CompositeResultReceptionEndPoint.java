package backend;

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;

public class CompositeResultReceptionEndPoint 
extends BCMCompositeEndPoint{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompositeResultReceptionEndPoint(int nbEndPoint) {
		super(nbEndPoint);
		ResultReceptionEndpoint contentAccessEndpoint = new ResultReceptionEndpoint();
		this.addEndPoint(contentAccessEndpoint);
		MapReduceResultReceptionEndpoint mapReduceEndpoint = new MapReduceResultReceptionEndpoint();
		this.addEndPoint(mapReduceEndpoint);
	}
	
	public ResultReceptionEndpoint getContentAccessEndpoint() {
		return (ResultReceptionEndpoint) this.getEndPoint(ResultReceptionCI.class);
	}
	
	public MapReduceResultReceptionEndpoint getMapReduceEndpoint() {
		return (MapReduceResultReceptionEndpoint) this.getEndPoint(MapReduceResultReceptionCI.class);
	}

}

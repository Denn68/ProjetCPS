package backend;

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints.ContentNodeCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import frontend.DHTManagementEndpoint;

public class CompositeMapContentManagementEndpoint
extends BCMCompositeEndPoint
implements ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ContentAccessEndpoint contentAccessEndpoint;
	private ParallelMapReduceEndpoint mapReduceEndpoint;
	private DHTManagementEndpoint DHTManagementEndpoint;

	public CompositeMapContentManagementEndpoint(int numberOfEndPoints) {
		super(numberOfEndPoints);
		this.contentAccessEndpoint = new ContentAccessEndpoint();
		this.addEndPoint(this.contentAccessEndpoint);
		this.mapReduceEndpoint = new ParallelMapReduceEndpoint();
		this.addEndPoint(this.mapReduceEndpoint);
		this.DHTManagementEndpoint = new DHTManagementEndpoint();
		this.addEndPoint(this.DHTManagementEndpoint);
	}

	@Override
	public EndPointI<DHTManagementCI> getDHTManagementEndpoint() {
		return this.getEndPoint(DHTManagementCI.class);
	}
	
	@Override
	public EndPointI<ContentAccessCI> getContentAccessEndpoint() {
		return this.getEndPoint(ContentAccessCI.class);
	}
	
	@Override
	public EndPointI<ParallelMapReduceCI> getMapReduceEndpoint() {
		return this.getEndPoint(ParallelMapReduceCI.class);
	}
	
	public void setContentAccessExecutorServiceIndex(int contentAccessExecutorServiceIndex) {
		((ContentAccessEndpoint) this.retrieveEndPoint(ContentAccessCI.class)).setExecutorServiceIndex(contentAccessExecutorServiceIndex);
	}
	
	public void setMapReduceExecutorServiceIndex(int mapReduceExecutorServiceIndex) {
		((ParallelMapReduceEndpoint) this.retrieveEndPoint(ParallelMapReduceCI.class)).setExecutorServiceIndex(mapReduceExecutorServiceIndex);
	}
	
	public void setDHTManagementExecutorServiceIndex(int DHTManagementExecutorServiceIndex) {
		((DHTManagementEndpoint) this.retrieveEndPoint(DHTManagementCI.class)).setExecutorServiceIndex(DHTManagementExecutorServiceIndex);
	}

}

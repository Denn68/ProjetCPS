package frontend;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints.ContentNodeCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.LoadPolicyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.mapreduce.utils.SerializablePair;

public class DHTManagementOutboundPort 
extends AbstractOutboundPort
implements DHTManagementCI{

	public DHTManagementOutboundPort(ComponentI owner) throws Exception {
		super(DHTManagementCI.class, owner);
		
		assert owner != null;
	}
	
	public DHTManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, DHTManagementCI.class, owner);

		assert uri != null && owner != null;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void initialiseContent(NodeContentI content) throws Exception {
		((DHTManagementCI) this.getConnector()).initialiseContent(content);
	}

	@Override
	public NodeStateI getCurrentState() throws Exception {
		return ((DHTManagementCI) this.getConnector()).getCurrentState();
	}

	@Override
	public NodeContentI suppressNode() throws Exception {
		return ((DHTManagementCI) this.getConnector()).suppressNode();
	}

	@Override
	public <CI extends ResultReceptionCI> void split(String computationURI, LoadPolicyI loadPolicy,
			EndPointI<CI> caller) throws Exception {
		((DHTManagementCI) this.getConnector()).split(computationURI, loadPolicy, caller);
	}

	@Override
	public <CI extends ResultReceptionCI> void merge(String computationURI, LoadPolicyI loadPolicy,
			EndPointI<CI> caller) throws Exception {
		((DHTManagementCI) this.getConnector()).merge(computationURI, loadPolicy, caller);
	}

	@Override
	public void computeChords(String computationURI, int numberOfChords) throws Exception {
		((DHTManagementCI) this.getConnector()).computeChords(computationURI, numberOfChords);
	}

	@Override
	public SerializablePair<ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI>, Integer> getChordInfo(
			int offset) throws Exception {
		return ((DHTManagementCI) this.getConnector()).getChordInfo(offset);
	}

}

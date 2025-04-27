package frontend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints.ContentNodeCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.LoadPolicyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.cps.mapreduce.utils.SerializablePair;

public class DHTManagementInboundPort 
extends AbstractInboundPort
implements DHTManagementCI{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final int executorIndex;

	public DHTManagementInboundPort(String uri, int executorIndex, ComponentI owner) throws Exception {
		super(uri, DHTManagementCI.class, owner);

		assert	owner.validExecutorServiceIndex(executorIndex) ;

		this.executorIndex = executorIndex ;
	}
	
	public				DHTManagementInboundPort(
			int executorIndex,
			ComponentI owner
			) throws Exception
		{
			super(DHTManagementCI.class, owner);

			assert	owner.validExecutorServiceIndex(executorIndex) ;

			this.executorIndex = executorIndex ;
		}

	@Override
	public void initialiseContent(NodeContentI content) throws Exception {
		this.getOwner().handleRequest(this.executorIndex, owner -> {
			((DHTManagementI) owner).initialiseContent(content);
			return null;
		});
	}

	@Override
	public NodeStateI getCurrentState() throws Exception {
		return this.getOwner().handleRequest(this.executorIndex, owner ->
			((DHTManagementI) owner).getCurrentState()
		);
	}

	@Override
	public NodeContentI suppressNode() throws Exception {
		return this.getOwner().handleRequest(this.executorIndex, owner ->
			((DHTManagementI) owner).suppressNode()
		);
	}

	@Override
	public <CI extends ResultReceptionCI> void split(String computationURI, LoadPolicyI loadPolicy,
			EndPointI<CI> caller) throws Exception {
		this.getOwner().handleRequest(this.executorIndex, owner -> {
			((DHTManagementI) owner).split(computationURI, loadPolicy, caller);
			return null;
		});
	}

	@Override
	public <CI extends ResultReceptionCI> void merge(String computationURI, LoadPolicyI loadPolicy,
			EndPointI<CI> caller) throws Exception {
		this.getOwner().handleRequest(this.executorIndex, owner -> {
			((DHTManagementI) owner).merge(computationURI, loadPolicy, caller);
			return null;
		});
	}

	@Override
	public void computeChords(String computationURI, int numberOfChords) throws Exception {
		this.getOwner().handleRequest(this.executorIndex, owner -> {
			((DHTManagementI) owner).computeChords(computationURI, numberOfChords);
			return null;
		});
	}

	@Override
	public SerializablePair<ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI>, Integer> getChordInfo(
			int offset) throws Exception {
		return this.getOwner().handleRequest(this.executorIndex, owner ->
			((DHTManagementI) owner).getChordInfo(offset)
		);
	}

}

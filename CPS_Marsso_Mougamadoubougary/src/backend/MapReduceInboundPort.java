package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class MapReduceInboundPort 
extends AbstractInboundPort
implements MapReduceSyncCI{

	public MapReduceInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, MapReduceSyncCI.class, owner);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public <R extends Serializable> void mapSync(String computationURI, SelectorI selector, ProcessorI<R> processor)
			throws Exception {
		this.getOwner().handleRequest(owner -> {((Node)owner).mapSync(computationURI, selector, processor); return null;});
		
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String computationURI, ReductorI<A, R> reductor,
			CombinatorI<A> combinator, A currentAcc) throws Exception {
		return this.getOwner().handleRequest(owner -> ((Node)owner).reduceSync(computationURI, reductor, combinator, currentAcc));
	}

	@Override
	public void clearMapReduceComputation(String computationURI) throws Exception {
		this.getOwner().handleRequest(owner -> {((Node)owner).clearMapReduceComputation(computationURI); return null;});
		
	}

}

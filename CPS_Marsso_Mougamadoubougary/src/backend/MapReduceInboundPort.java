package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class MapReduceInboundPort 
extends AbstractInboundPort
implements MapReduceCI{

	//public MapReduceInboundPort(String uri, ComponentI owner) throws Exception {
	//	super(uri, MapReduceCI.class, owner);
	//}
	
	public				MapReduceInboundPort(
			int executorIndex,
			ComponentI owner
			) throws Exception
		{
			super(MapReduceCI.class, owner);

			assert	owner.validExecutorServiceIndex(executorIndex) ;

			this.executorIndex = executorIndex ;
		}
	
	public			MapReduceInboundPort(
			String uri,
			int executorIndex,
			ComponentI owner
			) throws Exception
		{
			super(uri, MapReduceCI.class, owner);
	
			assert	owner.validExecutorServiceIndex(executorIndex) ;
	
			this.executorIndex = executorIndex ;
		}

	private static final long serialVersionUID = 1L;
	protected final int	executorIndex ;

	@Override
	public <R extends Serializable> void mapSync(String computationURI, SelectorI selector, ProcessorI<R> processor)
			throws Exception {
		this.getOwner().handleRequest(owner -> {((MapReduceI)owner).mapSync(computationURI, selector, processor); return null;});
		
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String computationURI, ReductorI<A, R> reductor,
			CombinatorI<A> combinator, A currentAcc) throws Exception {
		return this.getOwner().handleRequest(owner -> ((MapReduceI)owner).reduceSync(computationURI, reductor, combinator, currentAcc));
	}

	@Override
	public void clearMapReduceComputation(String computationURI) throws Exception {
		this.getOwner().handleRequest(owner -> {((MapReduceI)owner).clearMapReduceComputation(computationURI); return null;});
		
	}

	@Override
	public <R extends Serializable> void map(String computationURI, SelectorI selector, ProcessorI<R> processor)
			throws Exception {
		this.getOwner().runTask(
				executorIndex,			// identifies the pool of threads to be used
				owner -> {
			try {
				((MapReduceI)owner).map(computationURI, selector, processor);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Override
	public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void reduce(String computationURI,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc, EndPointI<I> callerNode)
			throws Exception {
		this.getOwner().runTask(
				executorIndex,			// identifies the pool of threads to be used
				owner -> {
			try {
				((MapReduceI)owner).reduce(computationURI, reductor, combinator, identityAcc, currentAcc, callerNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}

	

}

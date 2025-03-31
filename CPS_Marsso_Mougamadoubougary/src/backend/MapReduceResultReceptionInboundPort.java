package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionI;

public class MapReduceResultReceptionInboundPort 
extends AbstractInboundPort
implements MapReduceResultReceptionCI{

	//public MapReduceResultReceptionInboundPort(String uri, ComponentI owner) throws Exception {
	//	super(uri, MapReduceResultReceptionCI.class, owner);
	//}
	
	public				MapReduceResultReceptionInboundPort(
			int executorIndex,
			ComponentI owner
			) throws Exception
		{
			super(MapReduceResultReceptionCI.class, owner);

			assert	owner.validExecutorServiceIndex(executorIndex) ;

			this.executorIndex = executorIndex ;
		}
	
	public			MapReduceResultReceptionInboundPort(
			String uri,
			int executorIndex,
			ComponentI owner
			) throws Exception
		{
			super(uri, MapReduceResultReceptionCI.class, owner);
	
			assert	owner.validExecutorServiceIndex(executorIndex) ;
	
			this.executorIndex = executorIndex ;
		}

	private static final long serialVersionUID = 1L;
	protected final int	executorIndex ;

	@Override
	public void acceptResult(String computationURI, String emitterId, Serializable acc) throws Exception {
		this.getOwner().runTask(
				executorIndex,			// identifies the pool of threads to be used
				owner -> {
			try {
				((MapReduceResultReceptionI)owner).acceptResult(computationURI, emitterId, acc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}

}

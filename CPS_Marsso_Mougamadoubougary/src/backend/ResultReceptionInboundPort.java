package backend;


import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionI;

public class ResultReceptionInboundPort 
extends AbstractInboundPort
implements ResultReceptionCI{

	//public ResultReceptionInboundPort(String uri, ComponentI owner) throws Exception {
	//	super(uri, ResultReceptionCI.class, owner);
	//}
	
	public				ResultReceptionInboundPort(
			int executorIndex,
			ComponentI owner
			) throws Exception
		{
			super(ResultReceptionCI.class, owner);

			assert	owner.validExecutorServiceIndex(executorIndex) ;

			this.executorIndex = executorIndex ;
		}
	
	public			ResultReceptionInboundPort(
			String uri,
			int executorIndex,
			ComponentI owner
			) throws Exception
		{
			super(uri, ResultReceptionCI.class, owner);
	
			assert	owner.validExecutorServiceIndex(executorIndex) ;
	
			this.executorIndex = executorIndex ;
		}

	private static final long serialVersionUID = 1L;
	protected final int	executorIndex ;

	@Override
	public void acceptResult(String computationURI, Serializable result) throws Exception {
		this.getOwner().runTask(
				executorIndex,			// identifies the pool of threads to be used
				owner -> {
			try {
				((ResultReceptionI)owner).acceptResult(computationURI, result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});	
		
	}


}

package backend;


import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import frontend.Facade;

public class ResultReceptionInboundPort 
extends AbstractInboundPort
implements ResultReceptionCI{

	public ResultReceptionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ResultReceptionCI.class, owner);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void acceptResult(String computationURI, Serializable result) throws Exception {
		this.getOwner().runTask(owner -> {
			try {
				((Facade)owner).acceptResult(computationURI, result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});	
		
	}


}

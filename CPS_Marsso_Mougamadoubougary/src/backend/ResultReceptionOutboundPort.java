package backend;


import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;

public class ResultReceptionOutboundPort 
extends AbstractOutboundPort
implements ResultReceptionCI{

	public ResultReceptionOutboundPort(ComponentI owner) throws Exception {
		super(ResultReceptionCI.class, owner);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void acceptResult(String computationURI, Serializable result) throws Exception {
		((ResultReceptionCI) this.getConnector()).acceptResult(computationURI, result);
		
	}

}

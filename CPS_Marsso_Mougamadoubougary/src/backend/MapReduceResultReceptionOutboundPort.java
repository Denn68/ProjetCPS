package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;

public class MapReduceResultReceptionOutboundPort 
extends AbstractOutboundPort
implements MapReduceResultReceptionCI{

	public MapReduceResultReceptionOutboundPort(ComponentI owner) throws Exception {
		super(MapReduceResultReceptionCI.class, owner);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void acceptResult(String computationURI, String emitterId, Serializable acc) throws Exception {
		((MapReduceResultReceptionCI) this.getConnector()).acceptResult(computationURI, emitterId, acc);
		
	}

}

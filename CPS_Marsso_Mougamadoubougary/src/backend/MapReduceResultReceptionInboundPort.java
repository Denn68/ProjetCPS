package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionI;
import frontend.Facade;

public class MapReduceResultReceptionInboundPort 
extends AbstractInboundPort
implements MapReduceResultReceptionCI{

	public MapReduceResultReceptionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, MapReduceResultReceptionCI.class, owner);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void acceptResult(String computationURI, String emitterId, Serializable acc) throws Exception {
		this.getOwner().runTask(owner -> {
			try {
				((MapReduceResultReceptionI)owner).acceptResult(computationURI, emitterId, acc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}

}

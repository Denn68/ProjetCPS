package backend;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;

public class ContentAccessInboundPort 
extends AbstractInboundPort
implements ContentAccessCI{

	public ContentAccessInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ContentAccessCI.class, owner);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public ContentDataI getSync(String computationURI, ContentKeyI key) throws Exception {
		return this.getOwner().handleRequest(
				owner -> ((ContentAccessI)owner).getSync(computationURI, key));
	}

	@Override
	public ContentDataI putSync(String computationURI, ContentKeyI key, ContentDataI value) throws Exception {
		return this.getOwner().handleRequest(
				owner -> ((ContentAccessI)owner).putSync(computationURI, key, value));
	}

	@Override
	public ContentDataI removeSync(String computationURI, ContentKeyI key) throws Exception {
		return this.getOwner().handleRequest(
				owner -> ((ContentAccessI)owner).removeSync(computationURI, key));
	}

	@Override
	public void clearComputation(String computationURI) throws Exception {
		this.getOwner().handleRequest(owner -> {((ContentAccessI)owner).clearComputation(computationURI);return null;});
		
	}

	@Override
	public <I extends ResultReceptionCI> void get(String computationURI, ContentKeyI key, EndPointI<I> caller)
			throws Exception {
		this.getOwner().runTask(owner -> {
			try {
				((ContentAccessI)owner).get(computationURI, key, caller);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});		
	}

	@Override
	public <I extends ResultReceptionCI> void put(String computationURI, ContentKeyI key, ContentDataI value,
			EndPointI<I> caller) throws Exception {
		this.getOwner().runTask(owner -> {
			try {
				((ContentAccessI)owner).put(computationURI, key, value, caller);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});	
		
	}

	@Override
	public <I extends ResultReceptionCI> void remove(String computationURI, ContentKeyI key, EndPointI<I> caller)
			throws Exception {
		this.getOwner().runTask(owner -> {
			try {
				((ContentAccessI)owner).remove(computationURI, key, caller);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});	
		
	}


}

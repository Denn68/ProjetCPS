package backend;


import java.io.Serializable;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;

public class ContentAccessConnector 
extends AbstractConnector
implements ContentAccessCI{
	@Override
	public ContentDataI getSync(String computationURI, ContentKeyI key) throws Exception {
		return ((ContentAccessCI)this.offering).getSync(computationURI, key);
	}

	@Override
	public ContentDataI putSync(String computationURI, ContentKeyI key, ContentDataI value) throws Exception {
		return ((ContentAccessCI)this.offering).putSync(computationURI, key, value);
	}

	@Override
	public ContentDataI removeSync(String computationURI, ContentKeyI key) throws Exception {
		return ((ContentAccessCI)this.offering).removeSync(computationURI, key);
	}

	@Override
	public void clearComputation(String computationURI) throws Exception {
		((ContentAccessCI)this.offering).clearComputation(computationURI);
	}

	@Override
	public <I extends ResultReceptionCI> void get(String computationURI, ContentKeyI key, EndPointI<I> caller)
			throws Exception {
		((ContentAccessCI)this.offering).get(computationURI, key, caller);
		
	}

	@Override
	public <I extends ResultReceptionCI> void put(String computationURI, ContentKeyI key, ContentDataI value,
			EndPointI<I> caller) throws Exception {
		((ContentAccessCI)this.offering).put(computationURI, key, value, caller);
		
	}

	@Override
	public <I extends ResultReceptionCI> void remove(String computationURI, ContentKeyI key, EndPointI<I> caller)
			throws Exception {
		((ContentAccessCI)this.offering).remove(computationURI, key, caller);
		
	}
}

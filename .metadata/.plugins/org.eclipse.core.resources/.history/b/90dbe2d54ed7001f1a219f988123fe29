package backend;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;

public class ContentKey 
implements ContentKeyI{
	
	private static final long serialVersionUID = 1L;

	public ContentKey (int integerKey) {
		this.integerKey = integerKey;
	}
	private int integerKey;
	
	@Override
	public int hashCode() {
		return integerKey % 20;
	}
}

package backend;

import java.util.LinkedList;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;

public class ContentAccessSync 
implements ContentAccessSyncI{
	
	public ContentAccessSync (LinkedList<Node> anneauNoeud) {
		this.anneauNoeud = anneauNoeud;
	}
	
	private LinkedList<Node> anneauNoeud;
	
	@Override
	public void clearComputation(String arg0) throws Exception {
		for (Node noeud: this.anneauNoeud) {
			noeud.clearComputation();
		}
	}

	@Override
	public ContentDataI getSync(String arg0, ContentKeyI arg1) throws Exception {
		for (Node noeud: this.anneauNoeud) {
			if(noeud.contains(arg1)) {
				return noeud.getData(arg1);
			}
		}
		throw new IllegalArgumentException(
				"The key is not in the interval of key: " + arg1);
	}

	@Override
	public ContentDataI putSync(String arg0, ContentKeyI arg1, ContentDataI arg2) throws Exception {
		for (Node noeud: this.anneauNoeud) {
			if(noeud.contains(arg1)) {
				return noeud.putData(arg1, arg2);
			}
		}
		throw new IllegalArgumentException(
				"The key is not in the interval of key: " + arg1);

	}

	@Override
	public ContentDataI removeSync(String arg0, ContentKeyI arg1) throws Exception {
		for (Node noeud: this.anneauNoeud) {
			if(noeud.contains(arg1)) {
				return noeud.removeData(arg1);
			}
		}
		throw new IllegalArgumentException(
				"The key is not in the interval of key: " + arg1);

	}
}

package frontend;

import java.io.Serializable;
import java.util.LinkedList;

import backend.Node;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class Facade 
implements DHTServicesI{

	public Facade (LinkedList<Node> anneauNoeud) {
		this.anneauNoeud = anneauNoeud;
	}
	
	private LinkedList<Node> anneauNoeud;
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		for (Node noeud: this.anneauNoeud) {
			if(noeud.contains(key)) {
				return noeud.getSync("", key);
			}
		}
		throw new IllegalArgumentException(
				"The key is not in the interval of key: " + key);
	}

	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
		for (Node noeud: this.anneauNoeud) {
			noeud.mapSync("", selector, processor);
			return noeud.reduceSync("", reductor, combinator, identity);
		}
		throw new IllegalArgumentException(
				"Je sais pas: ");
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		for (Node noeud: this.anneauNoeud) {
			if(noeud.contains(key)) {
				return noeud.putSync("", key, data);
			}
		}
		throw new IllegalArgumentException(
				"The key is not in the interval of key: " + key);
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		for (Node noeud: this.anneauNoeud) {
			if(noeud.contains(key)) {
				return noeud.removeSync("", key);
			}
		}
		throw new IllegalArgumentException(
				"The key is not in the interval of key: " + key);

	}

}

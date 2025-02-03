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

	public Facade (Node anneauNoeud) {
		this.anneauNoeud = anneauNoeud;
		this.anneauNoeud.setEstPasser(true);
	}
	
	private Node anneauNoeud;
	
	@Override
	public ContentDataI get(ContentKeyI key) throws Exception {
		return this.anneauNoeud.getSync("get", key);
	}

	@Override
	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
			anneauNoeud.mapSync("mapreduce", selector, processor);
			return anneauNoeud.reduceSync("mapreduce", reductor, combinator, identity);
	}

	@Override
	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		return this.anneauNoeud.putSync("put", key, data); 
	}

	@Override
	public ContentDataI remove(ContentKeyI key) throws Exception {
		return this.anneauNoeud.removeSync("remove", key);
	}

}

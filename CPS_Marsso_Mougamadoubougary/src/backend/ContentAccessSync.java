package backend;

import java.io.Serializable;
import java.util.LinkedList;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceSyncI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class ContentAccessSync 
implements ContentAccessSyncI,MapReduceSyncI{
	
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

	@Override
	public void clearMapReduceComputation(String arg0) throws Exception {

	}

	@Override
	public <R extends Serializable> void mapSync(String arg0, SelectorI arg1, ProcessorI<R> arg2) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <A extends Serializable, R> A reduceSync(String arg0, ReductorI<A, R> arg1, CombinatorI<A> arg2, A arg3)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

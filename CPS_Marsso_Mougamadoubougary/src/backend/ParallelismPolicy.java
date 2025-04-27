package backend;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceI.ParallelismPolicyI;

public class ParallelismPolicy implements ParallelismPolicyI {
	
	private static final long serialVersionUID = 1L;
	public int nbreChords;

	public ParallelismPolicy(int nbreChords) {
		this.nbreChords = nbreChords;
	}
	
	public int getNbreChords() {
		return nbreChords;
	}
	
}

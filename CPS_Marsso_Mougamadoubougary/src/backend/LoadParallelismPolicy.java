package backend;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.LoadPolicyI;

public class LoadParallelismPolicy implements LoadPolicyI {
	private static final long serialVersionUID = 1L;

	private static final int CRITICAL_SIZE = 20;
	
	private static final int  MINIMAL_SIZE = 2;
	
	@Override
	public boolean shouldSplitInTwoAdjacentNodes(int currentSize) {
		return currentSize >= CRITICAL_SIZE * 2;
	}
	
	@Override
	public boolean shouldMergeWithNextNode(int thisNodeCurrentSize, int nextNodeCurrentSize) {
		return ((nextNodeCurrentSize + thisNodeCurrentSize) < CRITICAL_SIZE) && thisNodeCurrentSize > MINIMAL_SIZE && nextNodeCurrentSize > MINIMAL_SIZE ;
	}
	
}

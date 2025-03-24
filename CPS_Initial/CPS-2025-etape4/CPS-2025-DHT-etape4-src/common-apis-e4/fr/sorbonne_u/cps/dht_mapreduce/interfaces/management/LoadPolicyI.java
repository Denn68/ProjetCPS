package fr.sorbonne_u.cps.dht_mapreduce.interfaces.management;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an example
// of a component-based distributed application, namely a Distributed Hash Table
// over which a Map/Reduce processing capability is added.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.io.Serializable;

/**
 * The interface <code>LoadPolicyI</code> declares the signatures of the methods
 * that define the conditions under which a content node must be split in two
 * adjacent nodes and the conditions under which two adjacent content nodes must
 * be merged into a single node.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The DHT stores key-value pairs in nodes implemented as distributed
 * components. As the number of pairs grows, existing nodes will become more and
 * more loaded, hence a split of the pairs in overloaded nodes into two adjacent
 * nodes will be required to balance the load. When the number of pairs in nodes
 * decreases, the inverse operation, a merge, must be performed to keep the
 * number of nodes as small as possible for a given global DHT load.
 * </p>
 * <p>
 * The two methods defined in this interface are meant to hold the load
 * management policy. {@code shouldSplitInTwoAdjacentNodes} indicates whether
 * the current number of pairs in a node requires a split.
 * {@code shouldMergeWithNextNode} indicates whether the combined current
 * numbers of pairs in two adjacent nodes require a merge of these two adjacent
 * nodes.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-06-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		LoadPolicyI
extends		Serializable
{
	/**
	 * return true if the current node should split in two adjacent nodes.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code currentSize >= 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param currentSize	current number of entries held in this node.
	 * @return				true if the current node should split in two adjacent nodes.
	 */
	public boolean		shouldSplitInTwoAdjacentNodes(int currentSize);

	/**
	 * return true if this node and the next one should be merged into a single
	 * node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code thisNodeCurrentSize >= 0}
	 * pre	{@code nextNodeCurrentSize >= 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param thisNodeCurrentSize	current number of entries held in this node.
	 * @param nextNodeCurrentSize	current number of entries held in the next node.
	 * @return						true if this node and the next one should be merged into a single node.
	 */
	public boolean		shouldMergeWithNextNode(
		int thisNodeCurrentSize,
		int nextNodeCurrentSize
		);
}

package fr.sorbonne_u.cps.dht_mapreduce.interfaces.management;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement
// a simulation of a map-reduce kind of system in BCM4Java.
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

import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints.ContentNodeCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.mapreduce.utils.SerializablePair;
import java.io.Serializable;

/**
 * The interface <code>DHTManagementI</code> defines the methods used to manage
 * the form of the DHT, adding and removing nodes, updating the internal node
 * information, etc.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-06-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		DHTManagementI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The interface <code>NodeStateI</code> is meant to mark objects than
	 * represent the state of a node <i>i.e.</i>, information required to
	 * decide whether the corresponding node may be split or merged.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * When trying to split or merge two nodes during a pass over the ring,
	 * the algorithm has to get the state of the next node to decide whether a
	 * split or merge is required. As this information is node and algorithm
	 * dependent, the user must define what is actually needed and then create
	 * a class which instances will store the state to be transmitted. These
	 * classes must implement the interface {@code NodeStateI}.
	 * </p>
	 * 
	 * <p><strong>Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2025-02-06</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public interface	NodeStateI
	extends		Serializable
	{
		
	}

	/**
	 * The interface <code>NodeContentI</code> is meant to mark objects than
	 * represent the content of a node to be saved and stored in the remaining
	 * nodes after a merge.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * When trying to split or merge two nodes during a pass over the ring,
	 * the algorithm has to get the content of the suppressed node to put it in
	 * the remaining nodes after the merge. As this information is node and
	 * algorithm dependent, the user must define what is actually needed and
	 * then create a class which instances will store this content to be
	 * transmitted. These content store classes must implement the interface
	 * {@code NodeContentI}.
	 * </p>
	 * 
	 * <p><strong>Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2025-02-06</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public interface	NodeContentI
	extends		Serializable
	{
		
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * forward a new node some local content.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code content != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param content		some content to be stored by the node.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			initialiseContent(NodeContentI content) throws Exception;

	/**
	 * return the current state in this node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return					the current state of this node.
	 * @throws Exception		<i>to do</i>.
	 */
	public NodeStateI	getCurrentState() throws Exception;

	/**
	 * get the full content of the node and then remove it from the DHT.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the full content of the suppressed node as needed to suppress it.
	 * @throws Exception	<i>to do</i>.
	 */
	public NodeContentI	suppressNode() throws Exception;

	/**
	 * make a pass over the DHT, splitting nodes in two adjacent ones when the
	 * former has a number of stored entries over a threshold, and then sending
	 * a signal to the designated caller through the given end point when the
	 * pass has terminated.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code computationURI != null && !computationURI.isEmpty()}
	 * pre	{@code loadPolicy != null}
	 * pre	{@code caller != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param <CI>					component interface offered by the initial caller to receive a termination signal.
	 * @param computationURI		URI of the computation.
	 * @param loadPolicy			policy to decide when to split nodes or merge adjacent ones.
	 * @param caller				end point to which the result must be forwarded.
	 * @throws Exception			<i>to do</i>.
	 */
	public <CI extends ResultReceptionCI> void	split(
		String computationURI,
		LoadPolicyI loadPolicy,
		EndPointI<CI> caller
		) throws Exception;

	/**
	 * make a pass over the DHT, merging adjacent nodes that total a number of
	 * stored entries under a threshold, and then sending a signal to the
	 * designated caller through the given end point when the pass has
	 * terminated.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code computationURI != null && !computationURI.isEmpty()}
	 * pre	{@code loadPolicy != null}
	 * pre	{@code caller != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param <CI>				component interface offered by the initial caller to receive a termination signal.
	 * @param computationURI	URI of the computation.
	 * @param loadPolicy		policy to decide when to split nodes or merge adjacent ones.
	 * @param caller			end point to which the result must be forwarded.
	 * @throws Exception		<i>to do</i>.
	 */
	public <CI extends ResultReceptionCI> void	merge(
		String computationURI,
		LoadPolicyI loadPolicy,
		EndPointI<CI> caller
		) throws Exception;

	/**
	 * compute the chords over the DHT ring.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code computationURI != null && !computationURI.isEmpty()}
	 * pre	{@code numberOfChords >= 1}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param computationURI	URI of the computation.
	 * @param numberOfChords	number of chords that must be computed for each node.
	 * @throws Exception		<i>to do</i>.
	 */
	public void			computeChords(
		String computationURI,
		int numberOfChords
		) throws Exception;

	/**
	 * called within the process of computing the chords of all nodes in the
	 * ring ({@code computeChords}),  get the reference to the node at
	 * {@code offset} from the current node in the ring; return null if no such
	 * node exist otherwise a
	 * {@code SerializablePair<ContentNodeCompositeEndPointI, Integer>}
	 * containing the end point to the node and the lower bound of the key hash
	 * for entries kept in this node and its followers in the DHT.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code offset >= 0}
	 * post	{@code return == null || return != null && return.second() != null}
	 * </pre>
	 *
	 * @param offset			offset of the sought node from the receiving node.
	 * @return					a pair containing a client side end point to the node and its lowest hash value.
	 * @throws Exception		<i>to do</i>.
	 */
	public SerializablePair<
				ContentNodeCompositeEndPointI<
					ContentAccessCI,
					ParallelMapReduceCI,
					DHTManagementCI>,
				Integer>	getChordInfo(int offset) throws Exception;
}

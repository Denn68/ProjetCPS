package fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce;

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

import java.io.Serializable;

import fr.sorbonne_u.components.endpoints.EndPointI;

/**
 * The interface <code>MapReduceI</code> defines the methods implementing the map
 * and the reduce computations over the DHT.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface is used as an implementation interface for components
 * representing the nodes of the DHT and that provides a map/reduce facility
 * over the entries of the DHT. The computation is done in two phases: first
 * the map traverses the DHT nodes and then the reduce. To enable a parallelism
 * between the map/reduce computations, an URI is attributed to each of them
 * so that intermediate results can be stored, retrieved and cleared on nodes
 * as the computations progress.
 * </p>
 * <p>
 * This map/reduce facility assumes that the computations for the map and the
 * reduce is fully commutative and associative. This will allow to explore more
 * forms of parallelism in the implementation of the DHT. Also, the map, as
 * usual, can be executed completely asynchronously <i>i.e.</i>, when a node
 * execute a call to map, it can forward the call to the next node and need not
 * wait for a result from this next node. The reduce phase is not as simple.
 * Therefore, this interface defines two distinct reduce methods, one that is
 * called synchronously, returning its result to the caller with a usual
 * {@code return} statement, and one that is called asynchronously but providing
 * a last parameter giving the information to send the result back to the caller
 * using an asynchronous computation pattern.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-06-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		MapReduceI
extends		MapReduceSyncI
{
	/**
	 * map the {@code processor} computation over the entries in the DHT that
	 * passes the {@code selector} test.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code computationURI != null && !computationURI.isEmpty()}
	 * pre	{@code selector != null}
	 * pre	{@code processor != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param <R>				type of the results of the map computation.
	 * @param <I>				type of the result reception component interface.
	 * @param computationURI	URI of the computation, used to distinguish parallel maps over the same DHT.
	 * @param selector			a boolean function that selects the entries in the DHT that will be processed by the map.
	 * @param processor			a function implementing the processing to be mapped itself.
	 * @throws Exception		<i>to do</i>.
	 */
	public <R extends Serializable,I extends MapReduceResultReceptionCI>
									void	map(
		String computationURI,
		SelectorI selector,
		ProcessorI<R> processor
		) throws Exception;

	/**
	 * asynchronously reduce the results of type {@code R} from a previously
	 * executed map with URI {@code computationURI} using an accumulator of type
	 * {@code A}; {@code reductor} is the function that computes a new
	 * accumulator value from the previous one and a map result while
	 * {@code combinator} is the function that computes a new accumulator value
	 * from two previous accumulator values.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code computationURI != null && !computationURI.isEmpty()}
	 * pre	{@code reductor != null}
	 * pre	{@code combinator != null}
	 * pre	{@code callerNode != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param <A>				type of the accumulator.
	 * @param <R>				type of the map results.
	 * @param <I>				type of the result reception component interface.
	 * @param computationURI	URI of the computation, used to distinguish parallel maps over the same DHT.
	 * @param reductor			function {@code A} x {@code R -> A} accumulating one map result.
	 * @param combinator		function {@code A} x {@code A -> A} combining two accumulators.
	 * @param identityAcc		the identity accumulator value <i>i.e.,</i> reducing or combining any value v with this value returns v..
	 * @param currentAcc		the previously accumulated value.
	 * @param callerNode		a descriptor of the end point where to forward the final accumulator after all reductions.
	 * @throws Exception		<i>to do</i>.
	 */
	public <A extends Serializable,R,I extends MapReduceResultReceptionCI>
									void	reduce(
		String computationURI,
		ReductorI<A,R> reductor,
		CombinatorI<A> combinator,
		A identityAcc,
		A currentAcc,
		EndPointI<I> callerNode
		) throws Exception;
}

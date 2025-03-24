package fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
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

// -----------------------------------------------------------------------------
/**
 * The interface <code>ParallelMapReduceI</code> defines the methods
 * implementing the map and the reduce computations in parallel over the DHT
 * leveraging the presence of cords in the nodes.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-03-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ParallelMapReduceI
extends		MapReduceI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The interface <code>ParallelismPolicyI</code> is meant to mark a class
	 * as representing the parallelism policy to be applied to a computation
	 * over the DHT leveraging the presence of cords in the nodes.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * This interface is just a marker interface allowing to define any kind of
	 * parallelism policy by simply defining a class implementing this interface.
	 * A parallelism policy defines how a call on a node will span into several
	 * parallel calls on more than one following nodes in the DHT ring.
	 * </p>
	 * 
	 * <p><strong>Implementation Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p><strong>Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2025-03-19</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public interface	ParallelismPolicyI
	extends		Serializable
	{
		
	}

	// -------------------------------------------------------------------------
	// Signature and default methods
	// -------------------------------------------------------------------------

	/**
	 * map the {@code processor} computation over the entries in the DHT that
	 * passes the {@code selector} test, doing this in parallel among the nodes
	 * under the control of the {@code parallelPolicy}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code computationURI != null && !computationURI.isEmpty()}
	 * pre	{@code selector != null}
	 * pre	{@code processor != null}
	 * pre	{@code parallelPolicy != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param <R>				type of the results of the map computation.
	 * @param computationURI	URI of the computation, used to distinguish parallel maps over the same DHT.
	 * @param selector			a boolean function that selects the entries in the DHT that will be processed by the map.
	 * @param processor			a function implementing the processing to be mapped itself.
	 * @param parallelismPolicy	parallelism policy controlling the way a call to a node spans into several parallel calls on more than one following nodes in the DHT ring.
	 * @throws Exception		<i>to do</i>.
	 */
	public <R extends Serializable> void	parallelMap(
		String computationURI,
		SelectorI selector,
		ProcessorI<R> processor,
		ParallelismPolicyI parallelismPolicy
		) throws Exception;

	/**
	 * asynchronously reduce the results of type {@code R} from a previously
	 * executed map with URI {@code computationURI} using an accumulator of type
	 * {@code A}; {@code reductor} is the function that computes a new
	 * accumulator value from the previous one and a map result while
	 * {@code combinator} is the function that computes a new accumulator value
	 * from two previous accumulator values; the reduction is performed in
	 * parallel among the nodes under the control of the {@code parallelPolicy}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code computationURI != null && !computationURI.isEmpty()}
	 * pre	{@code reductor != null}
	 * pre	{@code combinator != null}
	 * pre	{@code parallelPolicy != null}
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
	 * @param identityAcc		the identity accumulator value <i>i.e.,</i> reducing or combining any value v with this value returns v.
	 * @param currentAcc		the previously accumulated value.
	 * @param parallelismPolicy	parallelism policy controlling the way a call to a node spans into several parallel calls on more than one following nodes in the DHT ring.
	 * @param caller			an end point where to forward the final accumulator after all reductions.
	 * @throws Exception		<i>to do</i>.
	 */
	public <A extends Serializable,R, I extends MapReduceResultReceptionCI>
									void	parallelReduce(
		String computationURI,
		ReductorI<A,R> reductor,
		CombinatorI<A> combinator,
		A identityAcc,
		A currentAcc,
		ParallelismPolicyI parallelismPolicy,
		EndPointI<I> caller
		) throws Exception;
}
// -----------------------------------------------------------------------------

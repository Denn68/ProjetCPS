package fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce;

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
import fr.sorbonne_u.components.endpoints.EndPointI;

/**
 * The component  interface <code>MapReduceCI</code> defines the map and the
 * reduce services over the DHT.
 *
 * <p><strong>Description</strong></p>
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
public interface		MapReduceCI
extends		MapReduceSyncCI,
			MapReduceI
{
	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceI#map(java.lang.String, fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI, fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI)
	 */
	@Override
	public <R extends Serializable, I extends MapReduceResultReceptionCI>
									void	map(
		String computationURI,
		SelectorI selector,
		ProcessorI<R> processor
		) throws Exception;

	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceI#reduce(java.lang.String, fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI, fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI, java.io.Serializable, java.io.Serializable, fr.sorbonne_u.components.endpoints.EndPointI)
	 */
	@Override
	public <A extends Serializable, R, I extends MapReduceResultReceptionCI>
									void	reduce(
		String computationURI,
		ReductorI<A, R> reductor,
		CombinatorI<A> combinator,
		A identityAcc,
		A currentAcc,
		EndPointI<I> callerNode
		) throws Exception ;
}

package fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints;

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

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;

/**
 * The interface <code>ContentNodeMultiEndPoints_E2_I</code> declares the
 * signatures of the methods used in the definition of a software entity
 * (object, component, SOA service, etc.) representing a content node in
 * the DHT in an implementation-independent manner.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * A content node has three connection points, or end points: content access
 * (adding, getting and removing data), DHT management (load management, etc.)
 * and map/reduce (perform data-wise computations). The descriptor, used
 * to facilitate the connections among clients of these end points and nodes
 * providing them in the DHT, allows to retrieve descriptors (of type
 * {@code EndPointI}) for each of them using the methods declared in this
 * interface.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-06-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ContentNodeAsyncCompositeEndPointI<
									CAI extends ContentAccessCI,
									MRI extends MapReduceCI>
extends		ContentNodeBaseCompositeEndPointI<CAI,MRI>,
			BCMCompositeEndPointI
			
{
}

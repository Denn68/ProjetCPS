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
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints.ContentNodeCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.mapreduce.utils.SerializablePair;

/**
 * The component interface <code>DHTManagementCI</code> defines the services
 * used to manage the form of the DHT, adding and removing nodes, updating the
 * internal node information, etc.
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
public interface		DHTManagementCI
extends 	OfferedCI,
			RequiredCI,
			DHTManagementI
{
	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI#initialiseContent(fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI.NodeContentI)
	 */
	@Override
	public void			initialiseContent(NodeContentI content) throws Exception;

	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI#getCurrentState()
	 */
	@Override
	public NodeStateI	getCurrentState() throws Exception;

	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI#suppressNode()
	 */
	@Override
	public NodeContentI suppressNode() throws Exception;

	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI#split(java.lang.String, fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.LoadPolicyI, fr.sorbonne_u.components.endpoints.EndPointI)
	 */
	@Override
	public <CI extends ResultReceptionCI> void	split(
		String computationURI,
		LoadPolicyI loadPolicy,
		EndPointI<CI> caller
		) throws Exception;

	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI#merge(java.lang.String, fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.LoadPolicyI, fr.sorbonne_u.components.endpoints.EndPointI)
	 */
	@Override
	public <CI extends ResultReceptionCI> void	merge(
		String computationURI,
		LoadPolicyI loadPolicy,
		EndPointI<CI> caller
		) throws Exception;

	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI#computeChords(java.lang.String, int)
	 */
	@Override
	public void computeChords(String computationURI, int numberOfChords)
	throws Exception;

	/**
	 * @see fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementI#getChordInfo(int)
	 */
	@Override
	public SerializablePair<
				ContentNodeCompositeEndPointI<
					ContentAccessCI,
					ParallelMapReduceCI,
					DHTManagementCI>,
				Integer>		getChordInfo(int offset) throws Exception;
}

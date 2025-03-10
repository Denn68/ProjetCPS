package fr.sorbonne_u.components.examples.chm.interfaces;

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

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>MapReadingCI</code> defines services that access the
 * state of the map without changing it.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2019-01-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		MapReadingCI<K,V>
extends		RequiredCI,
			OfferedCI
{
	/**
	 * get the value associated with the given key and return it or null if
	 * none.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code key != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param key			the key which value must be returned.
	 * @return				the value associated with the given key or null if none.
	 * @throws Exception	<i>to do</i>.
	 */
	public V			get(K key) throws Exception ;

	/**
	 * return the number of key/value pairs kept in the map.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the number of key/value pairs kept in the map.
	 * @throws Exception	<i>to do</i>.
	 */
	public int			size() throws Exception ;

	/**
	 * return true if the map contains the given value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param value			value to be tested.
	 * @return				true if the map contains the given value.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		containsValue(V value) throws Exception ;

	/**
	 * return true if the map contains the given key.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code key != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param key			key to be tested.
	 * @return				true if the map contains the given key.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		containsKey(K key) throws Exception ;

	/**
	 * return true if the map is empty.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the map is empty.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		isEmpty() throws Exception ;
}
// -----------------------------------------------------------------------------

package fr.sorbonne_u.cps.map.withplugin.plugins;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an example of
// the BCM component model that aims to define a basic component model for Java.
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

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.map.components.MapImplementationI;
import fr.sorbonne_u.cps.map.interfaces.MapCI;
import fr.sorbonne_u.cps.map.withplugin.connections.MapInboundPortForPlugin;

import java.io.Serializable;
import java.util.HashMap;

// -----------------------------------------------------------------------------
/**
 * The class <code>MapPlugin</code> implements the map component side plug-in
 * for the <code>MapCI</code> component interface and associated ports and
 * connectors.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This implementation forces calls to the services to pass by the plug-in. This
 * represents the normal way to implement a plug-in. However, this map example
 * is not giving a fully representative example of a server-side plug-in as it
 * is not really useful to transform any component into a map component by
 * installing such a server-side plug-in. Indeed, one would expect that only
 * a dedicated component can be a map component. We provide this example just
 * to show how to program and use a server-side plug-in, not to provide a fully
 * justified use of server-side plug-ins.
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
 * <p>Created on : 2019-03-21</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			MapPlugin<K extends Serializable,
								  V extends Serializable>
extends 	AbstractPlugin
implements	MapImplementationI<K,V>
{
	// -------------------------------------------------------------------------
	// Plug-in variables and constants
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** the inbound port which calls will be on this plug-in and that will
	 * be created as a port of the owner component.							*/
	protected MapInboundPortForPlugin<K,V>	mip;
	/** the Java hash map that will contain the entries.					*/
	protected HashMap<K,V>			content;

	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------

	/**
	 * create a map plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code executorServiceURI != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				MapPlugin()
	{
		super();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		super.installOn(owner);

		// Add the interface
		this.addOfferedInterface(MapCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		super.initialise();

		this.content = new HashMap<>();

		// Create the inbound port
		this.mip = new MapInboundPortForPlugin<K,V>(this.getOwner(),
													this.getPluginURI());
		this.mip.publishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.content.clear();
		this.content = null;
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		this.mip.unpublishPort();
		this.mip.destroyPort();					// optional
		this.removeOfferedInterface(MapCI.class);
	}

	// -------------------------------------------------------------------------
	// Plug-in services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.map.components.MapImplementationI#put(java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public void			put(K key, V value) throws Exception
	{
		this.content.put(key, value);
	}

	/**
	 * @see fr.sorbonne_u.cps.map.components.MapImplementationI#get(java.io.Serializable)
	 */
	@Override
	public V			get(K key) throws Exception
	{
		return this.content.get(key);
	}

	/**
	 * @see fr.sorbonne_u.cps.map.components.MapImplementationI#containsKey(java.io.Serializable)
	 */
	@Override
	public boolean		containsKey(K key) throws Exception
	{
		return this.content.containsKey(key);
	}

	/**
	 * @see fr.sorbonne_u.cps.map.components.MapImplementationI#remove(java.io.Serializable)
	 */
	@Override
	public void			remove(K key) throws Exception
	{
		this.content.remove(key);
	}
}
// -----------------------------------------------------------------------------

package fr.sorbonne_u.cps.map.withplugin.connections;

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

import java.io.Serializable;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.map.interfaces.MapCI;
import fr.sorbonne_u.cps.map.withplugin.plugins.MapPlugin;

// -----------------------------------------------------------------------------
/**
 * The class <code>MapInboundPortForPlugin</code> implements an inbound port
 * for the <code>MapCI</code> component interface that directs its calls to
 * the plug-in rather than directly to the component implementation.
 *
 * <p><strong>Description</strong></p>
 * 
 * This port is meant to use the {@code MapPlugin} installed on a
 * {@code MapComponent}. To direct calls to the plug-in, it uses the plug-in URI
 * provided at creation time through the constructors. The methods create
 * {@code AbstractService} instances passing them the URI provided to the port
 * by calling {@code this.getPluginURI()}. Having initialised the plug-in URI in
 * instances of {@code AbstractService}, the method
 * {@code getServiceProviderReference()} return the reference to the plug-in
 * implementation object, hence calling the methods defined in the plug-in
 * rather than methods defined in the component class.
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
public class			MapInboundPortForPlugin<K extends Serializable,
											    V extends Serializable>
extends		AbstractInboundPort
implements	MapCI<K, V>
{
	private static final long serialVersionUID = 1L;

	public				MapInboundPortForPlugin(
		ComponentI owner,
		String pluginURI
		) throws Exception
	{
		super(MapCI.class, owner, pluginURI, null);

		assert	owner.isInstalled(pluginURI);
	}

	public				MapInboundPortForPlugin(
		String uri,
		ComponentI owner,
		String pluginURI
		) throws Exception
	{
		super(uri, MapCI.class, owner, pluginURI, null);

		assert	owner.isInstalled(pluginURI);
	}


	/**
	 * @see fr.sorbonne_u.cps.map.components.MapImplementationI#put(java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public void			put(K key, V value) throws Exception
	{
		this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {
				@SuppressWarnings("unchecked")
				@Override
				public Void call() throws Exception {
					((MapPlugin<K,V>)
							this.getServiceProviderReference()).put(key, value);
					return null;
				}
			});
	}

	/**
	 * @see fr.sorbonne_u.cps.map.components.MapImplementationI#get(java.io.Serializable)
	 */
	@Override
	public V				get(K key) throws Exception
	{
		return this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<V>(this.getPluginURI()) {
				@SuppressWarnings("unchecked")
				@Override
				public V call() throws Exception {
					return ((MapPlugin<K,V>)
								this.getServiceProviderReference()).get(key);
				}
			});
	}

	/**
	 * @see fr.sorbonne_u.cps.map.components.MapImplementationI#containsKey(java.io.Serializable)
	 */
	@Override
	public boolean		containsKey(K key) throws Exception
	{
		return this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<Boolean>(
													this.getPluginURI()) {
			 	@SuppressWarnings("unchecked")
				@Override
				public Boolean call() throws Exception {
					return ((MapPlugin<K,V>)
								this.getServiceProviderReference()).
															containsKey(key);
				}
			});
	}

	/**
	 * @see fr.sorbonne_u.cps.map.components.MapImplementationI#remove(java.io.Serializable)
	 */
	@Override
	public void			remove(K key) throws Exception
	{
		this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {
				@SuppressWarnings("unchecked")
				@Override
				public Void call() throws Exception {
					((MapPlugin<K,V>)
							this.getServiceProviderReference()).remove(key);
					return null;
				}
			});
	}
}
// -----------------------------------------------------------------------------

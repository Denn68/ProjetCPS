package fr.sorbonne_u.components.plugins.asynccall.connections;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an
// implementation of the BCM component model.
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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.plugins.asynccall.AbstractAsyncCall;
import fr.sorbonne_u.components.plugins.asynccall.AsyncCallCI;
import fr.sorbonne_u.components.plugins.asynccall.AsyncCallServerPlugin;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>AsyncCallInboundPort</code> implements an inbound port for
 * the <code>AsyncCallCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-04-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AsyncCallInboundPort
extends		AbstractInboundPort
implements	AsyncCallCI
{
	private static final long serialVersionUID = 1L;

	public				AsyncCallInboundPort(
		ComponentI owner,
		String pluginURI
		) throws Exception
	{
		super(AsyncCallCI.class, owner, pluginURI, null);
	}

	public				AsyncCallInboundPort(
		String uri,
		ComponentI owner,
		String pluginURI
		) throws Exception
	{
		super(uri, AsyncCallCI.class, owner, pluginURI, null);
	}

	/**
	 * @see fr.sorbonne_u.components.plugins.asynccall.AsyncCallCI#disconnectClient(java.lang.String)
	 */
	@Override
	public void			disconnectClient(String receptionPortURI)
	throws Exception
	{
		((AsyncCallServerPlugin)this.getOwnerPlugin(this.getPluginURI())).
											disconnectClient(receptionPortURI);
	}

	/**
	 * @see fr.sorbonne_u.components.plugins.asynccall.AsyncCallCI#asyncCall(fr.sorbonne_u.components.plugins.asynccall.AbstractAsyncCall)
	 */
	@Override
	public void			asyncCall(AbstractAsyncCall c) throws Exception
	{
		((AsyncCallServerPlugin)this.getOwnerPlugin(this.getPluginURI())).
																asyncCall(c);
	}
}
// -----------------------------------------------------------------------------

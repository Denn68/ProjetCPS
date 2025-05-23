package fr.sorbonne_u.cps.sron.connection;

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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sron.interfaces.ManagementI;
import fr.sorbonne_u.cps.sron.interfaces.NodeServicesCI;
import fr.sorbonne_u.cps.sron.plugins.StandardMessage;

// -----------------------------------------------------------------------------
/**
 * The class <code>NodeServicesOutboundPort</code> implements the outbound
 * port for the required interface <code>NodeServicesCI</code>.
 *
 * <p><strong>Description</strong></p>
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
 * <p>Created on : 2019-04-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			NodeServicesOutboundPort
extends		AbstractOutboundPort
implements	NodeServicesCI
{
	private static final long serialVersionUID = 1L;

	public				NodeServicesOutboundPort(ComponentI owner)
	throws Exception
	{
		super(NodeServicesCI.class, owner);
	}

	public				NodeServicesOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, NodeServicesCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessagingI#broadcast(fr.sorbonne_u.cps.sron.plugins.StandardMessage)
	 */
	@Override
	public void			broadcast(StandardMessage m) throws Exception
	{
		((NodeServicesCI)this.getConnector()).broadcast(m);
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessagingI#acceptOrPass(fr.sorbonne_u.cps.sron.plugins.StandardMessage)
	 */
	@Override
	public void			acceptOrPass(StandardMessage m) throws Exception
	{
		((NodeServicesCI)this.getConnector()).acceptOrPass(m);
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.ManagementI#connectAsFirstNode()
	 */
	@Override
	public void			connectAsFirstNode() throws Exception
	{
		((ManagementI)this.getConnector()).connectAsFirstNode();
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.ManagementI#connectNextNode(java.lang.String)
	 */
	@Override
	public void			connectNextNode(String nextRIPURI) throws Exception
	{
		((ManagementI)this.getConnector()).connectNextNode(nextRIPURI);
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.ManagementI#connectToNewNext(java.lang.String)
	 */
	@Override
	public void			connectToNewNext(String newNextIBPURI)
	throws Exception
	{
		((ManagementI)this.getConnector()).connectToNewNext(newNextIBPURI);
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.ManagementI#disconnectNode()
	 */
	@Override
	public void			disconnectNode() throws Exception
	{
		((ManagementI)this.getConnector()).disconnectNode();
	}
}
// -----------------------------------------------------------------------------

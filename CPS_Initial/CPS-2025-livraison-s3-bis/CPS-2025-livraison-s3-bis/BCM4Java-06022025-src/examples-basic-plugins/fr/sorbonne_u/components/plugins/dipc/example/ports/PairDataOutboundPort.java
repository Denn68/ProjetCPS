package fr.sorbonne_u.components.plugins.dipc.example.ports;

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
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI;
import fr.sorbonne_u.components.plugins.dipc.example.components.ClientComponent;
import fr.sorbonne_u.components.plugins.dipc.example.interfaces.PairDataCI.PairI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>PairDataOutboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-03-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			PairDataOutboundPort
extends		AbstractDataOutboundPort
{
	private static final long serialVersionUID = 1L;

	public				PairDataOutboundPort(
		String uri, 
		ComponentI owner
		) throws Exception
	{
		super(uri,
			  DataRequiredCI.PullCI.class,
			  DataRequiredCI.PushCI.class,
			  owner) ;
		assert	owner instanceof ClientComponent ;
	}

	public				PairDataOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(DataRequiredCI.PullCI.class,
			  DataRequiredCI.PushCI.class,
			  owner) ;
		assert	owner instanceof ClientComponent ;
	}

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataRequiredCI.PushCI#receive(fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI)
	 */
	@Override
	public void			receive(DataI d) throws Exception
	{
		((ClientComponent)this.getOwner()).consumeData((PairI) d) ;
	}
}
//------------------------------------------------------------------------------

package fr.sorbonne_u.cps.sron.election.components;

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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationCI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.cps.sron.connection.NodeServicesConnector;
import fr.sorbonne_u.cps.sron.connection.NodeServicesOutboundPort;
import fr.sorbonne_u.cps.sron.election.connection.ElectionManagementConnector;
import fr.sorbonne_u.cps.sron.election.connection.ElectionManagementOutboundPort;
import fr.sorbonne_u.cps.sron.election.interfaces.ElectionManagementCI;
import fr.sorbonne_u.cps.sron.interfaces.NodeServicesCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>DynamicAssembler</code> implements the component which
 * dynamically assembles the components for a run of the election algorithm.
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
public class			DynamicAssembler
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Variables and constants
	// -------------------------------------------------------------------------

	/** outbound port connected to the dynamic component creator component.	*/
	protected DynamicComponentCreationOutboundPort	dccop;
	/** URI of the JVM where to create the components.						*/
	protected final String							jvmURI;
	/** URI of first component created in the ring overlay network.  		*/
	protected String								node1IBPURI;
	/** URI of second component created in the ring overlay network.  		*/
	protected String								node2IBPURI;
	/** URI of third component created in the ring overlay network.  		*/
	protected String								node3IBPURI;
	/** URI of fourth component created in the ring overlay network.  		*/
	protected String								node4IBPURI;
	/** URI of fifth component created in the ring overlay network.  		*/
	protected String								node5IBPURI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			DynamicAssembler(
		String jvmURI
		)
	{
		super(1, 0);
		this.jvmURI = jvmURI;
		this.getTracer().setTitle("DynamicAssembler");
		this.getTracer().setRelativePosition(0, 0);
		this.toggleTracing();
	}

	protected			DynamicAssembler(
		String reflectionInboundPortURI,
		String jvmURI
		)
	{
		super(reflectionInboundPortURI, 1, 0);
		this.jvmURI = jvmURI;
		this.getTracer().setTitle("DynamicAssembler");
		this.getTracer().setRelativePosition(0, 0);
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Life-cycle methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		this.addRequiredInterface(DynamicComponentCreationCI.class);
		this.dccop = new DynamicComponentCreationOutboundPort(this);
		this.dccop.publishPort();

		this.doPortConnection(
				this.dccop.getPortURI(),
				this.jvmURI,
				DynamicComponentCreationConnector.class.getCanonicalName());

		this.node1IBPURI =
				this.dccop.createComponent(
						ElectionNode.class.getCanonicalName(),
						new Object[]{});
		this.dccop.startComponent(this.node1IBPURI);

		this.addRequiredInterface(ReflectionCI.class);
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		rop.publishPort();
		this.doPortConnection(
				rop.getPortURI(),
				this.node1IBPURI,
				ReflectionConnector.class.getCanonicalName());

		String[] uris =
			rop.findInboundPortURIsFromInterface(NodeServicesCI.class);
		assert	uris != null && uris.length == 1;

		this.addRequiredInterface(NodeServicesCI.class);
		NodeServicesOutboundPort nsop1 = new NodeServicesOutboundPort(this);
		nsop1.publishPort();

		this.doPortConnection(
				nsop1.getPortURI(),
				uris[0],
				NodeServicesConnector.class.getCanonicalName());

		nsop1.connectAsFirstNode();

		this.node2IBPURI =
				this.dccop.createComponent(
						ElectionNode.class.getCanonicalName(),
						new Object[]{});
		this.dccop.startComponent(node2IBPURI);
		nsop1.connectNextNode(this.node2IBPURI);

		this.node3IBPURI =
				this.dccop.createComponent(
						ElectionNode.class.getCanonicalName(),
						new Object[]{});
		this.dccop.startComponent(this.node3IBPURI);
		nsop1.connectNextNode(this.node3IBPURI);

		this.node4IBPURI =
				this.dccop.createComponent(
						ElectionNode.class.getCanonicalName(),
						new Object[]{});
		this.dccop.startComponent(this.node4IBPURI);
		nsop1.connectNextNode(this.node4IBPURI);

		this.node5IBPURI =
				this.dccop.createComponent(
						ElectionNode.class.getCanonicalName(),
						new Object[]{});
		this.dccop.startComponent(this.node5IBPURI);
		nsop1.connectNextNode(this.node5IBPURI);

		uris = rop.findInboundPortURIsFromInterface(ElectionManagementCI.class);
		assert	uris != null && uris.length == 1;
		this.addRequiredInterface(ElectionManagementCI.class);
		ElectionManagementOutboundPort mop =
								new ElectionManagementOutboundPort(this);
		mop.publishPort();
		this.doPortConnection(
				mop.getPortURI(),
				uris[0],
				ElectionManagementConnector.class.getCanonicalName());

		Thread.sleep(1000L);

		this.traceMessage("Ring overlay network election test.\n");
		this.traceMessage("   With the numbering of nodes, their order in " +
						     "the ring is 0-4-3-2-1.\n\n");
		this.traceMessage("Start the election.\n");
		mop.startElection();

		Thread.sleep(1000L);
		this.traceMessage("Election ended.\n");

		this.doPortDisconnection(nsop1.getPortURI());
		nsop1.unpublishPort();
		this.doPortDisconnection(mop.getPortURI());
		mop.unpublishPort();

		this.doPortDisconnection(rop.getPortURI());
		rop.unpublishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(this.dccop.getPortURI());

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.dccop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
//------------------------------------------------------------------------------

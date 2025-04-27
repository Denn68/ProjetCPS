package frontend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class DHTManagementEndpoint 
extends BCMEndPoint<DHTManagementCI>{

	private int executorIndex;

	public DHTManagementEndpoint() {
		super(DHTManagementCI.class, DHTManagementCI.class);
	}
	
	public DHTManagementEndpoint(String inboundPortURI, int executorServiceIndex) {
		super(DHTManagementCI.class, DHTManagementCI.class, inboundPortURI);
		this.executorIndex = executorServiceIndex;
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected AbstractInboundPort makeInboundPort(AbstractComponent c, String inboundPortURI) throws Exception {
		// Preconditions checking
		assert	c != null : new PreconditionException("c != null");
		assert	inboundPortURI != null && !inboundPortURI.isEmpty() :
				new PreconditionException(
						"inboundPortURI != null && !inboundPortURI.isEmpty()");
		

		assert this.inboundPortURI.equals(inboundPortURI) : new PreconditionException("Different InboundPortURI");

		DHTManagementInboundPort p =
				new DHTManagementInboundPort(inboundPortURI, this.executorIndex, c);
		p.publishPort();

		// Postconditions checking
		assert	p != null && p.isPublished() :
				new PostconditionException(
						"return != null && return.isPublished()");
		assert	((AbstractPort)p).getPortURI().equals(inboundPortURI) :
				new PostconditionException(
						"((AbstractPort)return).getPortURI().equals(inboundPortURI)");
		assert	getServerSideInterface().isAssignableFrom(p.getClass()) :
				new PostconditionException(
						"getOfferedComponentInterface()."
						+ "isAssignableFrom(return.getClass())");
		// Invariant checking
		assert	DHTManagementEndpoint.implementationInvariants(this) :
				new ImplementationInvariantException(
						"DHTManagementEndpoint.implementationInvariants(this)");
		assert	DHTManagementEndpoint.invariants(this) :
				new InvariantException("DHTManagementEndpoint.invariants(this)");
		
		return p;
	}

	@Override
	protected DHTManagementCI makeOutboundPort(AbstractComponent c, String inboundPortURI)
			throws Exception {
		// Preconditions checking
				assert	c != null : new PreconditionException("c != null");
				assert this.inboundPortURI.equals(inboundPortURI) : new PreconditionException("Different InboundPortURI");

				DHTManagementOutboundPort p =
						new DHTManagementOutboundPort(c);
				p.publishPort();
				c.doPortConnection(
						p.getPortURI(),
						inboundPortURI,
						DHTManagementConnector.class.getCanonicalName());

				// Postconditions checking
				assert	p != null && p.isPublished() && p.connected() :
						new PostconditionException(
								"return != null && return.isPublished() && "
								+ "return.connected()");
				assert	((AbstractPort)p).getServerPortURI().equals(getInboundPortURI()) :
						new PostconditionException(
								"((AbstractPort)return).getServerPortURI()."
								+ "equals(getInboundPortURI())");
				assert	getClientSideInterface().isAssignableFrom(p.getClass()) :
						new PostconditionException(
								"getImplementedInterface().isAssignableFrom("
								+ "return.getClass())");
				
				// Invariant checking
				assert	implementationInvariants(this) :
						new ImplementationInvariantException(
								"implementationInvariants(this)");
				assert	invariants(this) : new InvariantException("invariants(this)");
				
				return p;
	}

	public void setExecutorServiceIndex(int dHTManagementExecutorServiceIndex) {
		this.executorIndex = dHTManagementExecutorServiceIndex;
	}

}

package backend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class ContentAccessEndpoint 
extends BCMEndPoint<ContentAccessCI>{

	public ContentAccessEndpoint() {
		super(ContentAccessCI.class, ContentAccessCI.class);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int executorServiceIndex;

	@Override
	protected AbstractInboundPort makeInboundPort(AbstractComponent c, String inboundPortURI) throws Exception {
		// Preconditions checking
		assert	c != null : new PreconditionException("c != null");
		assert	inboundPortURI != null && !inboundPortURI.isEmpty() :
				new PreconditionException(
						"inboundPortURI != null && !inboundPortURI.isEmpty()");

		ContentAccessInboundPort p =
				new ContentAccessInboundPort(this.inboundPortURI, this.executorServiceIndex, c);
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
		assert	ContentAccessEndpoint.implementationInvariants(this) :
				new ImplementationInvariantException(
						"ContentAccessEndpoint.implementationInvariants(this)");
		assert	ContentAccessEndpoint.invariants(this) :
				new InvariantException("ContentAccessEndpoint.invariants(this)");
		
		return p;
	}

	@Override
	protected ContentAccessCI makeOutboundPort(AbstractComponent c, String inboundPortURI)
			throws Exception {
		// Preconditions checking
				assert	c != null : new PreconditionException("c != null");

				ContentAccessOutboundPort p =
						new ContentAccessOutboundPort(c);
				p.publishPort();
				c.doPortConnection(
						p.getPortURI(),
						this.inboundPortURI,
						ContentAccessConnector.class.getCanonicalName());

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
	
	public void setExecutorServiceIndex(int executorServiceIndex) {
		this.executorServiceIndex = executorServiceIndex;
	}

}

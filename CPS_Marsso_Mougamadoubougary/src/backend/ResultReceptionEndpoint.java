package backend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class ResultReceptionEndpoint 
extends BCMEndPoint<ResultReceptionCI>{

	public ResultReceptionEndpoint() {
		super(ResultReceptionCI.class, ResultReceptionCI.class);
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

		ResultReceptionInboundPort p =
				new ResultReceptionInboundPort(this.inboundPortURI, this.executorServiceIndex, c);
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
		assert	ResultReceptionEndpoint.implementationInvariants(this) :
				new ImplementationInvariantException(
						"ResultReceptionCI.implementationInvariants(this)");
		assert	ResultReceptionEndpoint.invariants(this) :
				new InvariantException("ResultReceptionCI.invariants(this)");
		
		return p;
	}

	@Override
	protected ResultReceptionCI makeOutboundPort(AbstractComponent c, String inboundPortURI)
			throws Exception {
		// Preconditions checking
				assert	c != null : new PreconditionException("c != null");

				ResultReceptionOutboundPort p =
						new ResultReceptionOutboundPort(c);
				p.publishPort();
				c.doPortConnection(
						p.getPortURI(),
						this.inboundPortURI,
						ResultReceptionConnector.class.getCanonicalName());

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

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
import java.io.Serializable;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.cps.sron.election.connection.ElectionManagementInboundPort;
import fr.sorbonne_u.cps.sron.election.interfaces.ElectionManagementCI;
import fr.sorbonne_u.cps.sron.plugins.StandardMessage;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectionNode</code> represents a component in an overlay
 * network used to implement an election algorithm.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Implements the Chang and Roberts algorithm (1979, source: Wikipedia,
 * accessed on 2015/01/12).
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
 * <p>Created on : 2019-04-08</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ElectionNode
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>Candidate</code> is the payload of an election message
	 * used to represent a candidate node in the first phase of the algorithm.
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
	 * <p>Created on : 2020-04-09</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static class	Candidate
	implements Serializable
	{
		private static final long	serialVersionUID = 1L;
		/** unique identifier of the candidate node.						*/
		protected int				uid;

		public Candidate(int uid) {
			super();
			this.uid = uid;
		}
		public int					getUID() { return this.uid; }
		public String				toString()
		{
			return "Candidate[" + this.uid + "]";
		}
	}
	
	/**
	 * The class <code>Elected</code> is the payload of an elected message
	 * used to represent an elected node in the second phase of the algorithm.
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
	 * <p>Created on : 2020-04-09</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static class	Elected
	implements Serializable
	{
		private static final long	serialVersionUID = 1L;
		protected int				uid;

		public Elected(int uid) {
			super();
			this.uid = uid;
		}
		public int					getUID() { return this.uid; }
		public String				toString()
		{
			return "Elected[" + this.uid + "]";
		}
	}

	// -------------------------------------------------------------------------
	// Variables and constants
	// -------------------------------------------------------------------------

	/** number of threads used to process the messages in the overlay
	 *  network.															*/
	protected static final int				NB_THREADS = 1;
	/** election plug-in implementing the extension of the overlay network
	 *  required for the election algorithm.								*/
	protected ElectionNodePlugin			plugin;
	/** true if the node is participating in the election, false otherwise.	*/
	protected boolean						participant;
	/** uid of the elected node.											*/
	protected int							elected;
	/** port used by third party components to manage an election over the
	 *  overlay network containing this node.								*/
	protected ElectionManagementInboundPort emip;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an election node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected			ElectionNode() throws Exception
	{
		super(1, 0);
		this.init();
	}

	/**
	 * create an election node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of this component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			ElectionNode(String reflectionInboundPortURI)
	throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.init();
	}

	/**
	 * initialise the election node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		init() throws Exception
	{
		this.plugin = new ElectionNodePlugin(NB_THREADS);
		String pluginURI = "ElectionNode-" + this.plugin.getNodeNumber();
		plugin.setPluginURI(pluginURI);
		this.installPlugin(plugin);

		this.addOfferedInterface(ElectionManagementCI.class);
		this.emip = new ElectionManagementInboundPort(this);
		this.emip.publishPort();

		this.getTracer().setTitle(pluginURI);
		this.getTracer().setRelativePosition(1, this.plugin.getNodeNumber());
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Life-cycle methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.emip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Service implementations
	// -------------------------------------------------------------------------

	/**
	 * get the UID (number) attributed to this node in the election overlay
	 * network.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the uid (number) attributed to this node in the election overlay network.
	 */
	protected int		getNumber()
	{
		return this.plugin.getNodeNumber();
	}

	/**
	 * start the election by broadcasting the initialisation message to the
	 * network.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			startElection() throws Exception
	{
		this.plugin.broadcast(new StandardMessage(
									"message",
									ElectionNodePlugin.INIT_ELECTION_MESSAGE));
	}

	/**
	 * start the first phase of the election by sending the candidate message
	 * to the next node in the ring overlay network.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			startElectionFirstPhase() throws Exception
	{
		Thread.sleep(1000L);
		this.participant = true;
		this.elected = -1;
		plugin.sendElectionMessage(
				new StandardMessage("message",
									new Candidate(this.getNumber())));
	}

	/**
	 * broadcast the elected message for this node to all nodes in the
	 * network.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			broadcastElected() throws Exception
	{
		Thread.sleep(1000L);
		this.participant = false;
		this.elected = this.getNumber();
		plugin.broadcast(
				new StandardMessage("message",
									new Elected(this.getNumber())));

	}

	/**
	 * end the election (for tracing purposes).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			endElection() throws Exception
	{
		Thread.sleep(1000L);
		this.traceMessage(
			"Election has ended with elected: " + this.elected + "\n");
	}

	/**
	 * return true if this node is participating in the election.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this node is participating in the election.
	 */
	public boolean		isParticipant()
	{
		return this.participant;
	}

	/**
	 * set this node as participating or non participating in the election
	 * given the parameter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param b		true if the node is participating in the election, false otherwise.
	 */
	public void			setParticipant(boolean b)
	{
		this.participant = b;
	}

	/**
	 * set the UID of the elected node and resets the node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param n		UID of the elected node.
	 */
	public void			setElected(int n)
	{
		if (n >= 0) {
			this.traceMessage(
					"Node " + this.getNumber() + " sets " + n + " elected.\n");
		}
		this.participant = false;
		this.elected = n;
	}
}
// -----------------------------------------------------------------------------

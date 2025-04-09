package fr.sorbonne_u.cps.sron.plugins;

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

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.cps.sron.connection.NodeServicesConnector;
import fr.sorbonne_u.cps.sron.connection.NodeServicesInboundPort;
import fr.sorbonne_u.cps.sron.connection.NodeServicesOutboundPort;
import fr.sorbonne_u.cps.sron.interfaces.ManagementI;
import fr.sorbonne_u.cps.sron.interfaces.MessageI;
import fr.sorbonne_u.cps.sron.interfaces.MessagingI;
import fr.sorbonne_u.cps.sron.interfaces.NodeServicesCI;

import java.util.concurrent.locks.ReentrantReadWriteLock;

//------------------------------------------------------------------------------
/**
 * The class <code>SRONNodePlugin</code> implements a plug-in that creates
 * a ring overlay network over a set of components.
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
 * <p>Created on : 2019-04-08</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	SRONNodePlugin
extends		AbstractPlugin
implements	ManagementI,
			MessagingI
{
	// -------------------------------------------------------------------------
	// Plug-in variables and constants
	// -------------------------------------------------------------------------

	private static final long			serialVersionUID = 1L;
	/** URI of the pool of threads created on the owner component to execute
	 *  the overlay network methods as tasks.								*/
	public static final String			OVERLAY_NETWORK_POOL_URI =
													"overlay-node-pool-uri";
	/** index of the pool of threads created on the owner component to execute
	 *  the overlay network methods as tasks.								*/
	protected int						executorServiceIndex;
	/** number of threads in the overlay network pool.						*/
	protected final int					nbThreads;
	/** lock used to control the state of the overlay network.				*/
	protected ReentrantReadWriteLock	onNodeLock;
	/** a reflection outbound port used when adding a new next node
	 *  after the current one in the ring.									*/
	protected ReflectionOutboundPort	rop;
	/** network services inbound port connected to the previous node.		*/
	protected NodeServicesInboundPort	nsip;
	/** network services outbound port connected to the next node.			*/
	protected NodeServicesOutboundPort	nsop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new overlay network plug-in instance which will create and use
	 * a pool of threads on the owner component with the given number of
	 * threads.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code nbThreads > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param nbThreads		number of threads in the pool that will execute the plug-in methods.
	 */
	public				SRONNodePlugin(int nbThreads)
	{
		super();
		assert	nbThreads > 0;
		this.nbThreads = nbThreads;
	}

	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		super.installOn(owner);

		this.addOfferedInterface(NodeServicesCI.class);
		this.addRequiredInterface(NodeServicesCI.class);
		this.addRequiredInterface(ReflectionCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		this.onNodeLock = new ReentrantReadWriteLock();		

		assert	!this.getOwner().validExecutorServiceURI(
													OVERLAY_NETWORK_POOL_URI);
		this.executorServiceIndex =
				this.createNewExecutorService(OVERLAY_NETWORK_POOL_URI,
											  this.nbThreads,
											  false);
		assert	this.getOwner().validExecutorServiceURI(
												OVERLAY_NETWORK_POOL_URI);
		assert	this.getOwner().validExecutorServiceIndex(
												this.executorServiceIndex);

		this.nsip = new NodeServicesInboundPort(this.getOwner(),
												this.getPluginURI(),
												OVERLAY_NETWORK_POOL_URI);
		this.nsip.publishPort();

		this.nsop = new NodeServicesOutboundPort(this.getOwner());
		this.nsop.publishPort();

		this.rop = new ReflectionOutboundPort(this.getOwner());
		rop.publishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.nsop.connected()) {
			this.getOwner().doPortDisconnection(this.nsop.getPortURI());
		}
		if (this.rop.connected()) {
			this.getOwner().doPortDisconnection(this.rop.getPortURI());
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		this.nsip.unpublishPort();
		this.nsop.unpublishPort();
		this.rop.unpublishPort();
	}

	// -------------------------------------------------------------------------
	// Plug-in internal methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the message has been marked by this node.
	 * 
	 * <p>
	 * When a message enters the ring, the first node marks it as being
	 * the first node to see this message; this mark is used to detect
	 * when the message has circulated the whole ring.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m	message to be tested.
	 * @return	true if the message has been marked by this node.
	 */
	protected boolean	isMarked(StandardMessage m)
	{
		assert	m!= null;

		return m.getFirstReceiverInboundPortURI() != null;
	}

	/**
	 * return true if this node has marked the message m.
	 * 
	 * <p>
	 * When a message enters the ring, the first node marks it as being
	 * the first node to see this message; this mark is used to detect
	 * when the message has circulated the whole ring.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m	message to be tested.
	 * @return	true if this node has marked the message m.
	 */
	protected boolean	hasMarkedMessage(StandardMessage m)
	{
		assert	m!= null;

		try {
			return this.nsip.getPortURI().equals(
										m.getFirstReceiverInboundPortURI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * mark a message as being seen the first time in the ring by the
	 * current node.
	 * 
	 * <p>
	 * When a message enters the ring, the first node marks it as being
	 * the first node to see this message; this mark is used to detect
	 * when the message has circulated the whole ring.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null && !isMarked(m)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m	message to be marked.
	 */
	protected void		markMessage(StandardMessage m)
	{
		assert	m!= null && !this.isMarked(m);

		try {
			m.setFirstReceiverInboundPortURI(this.nsip.getPortURI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// -------------------------------------------------------------------------
	// Plug-in services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.ManagementI#connectAsFirstNode()
	 */
	@Override
	public void			connectAsFirstNode() throws Exception
	{
		this.onNodeLock.writeLock().lock();
		try {
			// connects to itself as the sole node in the ring overlay network
			this.getOwner().doPortConnection(
				this.nsop.getPortURI(),
				this.nsip.getPortURI(),
				NodeServicesConnector.class.getCanonicalName());
			assert	this.nsop.connected();
		} finally {
			this.onNodeLock.writeLock().unlock();
		}
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.ManagementI#connectNextNode(java.lang.String)
	 */
	@Override
	public void			connectNextNode(String nextRIPURI)
	throws Exception
	{
		this.onNodeLock.writeLock().lock();
		try {
			if (this.rop.connected()) {
				this.getOwner().doPortDisconnection(this.rop.getPortURI());
			}
			this.getOwner().doPortConnection(
						rop.getPortURI(),
						nextRIPURI,
						ReflectionConnector.class.getCanonicalName());
			String[] uris =
				rop.findInboundPortURIsFromInterface(NodeServicesCI.class);
			assert	uris != null && uris.length == 1;
			String currentNextIBPURI = this.nsop.getServerPortURI();
			this.getOwner().doPortDisconnection(this.nsop.getPortURI());
			this.getOwner().doPortConnection(
				this.nsop.getPortURI(),
				uris[0],
				NodeServicesConnector.class.getCanonicalName());
			this.nsop.connectToNewNext(currentNextIBPURI);
			this.getOwner().doPortDisconnection(rop.getPortURI());
		} finally {
			this.onNodeLock.writeLock().unlock();
		}
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.ManagementI#connectToNewNext(java.lang.String)
	 */
	@Override
	public void			connectToNewNext(String newNextIBPURI)
	throws Exception
	{
		assert	!this.nsop.connected();

		this.onNodeLock.writeLock().lock();
		try {
			this.getOwner().doPortConnection(
						this.nsop.getPortURI(),
						newNextIBPURI,
						NodeServicesConnector.class.getCanonicalName());
		} finally {
			this.onNodeLock.writeLock().unlock();
		}
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.ManagementI#disconnectNode()
	 */
	@Override
	public void			disconnectNode() throws Exception
	{
		this.onNodeLock.writeLock().lock();
		try {
			this.nsop.disconnectNode();
			this.getOwner().doPortDisconnection(this.nsop.getPortURI());
		} finally {
			this.onNodeLock.writeLock().unlock();
		}
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessagingI#broadcast(fr.sorbonne_u.cps.sron.plugins.StandardMessage)
	 */
	@Override
	public void			broadcast(StandardMessage m) throws Exception
	{
		if (!this.hasMarkedMessage(m)) {
			// first time the node sees m
			this.processBroadcastMessage(m);
			if (!this.isMarked(m)) {
				// first time the message enters the network
				this.markMessage(m);
			}
			this.onNodeLock.readLock().lock();
			try {
				this.nsop.broadcast(m);
			} finally {
				this.onNodeLock.readLock().unlock();
			}
		} else {
			// the node was the first to receive m, so
			// the broadcast has ended.
		}
	}

	/**
	 * process the broadcast message; must be implemented by subclasses
	 * as the processing is application dependent.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m	message to be processed.
	 */
	protected abstract void	processBroadcastMessage(MessageI m);

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessagingI#acceptOrPass(fr.sorbonne_u.cps.sron.plugins.StandardMessage)
	 */
	@Override
	public void		acceptOrPass(StandardMessage m) throws Exception
	{
		if (!this.hasMarkedMessage(m)) {
			// first time the node sees the message
			if (this.accept(m)) {
				// accept and process it, do not resend
				this.processAcceptedMessage(m);
			} else {
				// resend the messsage
				if (!this.isMarked(m)) {
					// first time the message enters the network
					this.markMessage(m);
				}
				this.onNodeLock.readLock().lock();
				try {
					this.nsop.acceptOrPass(m);
				} finally {
					this.onNodeLock.readLock().unlock();
				}
			}
		} else {
			// the node was the first to receive m, so
			// the message did the whole network without
			// being accepted.
		}
	}

	/**
	 * process an accepted message; must be implemented by subclasses
	 * as the processing is application dependent.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m	message to be processed.
	 */
	protected abstract void		processAcceptedMessage(MessageI m);

	/**
	 * return true if this node accepts the message (and is ready to
	 * process it).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m	message to be tested.
	 * @return	true if this node accepts the message (and is ready to process it).
	 */
	protected abstract boolean	accept(MessageI m);
}
//------------------------------------------------------------------------------

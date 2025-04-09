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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.cps.sron.election.components.ElectionNode.Candidate;
import fr.sorbonne_u.cps.sron.election.components.ElectionNode.Elected;
import fr.sorbonne_u.cps.sron.interfaces.MessageI;
import fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin;
import fr.sorbonne_u.cps.sron.plugins.StandardMessage;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectionNodePlugin</code> implements a plug-in for nodes
 * in a ring overlay network where elections can take place.
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
public class			ElectionNodePlugin
extends		SRONNodePlugin
{
	// -------------------------------------------------------------------------
	// Plug-in variables and constants
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** payload of the initialisation message.								*/
	public static final String		INIT_ELECTION_MESSAGE = "initElection";
	/** a counter used to attribute unique node numbers to new nodes.		*/
	protected static AtomicInteger	numberGenerator = new AtomicInteger(0);
	/** the number attributed to this node.									*/
	protected int					nodeNumber;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the plug-in which will use a pool of threads with the given
	 * number of threads to execute its methods on the owner component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code nbThreads > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param nbThreads		number of threads used to execute its methods on the owner component.
	 */
	public				ElectionNodePlugin(int nbThreads)
	{
		super(nbThreads);

		assert	nbThreads > 0;
		this.nodeNumber = numberGenerator.getAndIncrement();
	}

	// -------------------------------------------------------------------------
	// Plug-in service implementations
	// -------------------------------------------------------------------------

	/**
	 * return the UID of this node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the UID of this node.
	 */
	public int			getNodeNumber()
	{
		return this.nodeNumber;
	}

	/**
	 * return true if {@code m} is an election message.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m		a message in the election protocol.
	 * @return		true if {@code m} is an election message.
	 */
	protected boolean	isInitElectionMessage(MessageI m)
	{
		Serializable p = m.getPayload();
		return p != null && p.equals(INIT_ELECTION_MESSAGE);
	}

	/**
	 * return true if {@code m} is a candidate message.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m		a message in the election protocol.
	 * @return		true if {@code m} is a candidate message.
	 */
	protected boolean	isElectionMessage(MessageI m)
	{
		Object p = m.getPayload();
		if (p != null && p instanceof Candidate) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * return true if {@code m} is an elected message.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m		a message in the election protocol.
	 * @return		true if {@code m} is an elected message.
	 */
	protected boolean	isElectedMessage(MessageI m)
	{
		Object p = m.getPayload();
		if (p != null && p instanceof Elected) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * send the election message to the next node in the ring.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m				an election message.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			sendElectionMessage(StandardMessage m)
	throws Exception
	{
		this.onNodeLock.readLock().lock();
		try {
			this.nsop.acceptOrPass(m);
		} finally {
			this.onNodeLock.readLock().unlock();
		}
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin#broadcast(fr.sorbonne_u.cps.sron.plugins.StandardMessage)
	 */
	@Override
	public void			broadcast(StandardMessage m) throws Exception
	{
		if (this.hasMarkedMessage(m)) {
			if (this.isInitElectionMessage(m)) {
				// end of the initialisation phase, starts the first phase of
				// the election
				this.getOwner().runTask(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((ElectionNode)this.getTaskOwner()).
													startElectionFirstPhase();
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});
			} else if (this.isElectedMessage(m)) {
				// end of the election
				this.getOwner().runTask(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									((ElectionNode)this.getTaskOwner()).
													endElection();
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						});				
			}
		}
		super.broadcast(m);
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin#processBroadcastMessage(fr.sorbonne_u.cps.sron.interfaces.MessageI)
	 */
	@Override
	protected void		processBroadcastMessage(MessageI m)
	{
		if (this.isInitElectionMessage(m)) {
			((ElectionNode)this.getOwner()).setParticipant(false);
			((ElectionNode)this.getOwner()).setElected(-1);
			this.getOwner().traceMessage(
					"Node " + this.nodeNumber + " initialises election.\n");
		} else if (this.isElectedMessage(m)) {
			int n = ((Elected)m.getPayload()).getUID();
			((ElectionNode)this.getOwner()).setElected(n);
		} else {
			this.getOwner().traceMessage("Unknown message " + m);
		}
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin#processAcceptedMessage(fr.sorbonne_u.cps.sron.interfaces.MessageI)
	 */
	@Override
	protected void		processAcceptedMessage(MessageI m)
	{
		if (this.isElectionMessage(m)) {
			int uid = ((Candidate)m.getPayload()).getUID();
			try {
				if (this.getNodeNumber() < uid) {
					this.onNodeLock.readLock().lock();
					try {
						this.nsop.acceptOrPass((StandardMessage) m);
					} finally {
						this.onNodeLock.readLock().unlock();
					}
					((ElectionNode)this.getOwner()).setParticipant(true);
					this.getOwner().traceMessage(
						"Node " + this.nodeNumber +
						" participates in election but will not be elected.\n");
				} else  if (uid < this.getNodeNumber()) {
					if (! ((ElectionNode)this.getOwner()).isParticipant()) {
						this.onNodeLock.readLock().lock();
						try {
							this.nsop.acceptOrPass(
								new StandardMessage(
									"message",
									new Candidate(this.getNodeNumber())));
						} finally {
							this.onNodeLock.readLock().unlock();
						}
					}
					((ElectionNode)this.getOwner()).setParticipant(true);
					this.getOwner().traceMessage(
							"Node " + this.nodeNumber +
							" participates in election and is candidate.\n");
				} else {
					assert	uid == this.getNodeNumber();
					this.onNodeLock.readLock().lock();
					try {
						((ElectionNode)this.getOwner()).broadcastElected();
					} finally {
						this.onNodeLock.readLock().unlock();
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin#accept(fr.sorbonne_u.cps.sron.interfaces.MessageI)
	 */
	@Override
	protected boolean	accept(MessageI m)
	{
		boolean ret = false;
		if (this.isElectionMessage(m) || this.isElectedMessage(m)) {
			ret = true;
		}
		return ret;
	}
}
// -----------------------------------------------------------------------------

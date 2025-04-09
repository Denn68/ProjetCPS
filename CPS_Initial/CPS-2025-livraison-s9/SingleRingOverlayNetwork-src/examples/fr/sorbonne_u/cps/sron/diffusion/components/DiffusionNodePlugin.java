package fr.sorbonne_u.cps.sron.diffusion.components;

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

import fr.sorbonne_u.cps.sron.interfaces.MessageI;
import fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin;
import java.util.concurrent.atomic.AtomicInteger;

// -----------------------------------------------------------------------------
/**
 * The class <code>DiffusionNodePlugin</code> implements the plug-in for
 * a ring overlay network node performing broadcasting and point-to-point
 * message passing.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class merely implements the abstract methods of the plug-in
 * <code>SRONNodePlugin</code>.
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
public class			DiffusionNodePlugin
extends		SRONNodePlugin
{
	// -------------------------------------------------------------------------
	// Plug-in variables and constants
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** a counter used to attribute unique node numbers to new nodes.		*/
	protected static AtomicInteger	numberGenerator = new AtomicInteger(-1);
	/** the number attributed to this node.									*/
	protected int					nodeNumber;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new diffusion node plug-in which will create and use
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
	public				DiffusionNodePlugin(int nbThreads)
	{
		super(nbThreads);

		this.nodeNumber = numberGenerator.incrementAndGet();
	}

	// -------------------------------------------------------------------------
	// Plug-in services implementation
	// -------------------------------------------------------------------------

	/**
	 * return the unique number attributed to this node.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the unique number attributed to this node.
	 */
	public int			getNodeNumber()
	{
		return this.nodeNumber;
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin#processBroadcastMessage(fr.sorbonne_u.cps.sron.interfaces.MessageI)
	 */
	@Override
	protected void		processBroadcastMessage(MessageI m)
	{
		this.getOwner().traceMessage("node " + this.getNodeNumber() +
									 " receives message " + m.getURI() + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin#processAcceptedMessage(fr.sorbonne_u.cps.sron.interfaces.MessageI)
	 */
	@Override
	protected void		processAcceptedMessage(MessageI m)
	{
		this.getOwner().traceMessage("node " + this.getNodeNumber() +
									 " accepts message " + m.getURI() + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.plugins.SRONNodePlugin#accept(fr.sorbonne_u.cps.sron.interfaces.MessageI)
	 */
	@Override
	protected boolean	accept(MessageI m)
	{
		this.getOwner().traceMessage("node " + this.getNodeNumber() +
									 " tests message " + m.getURI() + "\n");
		return ((Integer)m.getPayload()) == this.getNodeNumber();
	}
}
// -----------------------------------------------------------------------------

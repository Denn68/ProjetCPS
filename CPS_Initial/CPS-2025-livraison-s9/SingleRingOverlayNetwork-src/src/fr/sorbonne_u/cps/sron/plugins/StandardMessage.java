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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import fr.sorbonne_u.cps.sron.interfaces.MessageI;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardMessage</code> implements a standard message that
 * can be sent over the ring overlay network.
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
 * <p>Created on : 2019-04-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardMessage
implements	MessageI
{
	private static final long serialVersionUID = 1L;
	/** a generator to get unique message numbers.							*/
	protected static AtomicInteger	numberGenerator = new AtomicInteger(0);
	/** URI of the inbound port of the first node receiving this message
	 *  in the ring overlay network.										*/
	private String			firstReceiverInboundPortURI;
	/** URI of the message.													*/
	protected final String	uri;
	/** true if the message has been accepted, false otherwise.				*/
	protected boolean		accepted;
	/** âyload of the message (content).									*/
	protected Serializable	payload;
	
	/**
	 * create a message with the given prefix for its URI and no payload.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uriPrefix != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uriPrefix	prefix for the URI of the message.
	 */
	public				StandardMessage(String uriPrefix)
	{
		this(uriPrefix, null);
	}

	/**
	 * create a message with the given prefix for its URI and the given payload.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uriPrefix != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uriPrefix	prefix for the URI of the message.
	 * @param payload	payload of the message.
	 */
	public				StandardMessage(
		String uriPrefix,
		Serializable payload
		)
	{
		assert	uriPrefix != null;

		this.firstReceiverInboundPortURI = null;
		this.uri = uriPrefix + numberGenerator.getAndIncrement();
		this.accepted = false;
		this.payload = payload;
	}

	/**
	 * return the URI of the first node that received this message.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the URI of the first node that received this message.
	 */
	/* package */ String	getFirstReceiverInboundPortURI()
	{
		return this.firstReceiverInboundPortURI;
	}

	/**
	 * set the URI of the first node that received this message.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ibpURI	the URI of the first node that received this message.
	 */
	/* package */ void		setFirstReceiverInboundPortURI(
		String ibpURI
		)
	{
		assert	this.firstReceiverInboundPortURI == null;
		this.firstReceiverInboundPortURI = ibpURI;
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessageI#getURI()
	 */
	@Override
	public String		getURI()
	{
		return this.uri;
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessageI#accept()
	 */
	@Override
	public void			accept()
	{
		assert	!this.hasBeenAccepted();
		this.accepted = true;
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessageI#hasBeenAccepted()
	 */
	@Override
	public boolean		hasBeenAccepted()
	{
		return this.accepted;
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessageI#getPayload()
	 */
	@Override
	public Serializable	getPayload()
	{
		return this.payload;
	}

	/**
	 * @see fr.sorbonne_u.cps.sron.interfaces.MessageI#setPayload(java.io.Serializable)
	 */
	@Override
	public void			setPayload(Serializable payload)
	{
		this.payload = payload;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		return this.getClass().getSimpleName() + "["
									+ this.getURI() + ", "
									+ this.firstReceiverInboundPortURI + ", "
									+ this.hasBeenAccepted() + ", "
									+ this.getPayload() + "]";
	}
}
// -----------------------------------------------------------------------------

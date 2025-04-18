package fr.sorbonne_u.cps.sron.interfaces;

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

import fr.sorbonne_u.cps.sron.plugins.StandardMessage;

// -----------------------------------------------------------------------------
/**
 * The class <code>MessagingI</code> declares the signatures of methods used
 * in communicating over the ring overlay network.
 *
 * <p><strong>Description</strong></p>
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
public interface		MessagingI
{
	/**
	 * broadcast the message over the ring overlay network.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m				message to be broadcast.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			broadcast(StandardMessage m) throws Exception;

	/**
	 * accept (and process the message) or transmit it to the next
	 * node in the ring; method used for point-to-point communication.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m				message to be accepted (and processed) or transmitted.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			acceptOrPass(StandardMessage m) throws Exception;
}
// -----------------------------------------------------------------------------

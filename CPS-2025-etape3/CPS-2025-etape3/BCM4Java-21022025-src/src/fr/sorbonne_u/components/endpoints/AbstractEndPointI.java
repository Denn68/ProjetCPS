package fr.sorbonne_u.components.endpoints;

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

import fr.sorbonne_u.components.exceptions.ConnectionException;

/**
 * The interface <code>AbstractEndPointI</code> defines the signatures of
 * methods common to all end points;
 * @see fr.sorbonne_u.components.endpoints.EndPointI for more details
 * about end points and their usage.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: major revision to have two sets of interfaces and objects representing
 *       the server and the client sides to ensure that methods that can only
 *       be called on one side are only visible on that side; also, replace the
 *       copyWithSharable protocol by creating first a end point and then a
 *       server and a client side with dedicated methods.
 * 
 * <p>
 * The signatures declared in this interface apply to all end points. They
 * are meant to allow the initialisation of the end points both on the server
 * and the client sides before the client can use them to call the server
 * and their clean up, first on the client and then on the server side, after
 * their use to manage in an implementation-dependent way the resources used
 * in them (registries, ports, etc.).
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2024-07-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		AbstractEndPointI
{
	/**
	 * return true if the server side of this end point is initialised, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the server side of this end point is initialised, false otherwise.
	 */
	public boolean		serverSideInitialised();

	/**
	 * on the server side, initialise the server side of this end point,
	 * performing whatever action needed to make it readily usable.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !serverSideInitialised()}
	 * pre	{@code serverSideEndPointOwner != null}
	 * post	{@code serverSideInitialised()}
	 * </pre>
	 *
	 * @param serverSideEndPointOwner	server side end point owner.
	 * @throws ConnectionException		when {@code serverSideEndPointOwner} does not conform to the end point expectations.
	 */
	public void			initialiseServerSide(Object serverSideEndPointOwner)
	throws ConnectionException;

	/**
	 * on the client side only, return true if the client side of this end point
	 * is initialised, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the client side of this end point is initialised, false otherwise.
	 */
	public boolean		clientSideInitialised();

	/**
	 * on the client side only, initialise the client side of this end point,
	 * performing whatever action needed to make it readily usable.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code serverSideInitialised()}
	 * pre	{@code !clientSideInitialised()}
	 * pre	{@code clientSideEndPointOwner != null}
	 * post	{@code clientSideInitialised()}
	 * </pre>
	 *
	 * @param clientSideEndPointOwner	client side end point owner.
	 * @throws ConnectionException		when {@code clientSideEndPointOwner} does not conform to the end point expectations.
	 */
	public void			initialiseClientSide(Object clientSideEndPointOwner)
	throws ConnectionException;

	/**
	 * on the server side only, return true if the server side of the end point
	 * has been cleaned, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the server side of the end point has been cleaned, false otherwise.
	 */
	public boolean		serverSideClean();

	/**
	 * on the server side only, clean up the server side of this end point,
	 * performing whatever action required to disable it definitively.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !serverSideClean()}
	 * post	{@code serverSideClean()}
	 * </pre>
	 */
	public void			cleanUpServerSide();

	/**
	 * on the client side only, return true if the client side of the end point
	 * has been cleaned, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the client side of the end point has been cleaned, false otherwise.
	 */
	public boolean		clientSideClean();

	/**
	 * on the client side only, clean up the client side of this end point,
	 * performing whatever action required to disable it definitively.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !clientSideClean()}
	 * post	{@code clientSideClean()}
	 * </pre>
	 */
	public void			cleanUpClientSide();
}

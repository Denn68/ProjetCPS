package fr.sorbonne_u.cps.asynvcacl.components;

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

import fr.sorbonne_u.cps.asyncalc.components.AsynchronousCalculator;
import fr.sorbonne_u.cps.asyncalc.connections.AsynchronousCalculatorServicesInboundPort;
import fr.sorbonne_u.cps.asyncalc.interfaces.ResultReceptionCI;
import fr.sorbonne_u.cps.asynvcacl.connections.AsynchronousVectorCalculatorServicesInboundPort;
import fr.sorbonne_u.cps.asynvcacl.interfaces.AsynchronousVectorCalculatorServicesCI;
import fr.sorbonne_u.exceptions.PreconditionException;
import java.util.concurrent.Future;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

// -----------------------------------------------------------------------------
/**
 * The class <code>AsynchronousVectorCalculator</code> implements an
 * asynchronous calculator component that adds the capability to compute over
 * vector arguments to the capability of <code>AsynchronousCalculator</code> to
 * compute over scalar ones, and it does so by computing all results in parallel.
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
 * <p>Created on : 2020-05-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = {AsynchronousVectorCalculatorServicesCI.class})
@RequiredInterfaces(required = {ResultReceptionCI.class})
// -----------------------------------------------------------------------------
public class			AsynchronousVectorCalculator
extends		AsynchronousCalculator
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** URI of the pool of threads used to perform the computations in
	 *  parallel.															*/
	protected static final String	POOL_URI = "computations pool" ;
	/** number of threads to be used in the pool of threads.				*/
	protected static final int		NTHREADS = 5 ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			AsynchronousVectorCalculator() throws Exception
	{
		// using only one thread in the standard pool, which is the choice made
		// in the superclass, means that all client requests are executed in
		// mutual exclusion because serialised; however, having a separate pool
		// of threads to perform the computations allows to execute the
		// computations of each request in parallel
		super() ;
	}

	protected			AsynchronousVectorCalculator(
		String reflectionInboundPortURI
		) throws Exception
	{
		// using only one thread in the standard pool, which is the choice made
		// in the superclass, means that all client requests are executed in
		// mutual exclusion because serialised; however, having a separate pool
		// of threads to perform the computations allows to execute the
		// computations of each request in parallel
		super(reflectionInboundPortURI);
	}

	/**
	 * @see fr.sorbonne_u.cps.asyncalc.components.AsynchronousCalculator#initialise()
	 */
	@Override
	protected void		initialise() throws Exception
	{
		super.initialise() ;
		this.createNewExecutorService(POOL_URI, NTHREADS, false) ;
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.asyncalc.components.AsynchronousCalculator#createPort()
	 */
	@Override
	protected AsynchronousCalculatorServicesInboundPort	createPort()
	throws Exception
	{
		return new AsynchronousVectorCalculatorServicesInboundPort(
													INBOUND_PORT_URI, this) ;
	}

	// -------------------------------------------------------------------------
	// Component services implementation methods
	// -------------------------------------------------------------------------

	/**
	 * add each of the first operands with each of the second and return an
	 * array of the results by calling a port offering the interface
	 * <code>ResultReceptionCI</code> which URI is given as the last
	 * parameter; the serial number allows the caller to identify its call
	 * when receiving the result.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code xs != null && ys != null && xs.length == ys.length}
	 * pre	{@code resultReceptionInboundPortURI != null && !resultReceptionInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param xs			first vector operand.
	 * @param ys			second vector operand.
	 * @param serialNo		serial number of the call.
	 * @param resultReceptionInboundPortURI	URI of the port offering the interface <code>ResultReceptionCI</code> to return the result.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			vectorAddition(
		double[] xs,
		double[] ys,
		long serialNo,
		String resultReceptionInboundPortURI
		) throws Exception
	{
		assert	xs != null && ys != null && xs.length == ys.length :
				new PreconditionException(
						"xs != null && ys != null && xs.length == ys.length");
		assert	resultReceptionInboundPortURI != null &&
									!resultReceptionInboundPortURI.isEmpty() :
				new PreconditionException(
						"resultReceptionInboundPortURI != null && "
						+ "!resultReceptionInboundPortURI.isEmpty()");

		@SuppressWarnings("unchecked")
		// create an array for future variables to wait for the results
		Future<Double>[] fs = new Future[xs.length] ;
		for (int i = 0 ; i < xs.length ; i++) {
			// launch in turn all of the computations, gathering the future 
			// variables into the array
			final int index = i ;
			fs[i] = this.baselineHandleRequest(
							POOL_URI,
							new AbstractComponent.AbstractService<Double>() {
								@Override
								public Double call() throws Exception {
									// to force a more lengthy operation
									Thread.sleep(100L) ;
									return xs[index] + ys[index];
								}
							}) ;
		}
		// create the results array
		double[] results = new double[fs.length] ;
		for (int i = 0 ; i < fs.length ; i++) {
			// get the results for all of the computations in turn, waiting
			// when the results are not yet computed
			results[i] = fs[i].get() ;
		}

		// sending the results back to the client
		this.sendResult(results, serialNo, resultReceptionInboundPortURI);
	}

	/**
	 * subtract each of the first operands with each of the second and return
	 * an array of the results by calling a port offering the interface
	 * <code>ResultReceptionCI</code> which URI is given as the last
	 * parameter; the serial number allows the caller to identify its call
	 * when receiving the result.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code xs != null && ys != null && xs.length == ys.length}
	 * pre	{@code resultReceptionInboundPortURI != null && !resultReceptionInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param xs			first vector operand.
	 * @param ys			second vector operand.
	 * @param serialNo		serial number of the call.
	 * @param resultReceptionInboundPortURI	URI of the port offering the interface <code>ResultReceptionCI</code> to return the result.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			vectorSubtraction(
		double[] xs,
		double[] ys,
		long serialNo,
		String resultReceptionInboundPortURI
		) throws Exception
	{
		assert	xs != null && ys != null && xs.length == ys.length :
				new PreconditionException(
					"xs != null && ys != null && xs.length == ys.length");
		assert	resultReceptionInboundPortURI != null &&
								!resultReceptionInboundPortURI.isEmpty() :
				new PreconditionException(
						"resultReceptionInboundPortURI != null && "
						+ "!resultReceptionInboundPortURI.isEmpty()");

		@SuppressWarnings("unchecked")
		// create an array for future variables to wait for the results
		Future<Double>[] fs = new Future[xs.length] ;
		for (int i = 0 ; i < xs.length ; i++) {
			// launch in turn all of the computations, gathering the future 
			// variables into the array
			final int index = i ;
			fs[i] = this.baselineHandleRequest(
							POOL_URI,
							new AbstractComponent.AbstractService<Double>() {
								@Override
								public Double call() throws Exception {
									// to force a more lengthy operation
									Thread.sleep(100L) ;
									return xs[index] - ys[index];
								}
							}) ;
		}
		// create the results array
		double[] results = new double[fs.length] ;
		for (int i = 0 ; i < fs.length ; i++) {
			// get the results for all of the computations in turn, waiting
			// when the results are not yet computed
			results[i] = fs[i].get() ;
		}

		// sending the results back to the client
		this.sendResult(results, serialNo, resultReceptionInboundPortURI);
	}
}
// -----------------------------------------------------------------------------

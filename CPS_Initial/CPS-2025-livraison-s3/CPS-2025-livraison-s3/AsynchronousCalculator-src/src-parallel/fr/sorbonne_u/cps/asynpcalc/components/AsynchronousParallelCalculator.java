package fr.sorbonne_u.cps.asynpcalc.components;

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
import fr.sorbonne_u.cps.asyncalc.connections.ResultReceptionConnector;
import fr.sorbonne_u.cps.asyncalc.interfaces.ResultReceptionCI;
import fr.sorbonne_u.cps.asynpcalc.connections.AsynchronousParallelCalculatorServicesInboundPort;
import fr.sorbonne_u.cps.asynpcalc.interfaces.AsynchronousParallelCalculatorServicesCI;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

// -----------------------------------------------------------------------------
/**
 * The class <code>AsynchronousParallelCalculator</code> implements an
 * asynchronous calculator component that adds the capability to compute over
 * vector arguments to the capability of <code>AsynchronousCalculator</code> to
 * compute over scalar ones; it does so by computing all results in parallel
 * and getting them and sending them back to the client in any order of
 * termination rather than all together to provide more parallelism.
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
 * <p>Created on : 2020-05-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = {AsynchronousParallelCalculatorServicesCI.class})
@RequiredInterfaces(required = {ResultReceptionCI.class})
// -----------------------------------------------------------------------------
public class			AsynchronousParallelCalculator
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

	protected			AsynchronousParallelCalculator() throws Exception
	{
		// using only one thread in the standard pool, which is the choice made
		// in the superclass, means that all client requests are executed in
		// mutual exclusion because serialised; however, having a separate pool
		// of threads to perform the computations allows to execute the
		// computations of each request in parallel
		super() ;
	}

	protected			AsynchronousParallelCalculator(
		String reflectionInboundPortURI
		) throws Exception
	{
		// using only one thread in the standard pool, which is the choice made
		// in the superclass, means that all client requests are executed in
		// mutual exclusion because serialised; however, having a separate pool
		// of threads to perform the computations allows to execute the
		// computations of each request in parallel
		super(reflectionInboundPortURI) ;
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
		return new AsynchronousParallelCalculatorServicesInboundPort(
													INBOUND_PORT_URI, this) ;
	}

	// -------------------------------------------------------------------------
	// Component services implementation methods
	// -------------------------------------------------------------------------

	/**
	 * add each of the first operands with each of the second and return
	 * the results one by one, as soon as they become available, by calling
	 * a port offering the interface <code>ResultReceptionCI</code> which URI
	 * is given as the last parameter; the serial numbers allow the caller to
	 * identify its each of the results when receiving them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code xs != null && ys != null && xs.length == ys.length}
	 * pre	{@code serialNo != null && xs.length == serialNo.length}
	 * pre	{@code resultReceptionInboundPortURI != null && !resultReceptionInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param xs			array of first operands.
	 * @param ys			array of second operands.
	 * @param serialNo		array of serial numbers for each result.
	 * @param resultReceptionInboundPortURI	URI of the port offering the interface <code>ResultReceptionCI</code> to return the result.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			parallelAdditions(
		double[] xs,
		double[] ys,
		long[] serialNo,
		String resultReceptionInboundPortURI
		) throws Exception
	{
		assert	xs != null && ys != null && xs.length == ys.length :
				new PreconditionException(
						"xs != null && ys != null && xs.length == ys.length");
		assert	serialNo != null && xs.length == serialNo.length :
				new PreconditionException(
						"serialNo != null && xs.length == serialNo.length");
		assert	resultReceptionInboundPortURI != null &&
								!resultReceptionInboundPortURI.isEmpty() :
				new PreconditionException(
						"resultReceptionInboundPortURI != null && "
						+ "!resultReceptionInboundPortURI.isEmpty()");

		// a hash map allowing to keep together future variables representing
		// the awaited results and the serial number used by the client to
		// identify the computation
		Map<Future<Double>,Long> tempResults =
										new HashMap<Future<Double>,Long>() ;
		// create the completion service over the pool of threads
		ExecutorCompletionService<Double> ecs =
				new ExecutorCompletionService<Double>(
										this.getExecutorService(POOL_URI)) ;
		ArrayList<AbstractComponent.AbstractService<Double>> requests =
															new ArrayList<>() ;

		// waiting time to simulate more lengthy computations
		final long waiting = 200;
		// create all of the tasks but they are not launched yet
		for (int i = 0 ; i < xs.length ; i++) {
			final int index = i ;
			AbstractComponent.AbstractService<Double> request =
					new AbstractComponent.AbstractService<Double>() {
								@Override
								public Double call() throws Exception {
									// to force a more lengthy operation
									Thread.sleep(waiting) ;
									return xs[index] + ys[index];
								}
							};
			// the setOwner is normally done by BCM4Java when submitting through
			// its handleRequest or runTask methods, but here we use submit,
			// hence setting the owner must be done manually
			request.setOwnerReference(this);
			// accumulate the request to submit them after
			requests.add(request) ;
		}

		// launch all tasks, putting their future variables in a hash map
		for (int i = 0 ; i < requests.size() ; i++) {
			Future<Double> f = ecs.submit(requests.get(i));
			// put the future variables with their associated serial number
			// in a hash map to get them back when the results will come out
			tempResults.put(f, serialNo[i]) ;
		}

		// do not use sendResult here to avoid connecting/disconnecting the
		// port for each result
		this.doPortConnection(
				this.rrop.getPortURI(),
				resultReceptionInboundPortURI,
				ResultReceptionConnector.class.getCanonicalName()) ;

		for (int i = 0 ; i < requests.size() ; i++) {
			// take returns the next computation which result is available
			Future<Double> f = ecs.take();
			// get the serial number associated to the result just obtained
			long no = tempResults.get(f);
			// send the result back to the client
			this.rrop.acceptResult(no, f.get()) ;
		}

		this.doPortDisconnection(this.rrop.getPortURI()) ;
	}

	/**
	 * subtract each of the first operands with each of the second and return
	 * the results one by one, as soon as they become available, by calling
	 * a port offering the interface <code>ResultReceptionCI</code> which URI
	 * is given as the last parameter; the serial numbers allow the caller to
	 * identify its each of the results when receiving them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code xs != null && ys != null && xs.length == ys.length}
	 * pre	{@code serialNo != null && xs.length == serialNo.length}
	 * pre	{@code resultReceptionInboundPortURI != null && !resultReceptionInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param xs			array of first operands.
	 * @param ys			array of second operands.
	 * @param serialNo		array of serial numbers for each result.
	 * @param resultReceptionInboundPortURI	URI of the port offering the interface <code>ResultReceptionCI</code> to return the result.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			parallelSubtractions(
			double[] xs,
			double[] ys,
			long[] serialNo,
			String resultReceptionInboundPortURI
			) throws Exception
	{
		assert	xs != null && ys != null && xs.length == ys.length :
				new PreconditionException(
						"xs != null && ys != null && xs.length == ys.length");
		assert	serialNo != null && xs.length == serialNo.length :
				new PreconditionException(
						"serialNo != null && xs.length == serialNo.length");
		assert	resultReceptionInboundPortURI != null &&
									!resultReceptionInboundPortURI.isEmpty() :
				new PreconditionException(
						"resultReceptionInboundPortURI != null && "
						+ "!resultReceptionInboundPortURI.isEmpty()");

		// a hash map allowing to keep together future variables representing
		// the awaited results and the serial number used by the client to
		// identify the computation
		Map<Future<Double>,Long> tempResults =
										new HashMap<Future<Double>,Long>() ;
		// create the completion service over the pool of threads
		ExecutorCompletionService<Double> ecs =
				new ExecutorCompletionService<Double>(
										this.getExecutorService(POOL_URI)) ;
		ArrayList<AbstractComponent.AbstractService<Double>> requests =
															new ArrayList<>() ;

		// waiting time to simulate more lengthy computations
		final long waiting = 200;
		for (int i = 0 ; i < xs.length ; i++) {
			final int index = i ;
			AbstractComponent.AbstractService<Double> request =
					new AbstractComponent.AbstractService<Double>() {
								@Override
								public Double call() throws Exception {
									// to force a more lengthy operation
									Thread.sleep(waiting) ;
									return xs[index] - ys[index];
								}
							} ;
			// the setOwner is normally done by BCM4Java when submitting through
			// its handleRequest or runTask methods, but here we use submit,
			// hence setting the owner must be done manually
			request.setOwnerReference(this) ;
			// accumulate the request to submit them after
			requests.add(request) ;
		}

		// launch all tasks, putting their future variables in a hash map
		for (int i = 0 ; i < requests.size() ; i++) {
			Future<Double> f = ecs.submit(requests.get(i)) ;
			// put the future variables with their associated serial number
			// in a hash map to get them back when the results will come out
			tempResults.put(f, serialNo[i]) ;
		}

		// do not use sendResult here to avoid connecting/disconnecting the
		// port for each result
		this.doPortConnection(
				this.rrop.getPortURI(),
				resultReceptionInboundPortURI,
				ResultReceptionConnector.class.getCanonicalName()) ;

		for (int i = 0 ; i < requests.size() ; i++) {
			// take returns the next computation which result is available
			Future<Double> f = ecs.take();
			// get the serial number associated to the result just obtained
			long no = tempResults.get(f);
			// send the result back to the client
			this.rrop.acceptResult(no, f.get()) ;
		}

		this.doPortDisconnection(this.rrop.getPortURI()) ;
	}
}
// -----------------------------------------------------------------------------

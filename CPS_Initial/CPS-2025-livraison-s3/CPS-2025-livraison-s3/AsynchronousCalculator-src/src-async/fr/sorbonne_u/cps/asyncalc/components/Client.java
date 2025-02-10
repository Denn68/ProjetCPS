package fr.sorbonne_u.cps.asyncalc.components;

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
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.cps.asyncalc.connections.AsynchronousCalculatorServicesConnector;
import fr.sorbonne_u.cps.asyncalc.connections.AsynchronousCalculatorServicesOutboundPort;
import fr.sorbonne_u.cps.asyncalc.connections.ResultReceptionInboundPort;
import fr.sorbonne_u.cps.asyncalc.interfaces.AsynchronousCalculatorServicesCI;
import fr.sorbonne_u.cps.asyncalc.interfaces.ResultReceptionCI;

import java.util.HashMap;

// -----------------------------------------------------------------------------
/**
 * The class <code>Client</code> implements a component that calls the
 * asynchronous calculator component to show how it works.
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
 * <p>Created on : 2020-05-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = {ResultReceptionCI.class})
@RequiredInterfaces(required = {AsynchronousCalculatorServicesCI.class})
// -----------------------------------------------------------------------------
public class			Client
extends		AbstractComponent
implements	ResultReceptorI
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** serial number counter.											 	*/
	protected long							serialNo = 0;
	/** inbound port offering the component interface
	 *  {@code ResultReceptionCI}.											*/
	protected ResultReceptionInboundPort	rrip;
	/** hash map storing the parameters of computations waiting for their
	 *  result, using the serial number of the request as key.				*/
	protected HashMap<Long, Object[]>		awaitedComputations;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a client component with two threads (one to execute the test
	 * scenario and one to receive asynchronously the results of the
	 * computations).
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
	protected			Client() throws Exception
	{
		super(2, 0);
		this.initialise();
	}

	/**
	 * create a client component with two threads (one to execute the test
	 * scenario and one to receive asynchronously the results of the
	 * computations) and the given URI of the reflection inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			Client(String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 2, 0);
		this.initialise();
	}

	/**
	 * initialise the component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void	initialise() throws Exception
	{
		// create and publish the inbound port to receive the result of the
		// computations
		this.rrip = new ResultReceptionInboundPort(this);
		this.rrip.publishPort();

		this.getTracer().setTitle("Client");
		this.getTracer().setRelativePosition(1, 0);
		this.toggleTracing();

		this.awaitedComputations = new HashMap<Long, Object[]>();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		// create the outbound port, publish it and connect it to call the
		// asynchronous calculator
		AsynchronousCalculatorServicesOutboundPort p =
						new AsynchronousCalculatorServicesOutboundPort(this);
		p.publishPort();
		this.doPortConnection(
			p.getPortURI(),
			AsynchronousCalculator.INBOUND_PORT_URI,
			AsynchronousCalculatorServicesConnector.class.getCanonicalName());

		// generate a unique serial number for the computation
		long no = this.serialNo++;
		// store the information about the computation to be requested from
		// the asynchronous calculator
		this.awaitedComputations.put(no, new Object[]{"addition", 10, 15});
		// call the asynchronous calculator to perform the computation
		p.addition(10, 15, no, this.rrip.getPortURI());

		// repeat for a second computation
		no = this.serialNo++;
		this.awaitedComputations.put(no, new Object[]{"subtraction", 100, 50});
		p.subtraction(100, 50, no, this.rrip.getPortURI());

		this.doPortDisconnection(p.getPortURI());
		p.unpublishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.rrip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.asyncalc.components.ResultReceptorI#acceptResult(long, java.lang.Object)
	 */
	@Override
	public void			acceptResult(long serialNo, Object result)
	throws Exception
	{
		assert	this.awaitedComputations.containsKey(serialNo);
		// retrieve the stored information about the computation which result
		// is received
		Object[] operation = this.awaitedComputations.remove(serialNo);
		// simple trace to show the computation and its result
		this.traceMessage("result of the " + operation[0] +
						  " of the operands (" + operation[1] + ", " +
						  						 operation[2] +
						  ") is " + result + "\n");
	}
}
// -----------------------------------------------------------------------------

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

import fr.sorbonne_u.cps.asyncalc.components.Client;
import fr.sorbonne_u.cps.asyncalc.interfaces.ResultReceptionCI;
import fr.sorbonne_u.cps.asynvcacl.connections.AsynchronousVectorCalculatorServicesConnector;
import fr.sorbonne_u.cps.asynvcacl.connections.AsynchronousVectorCalculatorServicesOutboundPort;
import fr.sorbonne_u.cps.asynvcacl.interfaces.AsynchronousVectorCalculatorServicesCI;

import java.lang.reflect.Array;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.asyncalc.components.AsynchronousCalculator;

// -----------------------------------------------------------------------------
/**
 * The class <code>VectorialClient</code> implements a component that calls the
 * asynchronous vector calculator component to show how it works.
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
@OfferedInterfaces(offered = {ResultReceptionCI.class})
@RequiredInterfaces(required = {AsynchronousVectorCalculatorServicesCI.class})
// -----------------------------------------------------------------------------
public class			VectorClient
extends		Client
{
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			VectorClient() throws Exception
	{
		super() ;
	}

	protected			VectorClient(String reflectionInboundPortURI)
	throws Exception
	{
		super(reflectionInboundPortURI) ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.asyncalc.components.Client#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		AsynchronousVectorCalculatorServicesOutboundPort p =
				new AsynchronousVectorCalculatorServicesOutboundPort(this) ;
		p.publishPort() ;
		this.doPortConnection(
				p.getPortURI(),
				AsynchronousCalculator.INBOUND_PORT_URI,
				AsynchronousVectorCalculatorServicesConnector.class.
														getCanonicalName()) ;

		long no = this.serialNo++ ;
		this.awaitedComputations.put(no,
				new Object[]{"additions", new double[]{10, 100},
										  new double[]{15, 150}}) ;
		p.vectorAddition(new double[]{10, 100}, new double[]{15, 150},
						no, this.rrip.getPortURI()) ;

		no = this.serialNo++ ;
		this.awaitedComputations.put(no,
				new Object[]{"subtractions", new double[]{100, 1000},
											 new double[]{50, 500}}) ;
		p.vectorSubtraction(new double[]{100, 1000}, new double[]{50, 500},
						   no, this.rrip.getPortURI()) ;

		Thread.sleep(500L) ;
		this.doPortDisconnection(p.getPortURI()) ;
		p.unpublishPort() ;
	}

	// -------------------------------------------------------------------------
	// Component services implementation methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.asyncalc.components.Client#acceptResult(long, java.lang.Object)
	 */
	@Override
	public void			acceptResult(long serialNo, Object result)
	throws Exception
	{
		if (result.getClass().isArray()) {
			Object[] results = this.convertToArray(result) ;
			Object[] operation = this.awaitedComputations.remove(serialNo) ;
			Object[] operands1 = this.convertToArray(operation[1]) ;
			Object[] operands2 = this.convertToArray(operation[2]) ;
			this.traceMessage(
					"results of the " + operation[0] +
					" of the operands (" +
							this.convertToString(operands1) + ", " +
							this.convertToString(operands2) +
					") are " + this.convertToString(results) + "\n") ;

		} else {
			super.acceptResult(serialNo, result) ;
		}
	}

	// -------------------------------------------------------------------------
	// INTERNAL methods
	// -------------------------------------------------------------------------

	/**
	 * converting an object to an object array, provided that the object is
	 * effectively an array.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	o.getClass().isArray()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param o		object ot be converted to an array.
	 * @return		object array resulting from the conversion.
	 */
	protected Object[]	convertToArray(Object o)
	{
		assert	o.getClass().isArray() ;
		Object[] results = new Object[Array.getLength(o)] ;
		for (int i = 0 ; i < results.length ; i++) {
			results[i] = Array.get(o, i) ;
		}
		return results ;
	}

	/**
	 * converting an object array to a string.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	a != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param a		object array to be converted.
	 * @return		a string representing the content of the array.
	 */
	protected String	convertToString(Object[] a)
	{
		assert	a != null ;
		StringBuffer sb = new StringBuffer("{") ;
		for (int i = 0 ; i < a.length ; i++) {
			sb.append(a[i]) ;
			if (i < a.length - 1) {
				sb.append(", ") ;
			}
		}
		sb.append("}") ;
		return sb.toString() ;
	}
}
// -----------------------------------------------------------------------------

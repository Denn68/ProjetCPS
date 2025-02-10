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

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.asyncalc.components.AsynchronousCalculator;
import fr.sorbonne_u.cps.asyncalc.components.Client;
import fr.sorbonne_u.cps.asyncalc.interfaces.ResultReceptionCI;
import fr.sorbonne_u.cps.asynpcalc.connections.AsynchronousParallelCalculatorServicesConnector;
import fr.sorbonne_u.cps.asynpcalc.connections.AsynchronousParallelCalculatorServicesOutboundPort;
import fr.sorbonne_u.cps.asynpcalc.interfaces.AsynchronousParallelCalculatorServicesCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ParallelClient</code> implements a component that calls the
 * parallel vector calculator component to show how it works.
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
 * <p>Created on : 2020-05-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = {ResultReceptionCI.class})
@RequiredInterfaces(required = {AsynchronousParallelCalculatorServicesCI.class})
// -----------------------------------------------------------------------------
public class			ParallelClient
extends		Client
{
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			ParallelClient() throws Exception
	{
		super() ;
	}

	protected			ParallelClient(String reflectionInboundPortURI)
	throws Exception
	{
		super(reflectionInboundPortURI);
	}

	// -------------------------------------------------------------------------
	// Component life-cycle methods
	// -------------------------------------------------------------------------

	@Override
	public void			execute() throws Exception
	{
		AsynchronousParallelCalculatorServicesOutboundPort p =
				new AsynchronousParallelCalculatorServicesOutboundPort(this) ;
		p.publishPort() ;
		this.doPortConnection(
			p.getPortURI(),
			AsynchronousCalculator.INBOUND_PORT_URI,
			AsynchronousParallelCalculatorServicesConnector.class.
														getCanonicalName()) ;

		double[] operand1 = new double[]{10.0, 100.0} ;		
		double[] operand2 = new double[]{2.0, 20.0} ;
		long[] no1 = new long[operand1.length] ;
		for (int i = 0 ; i < no1.length ; i++) {
			no1[i] = this.serialNo++ ;
			this.awaitedComputations.put(
					no1[i],
					new Object[]{"addition", operand1[i], operand2[i]}) ;
		}
		p.parallelAdditions(operand1, operand2, no1, this.rrip.getPortURI());

		long[] no2 = new long[operand1.length] ;
		for (int i = 0 ; i < no2.length ; i++) {
			no2[i] = this.serialNo++ ;
			this.awaitedComputations.put(
					no2[i],
					new Object[]{"subtraction", operand1[i], operand2[i]}) ;
		}
		p.parallelSubtractions(operand1, operand2, no2, this.rrip.getPortURI());

		this.doPortDisconnection(p.getPortURI()) ;
		p.unpublishPort() ;
	}
}
// -----------------------------------------------------------------------------

package fr.sorbonne_u.cps.asynvcacl.connections;

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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.asyncalc.connections.AsynchronousCalculatorServicesInboundPort;
import fr.sorbonne_u.cps.asynvcacl.components.AsynchronousVectorCalculator;
import fr.sorbonne_u.cps.asynvcacl.interfaces.AsynchronousVectorCalculatorServicesCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AsynchronousVectorialCalculatorServicesInboundPort</code>
 * implements the inbound port for the component interface
 * {@code AsynchronousVectorCalculatorServicesCI}.
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
public class			AsynchronousVectorCalculatorServicesInboundPort
extends		AsynchronousCalculatorServicesInboundPort
implements	AsynchronousVectorCalculatorServicesCI
{
	private static final long serialVersionUID = 1L;

	public				AsynchronousVectorCalculatorServicesInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(AsynchronousVectorCalculatorServicesCI.class, owner);
		assert	owner instanceof AsynchronousVectorCalculator ;
	}

	public				AsynchronousVectorCalculatorServicesInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AsynchronousVectorCalculatorServicesCI.class, owner);
		assert	owner instanceof AsynchronousVectorCalculator ;
	}

	/**
	 * @see fr.sorbonne_u.cps.asynvcacl.interfaces.AsynchronousVectorCalculatorServicesCI#vectorAddition(double[], double[], long, java.lang.String)
	 */
	@Override
	public void			vectorAddition(
		double[] xs,
		double[] ys,
		long serialNo,
		String resultReceptionInboundPortURI
		) throws Exception
	{
		this.getOwner().runTask(
				o -> {	try {
							((AsynchronousVectorCalculator)o).
									vectorAddition(
												xs, ys, serialNo,
												resultReceptionInboundPortURI);
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					 }) ;
	}

	/**
	 * @see fr.sorbonne_u.cps.asynvcacl.interfaces.AsynchronousVectorCalculatorServicesCI#vectorSubtraction(double[], double[], long, java.lang.String)
	 */
	@Override
	public void			vectorSubtraction(
		double[] xs,
		double[] ys,
		long serialNo,
		String resultReceptionInboundPortURI
		) throws Exception
	{
		this.getOwner().runTask(
				o -> {	try {
							((AsynchronousVectorCalculator)o).
									vectorSubtraction(
												xs, ys, serialNo,
												resultReceptionInboundPortURI);
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					 }) ;
	}
}
// -----------------------------------------------------------------------------

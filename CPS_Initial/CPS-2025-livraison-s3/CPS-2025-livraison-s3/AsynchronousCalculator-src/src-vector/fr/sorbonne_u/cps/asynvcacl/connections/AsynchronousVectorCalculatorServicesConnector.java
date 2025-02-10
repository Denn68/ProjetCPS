package fr.sorbonne_u.cps.asynvcacl.connections;

import fr.sorbonne_u.cps.asyncalc.connections.AsynchronousCalculatorServicesConnector;
import fr.sorbonne_u.cps.asynvcacl.interfaces.AsynchronousVectorCalculatorServicesCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AsynchronousVectorialCalculatorServicesConnector</code>
 * implements the connector for the component interface
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
public class			AsynchronousVectorCalculatorServicesConnector
extends		AsynchronousCalculatorServicesConnector
implements	AsynchronousVectorCalculatorServicesCI
{
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
		((AsynchronousVectorCalculatorServicesCI)this.offering).
				vectorAddition(xs, ys, serialNo,
								  resultReceptionInboundPortURI) ;
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
		((AsynchronousVectorCalculatorServicesCI)this.offering).
				vectorSubtraction(xs, ys, serialNo,
									 resultReceptionInboundPortURI) ;
	}
}
// -----------------------------------------------------------------------------

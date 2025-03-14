package fr.sorbonne_u.utils;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
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

// -----------------------------------------------------------------------------
/**
 * The class <code>Pair</code> implements a simple pair object containing two
 * values.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-07-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Pair<A,B>
implements	PairI<A,B>
{
	private A			first;
	private B			second;

	/**
	 * create a pair object with the two given values.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	{@code getFirst() == first && getSecond() == second}
	 * </pre>
	 *
	 * @param first		value to be put as first element in the pair.
	 * @param second	value to be put as second element in the pair.
	 */
	public				Pair(A first, B second)
	{
		super();
		this.first = first;
		this.second = second;
	}

	/**
	 * @see fr.sorbonne_u.utils.PairI#getFirst()
	 */
	@Override
	public A			getFirst()
	{
		return this.first;
	}

	/**
	 * @see fr.sorbonne_u.utils.PairI#setFirst(java.lang.Object)
	 */
	@Override
	public A			setFirst(A value)
	{
		A old = this.first;
		this.first = value;
		return old;
	}

	/**
	 * @see fr.sorbonne_u.utils.PairI#getSecond()
	 */
	@Override
	public B			getSecond()
	{
		return this.second;
	}

	/**
	 * @see fr.sorbonne_u.utils.PairI#setSecond(java.lang.Object)
	 */
	@Override
	public B			setSecond(B value)
	{
		B old = this.second;
		this.second = value;
		return old;
	}
}
// -----------------------------------------------------------------------------

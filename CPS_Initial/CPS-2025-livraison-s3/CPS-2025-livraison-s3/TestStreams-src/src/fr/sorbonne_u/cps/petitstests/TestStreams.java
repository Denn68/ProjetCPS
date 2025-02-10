package fr.sorbonne_u.cps.petitstests;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The class <code>TestStreams</code>
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
 * <p>Created on : 2025-01-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			TestStreams
{
	/**
	 * The functional interface <code>F3I</code> represents a function with
	 * three integer parameters.
	 *
	 * <p>Created on : 2025-01-29</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	@FunctionalInterface
	public static interface		F3I
	{
		public int apply(int i, int j, int k);
	}

	/**
	 * The class <code>Somme3</code> implements the function with three integer
	 * parameters that returns the sum of its three parameters.
	 *
	 * <p>Created on : 2025-01-29</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class			Somme3 implements F3I
	{
		@Override
		public int apply(int i, int j, int k) { return i + j + k; }
	}

	/**
	 * return true if {@code i} is even, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param i	an integer.
	 * @return	true if {@code i} is even, false otherwise.
	 */
	public static boolean	verboseSelector(int i)
	{
		boolean res = i % 2 == 0;
		System.out.println("Thread no " + Thread.currentThread().getId() +
						   " executes selector(" + i + ") giving " + res);
		return res;
	}

	/**
	 * return	the square of {@code i} as a {@code double} value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param i	an integer.
	 * @return	the square of {@code i} as a {@code double} value.
	 */
	public static double	verboseProcessor(int i)
	{
		double res = Math.pow(i, 2.0);
		System.out.println("Thread no " + Thread.currentThread().getId() +
						   " executes processor(" + i + ") giving " + res);
		return res;
	}

	/**
	 * return the sum of {@code u} and the integer value of {@code d}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param u	an integer.
	 * @param d	a real.
	 * @return	the sum of {@code u} and the integer value of {@code d}.
	 */
	public static int		verboseReductor(int u, double d)
	{
		int res = u + ((int)d);
		System.out.println("Thread no " + Thread.currentThread().getId() +
						   " executes reductor(" + u + ", " + d +
						   ") giving " + res);
		return res;
	}

	/**
	 * return the sum of {@code u1} and {@code u2}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param u1	an integer.
	 * @param u2	an integer.
	 * @return	the sum of {@code u1} and {@code u2}.
	 */
	public static int		verboseCombinator(int u1, int u2)
	{
		int res = u1 + u2;
		System.out.println("Thread no " + Thread.currentThread().getId() +
						   " executes combinator(" + u1 + ", " + u2 +
						   ") giving " + res);
        return res;
	}

	/**
	 * illustrates the use of functions, streams and parallel streams in
	 * map/reduce computations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param args	command line arguments, none are used here.
	 */
	public static void main(String[] args)
	{
		// ---------------------------------------------------------------------
		// Functions and sequential streams
		// ---------------------------------------------------------------------

		System.out.println(
				"Executing i -> i % 2 == 0 on 4 gives: " +
				((Predicate<Integer>)(i -> i % 2 == 0)).test(4));
		System.out.println(
				"Executing x -> Math.pow(x, 2.0) on 5 gives: " +
				((Function<Integer,Double>)(x -> Math.pow(x, 2.0))).apply(5));
		System.out.println(
				"Executing (s, c) -> Integer.parseInt(s + c) on \"1\" and '0' gives: " +
				((BiFunction<String,Character,Integer>)
						((s, c) -> Integer.parseInt(s + c))).apply("1", '0'));
		System.out.println(
				"Executing (x, y) -> x + y on 2.0 and 3.0 gives: " +
				((BinaryOperator<Double>)((x, y) -> x + y)).apply(2.0, 3.0));
		System.out.println(
				"Executing (new Somme3()).apply(1, 2, 3) gives: " +
				(new Somme3()).apply(1, 2, 3));
		System.out.println(
				"Executing ((F3I)((i, j, k) -> i + j + k)).apply(1, 2, 3) gives: " +
				((F3I)((i, j, k) -> i + j + k)).apply(1, 2, 3));

		// First "classical" map/reduce
		System.out.println(
			"Executing map/reduce on Stream.of(new Integer[]{1, 2, 3, 4, 5}) gives: " +
			Stream.of(new Integer[]{1, 2, 3, 4, 5})
				.filter(i -> i % 2 == 0)
				.map(i -> Math.pow(i, 2.0))
				.reduce(0, (u, d) -> u + d.intValue(), (u1, u2) -> u1 + u2));

		// Second map/reduce using verbose functions to see what threads are
		// used to execute the functions.
		System.out.println(
			"Executing verbose map/reduce on Stream.of(new Integer[]{1, 2, 3, 4, 5}) gives: " +
			Stream.of(new Integer[]{1, 2, 3, 4, 5})
			  .filter(TestStreams::verboseSelector)
			  .map(TestStreams::verboseProcessor)
			  .reduce(0,
					  TestStreams::verboseReductor,
					  TestStreams::verboseCombinator));

		// ---------------------------------------------------------------------
		// Parallel streams
		// ---------------------------------------------------------------------

		// Test parameters
		final int	NUMBER_OF_THREADS = 2;
		final int	NUMBER_OF_VALUES = 10;

		// ForkJoinPool creation with the prescribed number of threads.
		ForkJoinPool fjp = new ForkJoinPool(NUMBER_OF_THREADS);
		System.out.println("The ForkJoinPool has " + fjp.getParallelism() + " parallelism level.");
		System.out.println("The ForkJoinPool has " + fjp.getParallelism() + " current size.");

		// Data source creation, with the prescribed number of values
		// computing the expected result of the map/reduce as comparison basis
		Integer[] intArray = new Integer[NUMBER_OF_VALUES];
		int expectedResult = 0;
		for (int i = 1 ; i <= NUMBER_OF_VALUES; i++) {
			if (i % 2 == 0) {
				expectedResult += i * i;	
			}
			intArray[i - 1] = i;
		}
		System.out.println("expected result: " + expectedResult);

		// Parallel stream creation
		Stream<Integer> parIntStream = Stream.of(intArray).parallel();

		// map/reduce computation creation and submission to the ForkJoinPool
		// getting the corresponding FokJoinTask executed by the pool.
		ForkJoinTask<Integer> fjt =
			fjp.submit(() ->
				parIntStream
					.filter(i -> i % 2 == 0)	// conserve les valeurs paires
					.map(i -> Math.pow(i, 2.0))	// met au carré
												// additionne les carrés
					.reduce(0, (u, d) -> u + d.intValue(), (u1, u2) -> u1 + u2));
		try {
			// getting the result and synchronising with its availability
			System.out.println("first parallel map/reduce gives: " + fjt.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		// Fresh parallel stream creation
		Stream<Integer> parIntStream1 = Stream.of(intArray).parallel();
		// same map/reduce computation but using verbose functions to see what
		// threads are used to execute the functions.
		fjt = fjp.submit(() ->
				parIntStream1
				  .filter(TestStreams::verboseSelector)
				  .map(TestStreams::verboseProcessor)
				  .reduce(0,
						  TestStreams::verboseReductor,
						  TestStreams::verboseCombinator));
		try {
			// récupération du résultat avec synchronisation
			System.out.println("verbose parallel map/reduce gives: " + fjt.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}

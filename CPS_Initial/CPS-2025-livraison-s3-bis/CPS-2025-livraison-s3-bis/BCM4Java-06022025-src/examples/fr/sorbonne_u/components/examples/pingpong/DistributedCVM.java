package fr.sorbonne_u.components.examples.pingpong;

//Copyright Jacques Malenfant, Sorbonne Universite.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.examples.pingpong.components.PingPongPlayer;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

//-----------------------------------------------------------------------------
/**
 * The class <code>DistributedCVM</code> deploys and run the ping pong example
 * on two JVM.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The configuration of the distributed deployment uses two JVM, one for
 * each player. The constants <code>PLAYER1_JVM_URI</code> and
 * <code>PLAYER2_JVM_URI</code> gives the URI of the two JVM that must
 * be used in the configuration file. A typical configuration file
 * for a single host deployment would be:
 * </p>
 * 
 * <pre>
 * &lt;deployment&gt;
 *   &lt;cyclicBarrier   hostname="localhost" port="55253"/&gt;
 *   &lt;globalRegistry  hostname="localhost" port="55252"/&gt;
 *   &lt;rmiRegistryPort no="55999"/&gt;
 *   &lt;jvms2hostnames&gt;
 *     &lt;jvm2hostname jvmuri="player1jvm" rmiRegistryCreator="true"
 *                   hostname="localhost"/&gt;
 *     &lt;jvm2hostname jvmuri="player2jvm" rmiRegistryCreator="false"
 *                   hostname="localhost"/&gt;
 *   &lt;/jvms2hostnames&gt;
 * &lt;/deployment&gt;
 * </pre>
 * 
 * <p>
 * Starting the application must be done as in the basic client/server
 * example but with the following commands to start each player:
 * </p>
 * 
 * <pre>
 * java -ea -cp 'jars/*' -Djava.security.manager \
 * 			             -Djava.security.policy=dcvm.policy \
 *   fr.sorbonne_u.components.examples.pingpong.DistributedCVM player1jvm config.xml
 * 
 * java -ea -cp 'jars/*' -Djava.security.manager \
 * 			             -Djava.security.policy=dcvm.policy \
 *   fr.sorbonne_u.components.examples.pingpong.DistributedCVM player2jvm config.xml
 * </pre>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-03-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				DistributedCVM
extends		AbstractDistributedCVM
{
	/** URI of the player 1 JVM.											*/
	public final static String	PLAYER1_JVM_URI = "player1jvm";
	/** URI of the player 2 JVM.											*/
	public final static String	PLAYER2_JVM_URI = "player2jvm";
	/** URI of the two way port of the first player.						*/
	public final static String	PING_PONG_URI_1 = "player1";
	/** URI of the two way port of the second player.						*/
	public final static String	PING_PONG_URI_2 = "player2";
	/** URI of the inbound port of the first player.						*/
	public final static String	PLAYER1_PING_PONG_INBOUND_PORT_URI =
														"player1ibpURI";
	/** URI of the inbound port of the second player.						*/
	public final static String	PLAYER2_PING_PONG_INBOUND_PORT_URI =
														"player2ibpURI";
	/** URI of the outbound port of the first player.						*/
	public final static String	PLAYER1_PING_PONG_DATA_OUTBOUND_PORT_URI =
														"player1dobpURI";
	/** URI of the inbound port of the first player.						*/
	public final static String	PLAYER1_PING_PONG_DATA_INBOUND_PORT_URI =
														"player1dibpURI";
	/** URI of the outbound port of the second player.						*/
	public final static String	PLAYER2_PING_PONG_DATA_OUTBOUND_PORT_URI =
														"player2dobpURI";
	/** URI of the inbound port of the second player.						*/
	public final static String	PLAYER2_PING_PONG_DATA_INBOUND_PORT_URI =
														"player2dibpURI";
	/** URI of the two way port of the first player.						*/
	public final static String	PLAYER1_PING_PONG_TWOWAY_PORT_URI =
														"player1twpURI";
	/** URI of the two way port of the second player.						*/
	public final static String	PLAYER2_PING_PONG_TWOWAY_PORT_URI =
														"player2twpURI";

	public				DistributedCVM(String[] args) throws Exception
	{
		super(args);
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#instantiateAndPublish()
	 */
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		// ---------------------------------------------------------------------
		// Configuration phase
		// ---------------------------------------------------------------------

		// debugging mode configuration; comment and uncomment the line to see
		// the difference
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);

		// ---------------------------------------------------------------------
		// Creation phase
		// ---------------------------------------------------------------------

		if (thisJVMURI.equals(PLAYER1_JVM_URI)) {
			// A first player that initially has the service.
			String pp1URI =
				AbstractComponent.createComponent(
						PingPongPlayer.class.getCanonicalName(),
						new Object[]{PING_PONG_URI_1,
									 true,
									 PLAYER1_PING_PONG_INBOUND_PORT_URI,
									 PLAYER2_PING_PONG_INBOUND_PORT_URI,
									 PLAYER1_PING_PONG_DATA_OUTBOUND_PORT_URI,
									 PLAYER1_PING_PONG_DATA_INBOUND_PORT_URI,
									 PLAYER2_PING_PONG_DATA_OUTBOUND_PORT_URI,
									 PLAYER2_PING_PONG_DATA_INBOUND_PORT_URI,
									 PLAYER1_PING_PONG_TWOWAY_PORT_URI,
									 PLAYER2_PING_PONG_TWOWAY_PORT_URI});
				this.toggleTracing(pp1URI);
		} else if (thisJVMURI.equals(PLAYER2_JVM_URI)) {
			// A second player that is initially passive.
			String pp2URI =
				AbstractComponent.createComponent(
						PingPongPlayer.class.getCanonicalName(),
						new Object[]{PING_PONG_URI_2,
									 false,
									 PLAYER1_PING_PONG_INBOUND_PORT_URI,
									 PLAYER2_PING_PONG_INBOUND_PORT_URI,
									 PLAYER1_PING_PONG_DATA_OUTBOUND_PORT_URI,
									 PLAYER1_PING_PONG_DATA_INBOUND_PORT_URI,
									 PLAYER2_PING_PONG_DATA_OUTBOUND_PORT_URI,
									 PLAYER2_PING_PONG_DATA_INBOUND_PORT_URI,
									 PLAYER2_PING_PONG_TWOWAY_PORT_URI,
									 PLAYER1_PING_PONG_TWOWAY_PORT_URI});
				this.toggleTracing(pp2URI);
		} else {
			throw new Exception("Unknown JVM URI: " + thisJVMURI);
		}
		super.instantiateAndPublish();
	}

	public static void		main(String[] args)
	{
		try {
			DistributedCVM cvm = new DistributedCVM(args);
			cvm.startStandardLifeCycle(60000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
//-----------------------------------------------------------------------------

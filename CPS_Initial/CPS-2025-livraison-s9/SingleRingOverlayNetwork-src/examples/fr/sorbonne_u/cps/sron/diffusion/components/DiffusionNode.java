package fr.sorbonne_u.cps.sron.diffusion.components;

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

//------------------------------------------------------------------------------
/**
 * The class <code>DiffusionNode</code> implements a node that is put in a
 * ring overlay network to show the use of broadcasting and point-to-point
 * communication in such overlay networks.
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
 * <p>Created on : 2019-04-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			DiffusionNode
extends		AbstractComponent
{
	protected static final int		NB_THREADS = 1;
	protected DiffusionNodePlugin	plugin;

	protected			DiffusionNode() throws Exception
	{
		super(1, 0);
		this.init();
	}

	protected			DiffusionNode(String reflectionInboundPortURI)
	throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.init();
	}

	protected void		init() throws Exception
	{
		// create and install the plug-in
		DiffusionNodePlugin plugin = new DiffusionNodePlugin(NB_THREADS);
		String pluginURI = "DiffusionNode-" + plugin.getNodeNumber();
		plugin.setPluginURI(pluginURI);
		this.installPlugin(plugin);
		// initialise the component tracer window.
		this.getTracer().setTitle(pluginURI);
		this.getTracer().setRelativePosition(1, plugin.getNodeNumber());
		this.toggleTracing();
	}
}
//------------------------------------------------------------------------------

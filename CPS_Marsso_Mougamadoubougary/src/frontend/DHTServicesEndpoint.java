package frontend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class DHTServicesEndpoint extends BCMEndPoint<DHTServicesCI> {

    private static final long serialVersionUID = 1L;

    public DHTServicesEndpoint() {
        super(DHTServicesCI.class, DHTServicesCI.class);
    }

    @Override
    protected AbstractInboundPort makeInboundPort(AbstractComponent component, String inboundPortURI) throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert inboundPortURI != null && !inboundPortURI.isEmpty() : new PreconditionException("inboundPortURI != null && !inboundPortURI.isEmpty()");
        assert this.inboundPortURI.equals(inboundPortURI) : new PreconditionException("inboundPortURI != this.inboundPortURI");

        DHTInboundPort port = new DHTInboundPort(inboundPortURI, component);
        port.publishPort();

        assert port != null && port.isPublished() : new PostconditionException("port != null && port.isPublished()");
        assert ((AbstractPort) port).getPortURI().equals(inboundPortURI) : new PostconditionException("port URI mismatch");
        assert getServerSideInterface().isAssignableFrom(port.getClass()) : new PostconditionException("incompatible server interface");

        assert DHTServicesEndpoint.implementationInvariants(this) : new ImplementationInvariantException("implementationInvariants(this)");
        assert DHTServicesEndpoint.invariants(this) : new InvariantException("invariants(this)");

        return port;
    }

    @Override
    protected DHTServicesCI makeOutboundPort(AbstractComponent component, String inboundPortURI) throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert this.inboundPortURI.equals(inboundPortURI) : new PreconditionException("inboundPortURI != this.inboundPortURI");

        DHTOutboundPort port = new DHTOutboundPort(component);
        port.publishPort();
        component.doPortConnection(port.getPortURI(), inboundPortURI, DHTConnector.class.getCanonicalName());

        assert port != null && port.isPublished() && port.connected() : new PostconditionException("port published and connected");
        assert ((AbstractPort) port).getServerPortURI().equals(getInboundPortURI()) : new PostconditionException("server port URI mismatch");
        assert getClientSideInterface().isAssignableFrom(port.getClass()) : new PostconditionException("incompatible client interface");

        assert implementationInvariants(this) : new ImplementationInvariantException("implementationInvariants(this)");
        assert invariants(this) : new InvariantException("invariants(this)");

        return port;
    }
}

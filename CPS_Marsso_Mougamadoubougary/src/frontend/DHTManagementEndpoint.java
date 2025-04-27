package frontend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class DHTManagementEndpoint
extends BCMEndPoint<DHTManagementCI> {

    private static final long serialVersionUID = 1L;
    private int executorIndex;

    // Constructeur sans URI initial
    public DHTManagementEndpoint() {
        super(DHTManagementCI.class, DHTManagementCI.class);
    }

    // Constructeur avec URI et index d'exécuteur de service
    public DHTManagementEndpoint(String inboundPortURI, int executorServiceIndex) {
        super(DHTManagementCI.class, DHTManagementCI.class, inboundPortURI);
        this.executorIndex = executorServiceIndex;
    }

    /**
     * Crée le port entrant (inbound) du composant.
     */
    @Override
    protected AbstractInboundPort makeInboundPort(AbstractComponent component, String inboundPortURI) throws Exception {
        // Preconditions
        assert component != null : new PreconditionException("component != null");
        assert inboundPortURI != null && !inboundPortURI.isEmpty() :
            new PreconditionException("inboundPortURI != null && !inboundPortURI.isEmpty()");
        assert this.inboundPortURI.equals(inboundPortURI) :
            new PreconditionException("inboundPortURI must match this.inboundPortURI");

        DHTManagementInboundPort inboundPort = new DHTManagementInboundPort(inboundPortURI, this.executorIndex, component);
        inboundPort.publishPort();

        // Postconditions
        assert inboundPort != null && inboundPort.isPublished() :
            new PostconditionException("Inbound port must be created and published");
        assert ((AbstractPort) inboundPort).getPortURI().equals(inboundPortURI) :
            new PostconditionException("Inbound port URI mismatch");
        assert getServerSideInterface().isAssignableFrom(inboundPort.getClass()) :
            new PostconditionException("Inbound port does not implement the required server interface");

        // Invariant checking
        assert DHTManagementEndpoint.implementationInvariants(this) :
            new ImplementationInvariantException("DHTManagementEndpoint implementation invariant failed");
        assert DHTManagementEndpoint.invariants(this) :
            new InvariantException("DHTManagementEndpoint invariants failed");

        return inboundPort;
    }

    /**
     * Crée le port sortant (outbound) du composant.
     */
    @Override
    protected DHTManagementCI makeOutboundPort(AbstractComponent component, String inboundPortURI) throws Exception {
        // Preconditions
        assert component != null : new PreconditionException("component != null");
        assert this.inboundPortURI.equals(inboundPortURI) :
            new PreconditionException("inboundPortURI must match this.inboundPortURI");

        DHTManagementOutboundPort outboundPort = new DHTManagementOutboundPort(component);
        outboundPort.publishPort();

        component.doPortConnection(
            outboundPort.getPortURI(),
            inboundPortURI,
            DHTManagementConnector.class.getCanonicalName()
        );

        // Postconditions
        assert outboundPort != null && outboundPort.isPublished() && outboundPort.connected() :
            new PostconditionException("Outbound port must be published and connected");
        assert ((AbstractPort) outboundPort).getServerPortURI().equals(getInboundPortURI()) :
            new PostconditionException("Outbound port's server URI must match inbound URI");
        assert getClientSideInterface().isAssignableFrom(outboundPort.getClass()) :
            new PostconditionException("Outbound port does not implement the required client interface");

        // Invariant checking
        assert implementationInvariants(this) :
            new ImplementationInvariantException("DHTManagementEndpoint implementation invariant failed");
        assert invariants(this) :
            new InvariantException("DHTManagementEndpoint invariants failed");

        return outboundPort;
    }

    /**
     * Modifie l'index du service d'exécution.
     *
     * @param executorServiceIndex Nouvel index
     */
    public void setExecutorServiceIndex(int executorServiceIndex) {
        this.executorIndex = executorServiceIndex;
    }
}

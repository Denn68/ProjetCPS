package backend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class ResultReceptionEndpoint extends BCMEndPoint<ResultReceptionCI> {

    private static final long serialVersionUID = 1L;
    private int executorServiceIndex;

    // Constructeur par défaut
    public ResultReceptionEndpoint() {
        super(ResultReceptionCI.class, ResultReceptionCI.class);
    }

    // Création de l'inbound port
    @Override
    protected AbstractInboundPort makeInboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        // Vérifications des préconditions
        assert component != null : new PreconditionException("component != null");
        assert inboundPortUri != null && !inboundPortUri.isEmpty() :
                new PreconditionException("inboundPortUri != null && !inboundPortUri.isEmpty()");
        assert this.inboundPortURI.equals(inboundPortUri) :
                new PreconditionException("inboundPortUri différent de this.inboundPortURI");

        ResultReceptionInboundPort port = new ResultReceptionInboundPort(inboundPortUri, this.executorServiceIndex, component);
        port.publishPort();

        // Vérifications des postconditions
        assert port != null && port.isPublished() :
                new PostconditionException("port != null && port.isPublished()");
        assert ((AbstractPort) port).getPortURI().equals(inboundPortUri) :
                new PostconditionException("port.getPortURI() incorrect");
        assert getServerSideInterface().isAssignableFrom(port.getClass()) :
                new PostconditionException("Interface serveur incorrecte");
        assert ResultReceptionEndpoint.implementationInvariants(this) :
                new ImplementationInvariantException("implementationInvariants non respectés");
        assert ResultReceptionEndpoint.invariants(this) :
                new InvariantException("invariants non respectés");

        return port;
    }

    // Création de l'outbound port
    @Override
    protected ResultReceptionCI makeOutboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        // Vérifications des préconditions
        assert component != null : new PreconditionException("component != null");
        assert this.inboundPortURI.equals(inboundPortUri) :
                new PreconditionException("Different InboundPortURI");

        ResultReceptionOutboundPort port = new ResultReceptionOutboundPort(component);
        port.publishPort();
        component.doPortConnection(
                port.getPortURI(),
                inboundPortUri,
                ResultReceptionConnector.class.getCanonicalName()
        );

        // Vérifications des postconditions
        assert port != null && port.isPublished() && port.connected() :
                new PostconditionException("port != null && port.isPublished() && port.connected()");
        assert ((AbstractPort) port).getServerPortURI().equals(getInboundPortURI()) :
                new PostconditionException("port.getServerPortURI() != getInboundPortURI()");
        assert getClientSideInterface().isAssignableFrom(port.getClass()) :
                new PostconditionException("Type outbound incorrect");
        assert implementationInvariants(this) :
                new ImplementationInvariantException("implementationInvariants non respectés");
        assert invariants(this) :
                new InvariantException("invariants non respectés");

        return port;
    }

    // Setter pour l'index du service d'exécution
    public void setExecutorServiceIndex(int executorServiceIndex) {
        this.executorServiceIndex = executorServiceIndex;
    }
}

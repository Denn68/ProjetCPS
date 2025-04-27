package backend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class MapReduceResultReceptionEndpoint extends BCMEndPoint<MapReduceResultReceptionCI> {

    private static final long serialVersionUID = 1L;
    private int executorServiceIndex;

    // Constructeur par défaut pour MapReduceResultReception
    public MapReduceResultReceptionEndpoint() {
        super(MapReduceResultReceptionCI.class, MapReduceResultReceptionCI.class);
    }

    // Crée et publie l'inbound port pour la réception de résultats MapReduce
    @Override
    protected AbstractInboundPort makeInboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert inboundPortUri != null && !inboundPortUri.isEmpty() :
                new PreconditionException("inboundPortUri != null && !inboundPortUri.isEmpty()");

        MapReduceResultReceptionInboundPort port =
                new MapReduceResultReceptionInboundPort(inboundPortUri, this.executorServiceIndex, component);
        port.publishPort();

        assert port != null && port.isPublished() :
                new PostconditionException("port != null && port.isPublished()");
        assert ((AbstractPort) port).getPortURI().equals(inboundPortUri) :
                new PostconditionException("port URI mismatch after creation");
        assert getServerSideInterface().isAssignableFrom(port.getClass()) :
                new PostconditionException("port class not assignable to server-side interface");
        assert MapReduceResultReceptionEndpoint.implementationInvariants(this) :
                new ImplementationInvariantException("implementationInvariants(this)");
        assert MapReduceResultReceptionEndpoint.invariants(this) :
                new InvariantException("invariants(this)");

        return port;
    }

    // Crée et connecte l'outbound port pour la réception de résultats MapReduce
    @Override
    protected MapReduceResultReceptionCI makeOutboundPort(AbstractComponent component, String inboundPortUri)
            throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert this.inboundPortURI.equals(inboundPortUri) : new PreconditionException("Different inboundPortUri");

        MapReduceResultReceptionOutboundPort port = new MapReduceResultReceptionOutboundPort(component);
        port.publishPort();
        component.doPortConnection(
                port.getPortURI(),
                inboundPortUri,
                MapReduceResultReceptionConnector.class.getCanonicalName()
        );

        assert port != null && port.isPublished() && port.connected() :
                new PostconditionException("port != null && port.isPublished() && port.connected()");
        assert ((AbstractPort) port).getServerPortURI().equals(getInboundPortURI()) :
                new PostconditionException("server port URI mismatch after connection");
        assert getClientSideInterface().isAssignableFrom(port.getClass()) :
                new PostconditionException("port class not assignable to client-side interface");
        assert implementationInvariants(this) :
                new ImplementationInvariantException("implementationInvariants(this)");
        assert invariants(this) :
                new InvariantException("invariants(this)");

        return port;
    }

    // Définit l'index du service d'exécution
    public void setExecutorServiceIndex(int executorServiceIndex) {
        this.executorServiceIndex = executorServiceIndex;
    }
}

package backend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class MapReduceEndpoint extends BCMEndPoint<MapReduceCI> {

    private static final long serialVersionUID = 1L;
    private int executorServiceIndex;

    // Constructeur du MapReduceEndpoint
    public MapReduceEndpoint() {
        super(MapReduceCI.class, MapReduceCI.class);
    }

    // Crée et publie l'inbound port pour MapReduce
    @Override
    protected AbstractInboundPort makeInboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert inboundPortUri != null && !inboundPortUri.isEmpty() :
                new PreconditionException("inboundPortUri != null && !inboundPortUri.isEmpty()");

        MapReduceInboundPort port = new MapReduceInboundPort(this.inboundPortURI, this.executorServiceIndex, component);
        port.publishPort();

        assert port != null && port.isPublished() :
                new PostconditionException("port != null && port.isPublished()");
        assert ((AbstractPort) port).getPortURI().equals(inboundPortUri) :
                new PostconditionException("port URI mismatch after creation");
        assert getServerSideInterface().isAssignableFrom(port.getClass()) :
                new PostconditionException("port class not assignable to server-side interface");
        assert MapReduceEndpoint.implementationInvariants(this) :
                new ImplementationInvariantException("MapReduceEndpoint.implementationInvariants(this)");
        assert MapReduceEndpoint.invariants(this) :
                new InvariantException("MapReduceEndpoint.invariants(this)");

        return port;
    }

    // Crée et connecte l'outbound port pour MapReduce
    @Override
    protected MapReduceCI makeOutboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        assert component != null : new PreconditionException("component != null");

        MapReduceOutboundPort port = new MapReduceOutboundPort(component);
        port.publishPort();
        component.doPortConnection(
            port.getPortURI(),
            this.inboundPortURI,
            MapReduceConnector.class.getCanonicalName()
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

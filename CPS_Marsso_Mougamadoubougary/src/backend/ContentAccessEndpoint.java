package backend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class ContentAccessEndpoint extends BCMEndPoint<ContentAccessCI> {

    private static final long serialVersionUID = 1L;
    private int executorServiceIndex;

    // Constructeur du ContentAccessEndpoint
    public ContentAccessEndpoint() {
        super(ContentAccessCI.class, ContentAccessCI.class);
    }

    // Crée et publie l'inbound port pour le contenu
    @Override
    protected AbstractInboundPort makeInboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert inboundPortUri != null && !inboundPortUri.isEmpty() :
                new PreconditionException("inboundPortUri != null && !inboundPortUri.isEmpty()");

        ContentAccessInboundPort port = new ContentAccessInboundPort(inboundPortUri, this.executorServiceIndex, component);
        port.publishPort();

        assert port != null && port.isPublished() :
                new PostconditionException("port != null && port.isPublished()");
        assert ((AbstractPort) port).getPortURI().equals(inboundPortUri) :
                new PostconditionException("((AbstractPort) port).getPortURI().equals(inboundPortUri)");
        assert getServerSideInterface().isAssignableFrom(port.getClass()) :
                new PostconditionException("getServerSideInterface().isAssignableFrom(port.getClass())");
        assert ContentAccessEndpoint.implementationInvariants(this) :
                new ImplementationInvariantException("ContentAccessEndpoint.implementationInvariants(this)");
        assert ContentAccessEndpoint.invariants(this) :
                new InvariantException("ContentAccessEndpoint.invariants(this)");

        return port;
    }

    // Crée et connecte l'outbound port au serveur
    @Override
    protected ContentAccessCI makeOutboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert this.inboundPortURI.equals(inboundPortUri) : new PreconditionException("Different inboundPortUri");

        ContentAccessOutboundPort port = new ContentAccessOutboundPort(component);
        port.publishPort();
        component.doPortConnection(
            port.getPortURI(),
            inboundPortUri,
            ContentAccessConnector.class.getCanonicalName()
        );

        assert port != null && port.isPublished() && port.connected() :
                new PostconditionException("port != null && port.isPublished() && port.connected()");
        assert ((AbstractPort) port).getServerPortURI().equals(getInboundPortURI()) :
                new PostconditionException("((AbstractPort) port).getServerPortURI().equals(getInboundPortURI())");
        assert getClientSideInterface().isAssignableFrom(port.getClass()) :
                new PostconditionException("getClientSideInterface().isAssignableFrom(port.getClass())");
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

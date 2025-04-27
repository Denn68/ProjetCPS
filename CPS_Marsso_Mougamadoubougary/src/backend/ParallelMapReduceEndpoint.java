package backend;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.endpoints.BCMEndPoint;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class ParallelMapReduceEndpoint extends BCMEndPoint<ParallelMapReduceCI> {

    private static final long serialVersionUID = 1L;
    private int executorIndex;

    // Constructeur avec inboundPortURI et index de service d'exécution
    public ParallelMapReduceEndpoint(String inboundPortUri, int executorServiceIndex) {
        super(ParallelMapReduceCI.class, ParallelMapReduceCI.class, inboundPortUri);
        this.executorIndex = executorServiceIndex;
    }

    // Constructeur par défaut
    public ParallelMapReduceEndpoint() {
        super(ParallelMapReduceCI.class, ParallelMapReduceCI.class);
    }

    // Crée et publie l'inbound port pour le traitement parallèle
    @Override
    protected AbstractInboundPort makeInboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert inboundPortUri != null && !inboundPortUri.isEmpty() : new PreconditionException("inboundPortUri non valide");
        assert this.inboundPortURI.equals(inboundPortUri) : new PreconditionException("Different InboundPortURI");

        ParallelMapReduceInboundPort port = new ParallelMapReduceInboundPort(inboundPortUri, this.executorIndex, component);
        port.publishPort();

        assert port != null && port.isPublished() : new PostconditionException("port non publié après création");
        assert ((AbstractPort) port).getPortURI().equals(inboundPortUri) : new PostconditionException("URI inbound port incorrect");
        assert getServerSideInterface().isAssignableFrom(port.getClass()) : new PostconditionException("Type du port incorrect");
        assert ParallelMapReduceEndpoint.implementationInvariants(this) : new ImplementationInvariantException("invariants non respectés");
        assert ParallelMapReduceEndpoint.invariants(this) : new InvariantException("invariants non respectés");

        return port;
    }

    // Crée et connecte l'outbound port pour le traitement parallèle
    @Override
    protected ParallelMapReduceCI makeOutboundPort(AbstractComponent component, String inboundPortUri) throws Exception {
        assert component != null : new PreconditionException("component != null");
        assert this.inboundPortURI.equals(inboundPortUri) : new PreconditionException("Different InboundPortURI");

        ParallelMapReduceOutboundPort port = new ParallelMapReduceOutboundPort(component);
        port.publishPort();
        component.doPortConnection(
            port.getPortURI(),
            inboundPortUri,
            ParallelMapReduceConnector.class.getCanonicalName()
        );

        assert port != null && port.isPublished() && port.connected() :
            new PostconditionException("port non publié ou connecté après création");
        assert ((AbstractPort) port).getServerPortURI().equals(getInboundPortURI()) :
            new PostconditionException("ServerPortURI incorrect après connexion");
        assert getClientSideInterface().isAssignableFrom(port.getClass()) :
            new PostconditionException("Type du port outbound incorrect");
        assert implementationInvariants(this) : new ImplementationInvariantException("invariants non respectés");
        assert invariants(this) : new InvariantException("invariants non respectés");

        return port;
    }

    // Définit l'index du service d'exécution
    public void setExecutorServiceIndex(int executorServiceIndex) {
        this.executorIndex = executorServiceIndex;
    }
}

package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionI;

public class ResultReceptionInboundPort extends AbstractInboundPort implements ResultReceptionCI {

    private static final long serialVersionUID = 1L;
    protected final int executorIndex;

    // Constructeur sans URI
    public ResultReceptionInboundPort(int executorIndex, ComponentI owner) throws Exception {
        super(ResultReceptionCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex) : "Invalid executor index";
        this.executorIndex = executorIndex;
    }

    // Constructeur avec URI
    public ResultReceptionInboundPort(String uri, int executorIndex, ComponentI owner) throws Exception {
        super(uri, ResultReceptionCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex) : "Invalid executor index";
        this.executorIndex = executorIndex;
    }

    // Accepte un résultat et l'exécute dans un thread du pool spécifié
    @Override
    public void acceptResult(String computationUri, Serializable result) throws Exception {
        this.getOwner().runTask(executorIndex, owner -> {
            try {
                ((ResultReceptionI) owner).acceptResult(computationUri, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

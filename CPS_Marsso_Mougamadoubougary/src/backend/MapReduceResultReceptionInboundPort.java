package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionI;

public class MapReduceResultReceptionInboundPort extends AbstractInboundPort implements MapReduceResultReceptionCI {

    private static final long serialVersionUID = 1L;
    protected final int executorIndex;

    // Constructeur sans URI
    public MapReduceResultReceptionInboundPort(int executorIndex, ComponentI owner) throws Exception {
        super(MapReduceResultReceptionCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex);
        this.executorIndex = executorIndex;
    }

    // Constructeur avec URI
    public MapReduceResultReceptionInboundPort(String uri, int executorIndex, ComponentI owner) throws Exception {
        super(uri, MapReduceResultReceptionCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex);
        this.executorIndex = executorIndex;
    }

    // Accepte un résultat MapReduce et l'exécute dans un thread spécifique
    @Override
    public void acceptResult(String computationUri, String emitterId, Serializable acc) throws Exception {
        this.getOwner().runTask(
            executorIndex,
            owner -> {
                try {
                    ((MapReduceResultReceptionI) owner).acceptResult(computationUri, emitterId, acc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }
}

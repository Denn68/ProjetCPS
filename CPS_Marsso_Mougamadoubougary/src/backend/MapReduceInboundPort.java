package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class MapReduceInboundPort extends AbstractInboundPort implements MapReduceCI {

    private static final long serialVersionUID = 1L;
    protected final int executorIndex;

    // Constructeur sans URI
    public MapReduceInboundPort(int executorIndex, ComponentI owner) throws Exception {
        super(MapReduceCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex);
        this.executorIndex = executorIndex;
    }

    // Constructeur avec URI
    public MapReduceInboundPort(String uri, int executorIndex, ComponentI owner) throws Exception {
        super(uri, MapReduceCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex);
        this.executorIndex = executorIndex;
    }

    // Lance une opération map de manière synchrone
    @Override
    public <R extends Serializable> void mapSync(String computationUri, SelectorI selector, ProcessorI<R> processor)
            throws Exception {
        this.getOwner().handleRequest(
            owner -> {
                ((MapReduceI) owner).mapSync(computationUri, selector, processor);
                return null;
            }
        );
    }

    // Lance une opération reduce de manière synchrone
    @Override
    public <A extends Serializable, R> A reduceSync(String computationUri, ReductorI<A, R> reductor,
                                                    CombinatorI<A> combinator, A currentAcc) throws Exception {
        return this.getOwner().handleRequest(
            owner -> ((MapReduceI) owner).reduceSync(computationUri, reductor, combinator, currentAcc)
        );
    }

    // Nettoie les données d'une computation MapReduce
    @Override
    public void clearMapReduceComputation(String computationUri) throws Exception {
        this.getOwner().handleRequest(
            owner -> {
                ((MapReduceI) owner).clearMapReduceComputation(computationUri);
                return null;
            }
        );
    }

    // Lance une opération map de manière asynchrone
    @Override
    public <R extends Serializable> void map(String computationUri, SelectorI selector, ProcessorI<R> processor)
            throws Exception {
        this.getOwner().runTask(
            executorIndex,
            owner -> {
                try {
                    ((MapReduceI) owner).map(computationUri, selector, processor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }

    // Lance une opération reduce de manière asynchrone
    @Override
    public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void reduce(String computationUri,
            ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc, EndPointI<I> callerNode)
            throws Exception {
        this.getOwner().runTask(
            executorIndex,
            owner -> {
                try {
                    ((MapReduceI) owner).reduce(computationUri, reductor, combinator, identityAcc, currentAcc, callerNode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }
}

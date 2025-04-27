package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class ParallelMapReduceInboundPort extends AbstractInboundPort implements ParallelMapReduceCI {

    private static final long serialVersionUID = 1L;
    protected final int executorIndex;

    // Constructeur sans URI
    public ParallelMapReduceInboundPort(int executorIndex, ComponentI owner) throws Exception {
        super(ParallelMapReduceCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex);
        this.executorIndex = executorIndex;
    }

    // Constructeur avec URI
    public ParallelMapReduceInboundPort(String uri, int executorIndex, ComponentI owner) throws Exception {
        super(uri, ParallelMapReduceCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex);
        this.executorIndex = executorIndex;
    }

    // Lance un map synchronisé
    @Override
    public <R extends Serializable> void mapSync(String computationUri, SelectorI selector, ProcessorI<R> processor)
            throws Exception {
        this.getOwner().handleRequest(owner -> {
            ((MapReduceI) owner).mapSync(computationUri, selector, processor);
            return null;
        });
    }

    // Lance un reduce synchronisé
    @Override
    public <A extends Serializable, R> A reduceSync(String computationUri, ReductorI<A, R> reductor,
            CombinatorI<A> combinator, A currentAcc) throws Exception {
        return this.getOwner().handleRequest(owner ->
            ((MapReduceI) owner).reduceSync(computationUri, reductor, combinator, currentAcc)
        );
    }

    // Vide les résultats d'une computation
    @Override
    public void clearMapReduceComputation(String computationUri) throws Exception {
        this.getOwner().handleRequest(owner -> {
            ((MapReduceI) owner).clearMapReduceComputation(computationUri);
            return null;
        });
    }

    // Lance un map asynchrone
    @Override
    public <R extends Serializable> void map(String computationUri, SelectorI selector, ProcessorI<R> processor)
            throws Exception {
        this.getOwner().runTask(executorIndex, owner -> {
            try {
                ((MapReduceI) owner).map(computationUri, selector, processor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Lance un reduce asynchrone
    @Override
    public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void reduce(String computationUri,
            ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc, EndPointI<I> caller)
            throws Exception {
        this.getOwner().runTask(executorIndex, owner -> {
            try {
                ((MapReduceI) owner).reduce(computationUri, reductor, combinator, identityAcc, currentAcc, caller);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Lance un map parallèle
    @Override
    public <R extends Serializable> void parallelMap(String computationUri, SelectorI selector, ProcessorI<R> processor,
            ParallelismPolicyI parallelismPolicy) throws Exception {
        this.getOwner().handleRequest(executorIndex, owner -> {
            ((ParallelMapReduceI) owner).parallelMap(computationUri, selector, processor, parallelismPolicy);
            return null;
        });
    }

    // Lance un reduce parallèle
    @Override
    public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void parallelReduce(
            String computationUri, ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc,
            ParallelismPolicyI parallelismPolicy, EndPointI<I> caller) throws Exception {
        this.getOwner().handleRequest(executorIndex, owner -> {
            ((ParallelMapReduceI) owner).parallelReduce(computationUri, reductor, combinator, identityAcc, currentAcc, parallelismPolicy, caller);
            return null;
        });
    }
}

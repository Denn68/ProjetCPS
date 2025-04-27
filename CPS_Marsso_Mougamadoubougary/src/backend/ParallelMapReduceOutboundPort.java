package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class ParallelMapReduceOutboundPort extends AbstractOutboundPort implements ParallelMapReduceCI {

    private static final long serialVersionUID = 1L;

    // Constructeur standard sans URI
    public ParallelMapReduceOutboundPort(ComponentI owner) throws Exception {
        super(ParallelMapReduceCI.class, owner);
    }

    // Constructeur avec URI
    public ParallelMapReduceOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, ParallelMapReduceCI.class, owner);
        assert uri != null && owner != null;
    }

    // Constructeur avancé avec interface spécifique
    protected ParallelMapReduceOutboundPort(Class<? extends RequiredCI> implementedInterface, ComponentI owner)
            throws Exception {
        super(implementedInterface, owner);
    }

    // Map synchrone
    @Override
    public <R extends Serializable> void mapSync(String computationUri, SelectorI selector, ProcessorI<R> processor)
            throws Exception {
        ((MapReduceCI) this.getConnector()).mapSync(computationUri, selector, processor);
    }

    // Reduce synchrone
    @Override
    public <A extends Serializable, R> A reduceSync(String computationUri, ReductorI<A, R> reductor,
            CombinatorI<A> combinator, A currentAcc) throws Exception {
        return ((MapReduceCI) this.getConnector()).reduceSync(computationUri, reductor, combinator, currentAcc);
    }

    // Nettoyage d'une computation
    @Override
    public void clearMapReduceComputation(String computationUri) throws Exception {
        ((MapReduceCI) this.getConnector()).clearMapReduceComputation(computationUri);
    }

    // Map asynchrone
    @Override
    public <R extends Serializable> void map(String computationUri, SelectorI selector, ProcessorI<R> processor)
            throws Exception {
        ((MapReduceCI) this.getConnector()).map(computationUri, selector, processor);
    }

    // Reduce asynchrone
    @Override
    public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void reduce(
            String computationUri, ReductorI<A, R> reductor, CombinatorI<A> combinator,
            A identityAcc, A currentAcc, EndPointI<I> callerNode) throws Exception {
        ((MapReduceCI) this.getConnector()).reduce(computationUri, reductor, combinator, identityAcc, currentAcc, callerNode);
    }

    // Map parallèle
    @Override
    public <R extends Serializable> void parallelMap(String computationUri, SelectorI selector, ProcessorI<R> processor,
            ParallelismPolicyI parallelismPolicy) throws Exception {
        ((ParallelMapReduceCI) this.getConnector()).parallelMap(computationUri, selector, processor, parallelismPolicy);
    }

    // Reduce parallèle
    @Override
    public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void parallelReduce(
            String computationUri, ReductorI<A, R> reductor, CombinatorI<A> combinator,
            A identityAcc, A currentAcc, ParallelismPolicyI parallelismPolicy, EndPointI<I> caller) throws Exception {
        ((ParallelMapReduceCI) this.getConnector()).parallelReduce(computationUri, reductor, combinator, identityAcc, currentAcc, parallelismPolicy, caller);
    }
}

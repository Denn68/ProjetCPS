package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class ParallelMapReduceConnector extends MapReduceConnector implements ParallelMapReduceCI {

    // Lance une opération de map parallèle
    @Override
    public <R extends Serializable> void parallelMap(String computationUri, SelectorI selector, ProcessorI<R> processor,
                                                      ParallelismPolicyI parallelismPolicy) throws Exception {
        ((ParallelMapReduceCI) this.offering).parallelMap(computationUri, selector, processor, parallelismPolicy);
    }

    // Lance une opération de reduce parallèle
    @Override
    public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void parallelReduce(
            String computationUri, ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc,
            A currentAcc, ParallelismPolicyI parallelismPolicy, EndPointI<I> caller) throws Exception {
        ((ParallelMapReduceCI) this.offering).parallelReduce(
            computationUri, reductor, combinator, identityAcc, currentAcc, parallelismPolicy, caller
        );
    }
}

package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

public class MapReduceConnector extends AbstractConnector implements MapReduceCI {

    // Lance une opération map de manière synchrone
    @Override
    public <R extends Serializable> void mapSync(String computationUri, SelectorI selector, ProcessorI<R> processor)
            throws Exception {
        ((MapReduceCI) this.offering).mapSync(computationUri, selector, processor);
    }

    // Lance une opération reduce de manière synchrone
    @Override
    public <A extends Serializable, R> A reduceSync(String computationUri, ReductorI<A, R> reductor,
                                                    CombinatorI<A> combinator, A currentAcc) throws Exception {
        return ((MapReduceCI) this.offering).reduceSync(computationUri, reductor, combinator, currentAcc);
    }

    // Nettoie les données d'une computation MapReduce
    @Override
    public void clearMapReduceComputation(String computationUri) throws Exception {
        ((MapReduceCI) this.offering).clearMapReduceComputation(computationUri);
    }

    // Lance une opération map de manière asynchrone
    @Override
    public <R extends Serializable> void map(String computationUri, SelectorI selector, ProcessorI<R> processor)
            throws Exception {
        ((MapReduceCI) this.offering).map(computationUri, selector, processor);
    }

    // Lance une opération reduce de manière asynchrone
    @Override
    public <A extends Serializable, R, I extends MapReduceResultReceptionCI> void reduce(String computationUri,
            ReductorI<A, R> reductor, CombinatorI<A> combinator, A identityAcc, A currentAcc, EndPointI<I> callerNode)
            throws Exception {
        ((MapReduceCI) this.offering).reduce(computationUri, reductor, combinator, identityAcc, currentAcc, callerNode);
    }
}

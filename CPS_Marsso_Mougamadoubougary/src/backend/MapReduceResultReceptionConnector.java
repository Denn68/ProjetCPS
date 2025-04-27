package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceResultReceptionCI;

public class MapReduceResultReceptionConnector extends AbstractConnector implements MapReduceResultReceptionCI {

    // Accepte un résultat de MapReduce en provenance d'un nœud émetteur
    @Override
    public void acceptResult(String computationUri, String emitterId, Serializable acc) throws Exception {
        ((MapReduceResultReceptionCI) this.offering).acceptResult(computationUri, emitterId, acc);
    }
}

package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;

public class ResultReceptionConnector extends AbstractConnector implements ResultReceptionCI {

    // Accepte un résultat pour une computation donnée
    @Override
    public void acceptResult(String computationUri, Serializable result) throws Exception {
        ((ResultReceptionCI) this.offering).acceptResult(computationUri, result);
    }
}

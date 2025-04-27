package backend;

import java.io.Serializable;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;

public class ResultReceptionOutboundPort extends AbstractOutboundPort implements ResultReceptionCI {

    private static final long serialVersionUID = 1L;

    // Constructeur simple
    public ResultReceptionOutboundPort(ComponentI owner) throws Exception {
        super(ResultReceptionCI.class, owner);
    }

    // Transmet le résultat au composant connecté
    @Override
    public void acceptResult(String computationUri, Serializable result) throws Exception {
        ((ResultReceptionCI) this.getConnector()).acceptResult(computationUri, result);
    }
}

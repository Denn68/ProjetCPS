package backend;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;

public class ContentAccessConnector 
    extends AbstractConnector 
    implements ContentAccessCI {

    // Récupère un contenu de façon synchrone
    @Override
    public ContentDataI getSync(String computationUri, ContentKeyI key) throws Exception {
        return ((ContentAccessCI) this.offering).getSync(computationUri, key);
    }

    // Insère un contenu de façon synchrone
    @Override
    public ContentDataI putSync(String computationUri, ContentKeyI key, ContentDataI value) throws Exception {
        return ((ContentAccessCI) this.offering).putSync(computationUri, key, value);
    }

    // Supprime un contenu de façon synchrone
    @Override
    public ContentDataI removeSync(String computationUri, ContentKeyI key) throws Exception {
        return ((ContentAccessCI) this.offering).removeSync(computationUri, key);
    }

    // Efface les données liées à un calcul
    @Override
    public void clearComputation(String computationUri) throws Exception {
        ((ContentAccessCI) this.offering).clearComputation(computationUri);
    }

    // Récupère un contenu de façon asynchrone
    @Override
    public <I extends ResultReceptionCI> void get(String computationUri, ContentKeyI key, EndPointI<I> caller)
            throws Exception {
        ((ContentAccessCI) this.offering).get(computationUri, key, caller);
    }

    // Insère un contenu de façon asynchrone
    @Override
    public <I extends ResultReceptionCI> void put(String computationUri, ContentKeyI key, ContentDataI value,
            EndPointI<I> caller) throws Exception {
        ((ContentAccessCI) this.offering).put(computationUri, key, value, caller);
    }

    // Supprime un contenu de façon asynchrone
    @Override
    public <I extends ResultReceptionCI> void remove(String computationUri, ContentKeyI key, EndPointI<I> caller)
            throws Exception {
        ((ContentAccessCI) this.offering).remove(computationUri, key, caller);
    }
}

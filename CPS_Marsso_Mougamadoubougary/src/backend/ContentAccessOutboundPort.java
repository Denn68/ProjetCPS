package backend;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;

public class ContentAccessOutboundPort extends AbstractOutboundPort implements ContentAccessCI {

    private static final long serialVersionUID = 1L;

    // Constructeur du ContentAccessOutboundPort
    public ContentAccessOutboundPort(ComponentI owner) throws Exception {
        super(ContentAccessCI.class, owner);
    }

    // Récupère un contenu de façon synchrone via le connecteur
    @Override
    public ContentDataI getSync(String computationUri, ContentKeyI key) throws Exception {
        return ((ContentAccessCI) this.getConnector()).getSync(computationUri, key);
    }

    // Insère un contenu de façon synchrone via le connecteur
    @Override
    public ContentDataI putSync(String computationUri, ContentKeyI key, ContentDataI value) throws Exception {
        return ((ContentAccessCI) this.getConnector()).putSync(computationUri, key, value);
    }

    // Supprime un contenu de façon synchrone via le connecteur
    @Override
    public ContentDataI removeSync(String computationUri, ContentKeyI key) throws Exception {
        return ((ContentAccessCI) this.getConnector()).removeSync(computationUri, key);
    }

    // Efface toutes les données associées à un calcul via le connecteur
    @Override
    public void clearComputation(String computationUri) throws Exception {
        ((ContentAccessCI) this.getConnector()).clearComputation(computationUri);
    }

    // Récupère un contenu de façon asynchrone via le connecteur
    @Override
    public <I extends ResultReceptionCI> void get(String computationUri, ContentKeyI key, EndPointI<I> caller) throws Exception {
        ((ContentAccessCI) this.getConnector()).get(computationUri, key, caller);
    }

    // Insère un contenu de façon asynchrone via le connecteur
    @Override
    public <I extends ResultReceptionCI> void put(String computationUri, ContentKeyI key, ContentDataI value, EndPointI<I> caller) throws Exception {
        ((ContentAccessCI) this.getConnector()).put(computationUri, key, value, caller);
    }

    // Supprime un contenu de façon asynchrone via le connecteur
    @Override
    public <I extends ResultReceptionCI> void remove(String computationUri, ContentKeyI key, EndPointI<I> caller) throws Exception {
        ((ContentAccessCI) this.getConnector()).remove(computationUri, key, caller);
    }
}

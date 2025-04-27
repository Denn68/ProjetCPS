package backend;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ResultReceptionCI;

public class ContentAccessInboundPort extends AbstractInboundPort implements ContentAccessCI {

    private static final long serialVersionUID = 1L;
    protected final int executorIndex;

    // Constructeur sans URI
    public ContentAccessInboundPort(int executorIndex, ComponentI owner) throws Exception {
        super(ContentAccessCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex);
        this.executorIndex = executorIndex;
    }

    // Constructeur avec URI
    public ContentAccessInboundPort(String uri, int executorIndex, ComponentI owner) throws Exception {
        super(uri, ContentAccessCI.class, owner);
        assert owner.validExecutorServiceIndex(executorIndex);
        this.executorIndex = executorIndex;
    }

    // Récupère un contenu de façon synchrone
    @Override
    public ContentDataI getSync(String computationUri, ContentKeyI key) throws Exception {
        return this.getOwner().handleRequest(
            owner -> ((ContentAccessI) owner).getSync(computationUri, key)
        );
    }

    // Insère un contenu de façon synchrone
    @Override
    public ContentDataI putSync(String computationUri, ContentKeyI key, ContentDataI value) throws Exception {
        return this.getOwner().handleRequest(
            owner -> ((ContentAccessI) owner).putSync(computationUri, key, value)
        );
    }

    // Supprime un contenu de façon synchrone
    @Override
    public ContentDataI removeSync(String computationUri, ContentKeyI key) throws Exception {
        return this.getOwner().handleRequest(
            owner -> ((ContentAccessI) owner).removeSync(computationUri, key)
        );
    }

    // Efface toutes les données associées à un calcul
    @Override
    public void clearComputation(String computationUri) throws Exception {
        this.getOwner().handleRequest(
            owner -> { 
                ((ContentAccessI) owner).clearComputation(computationUri); 
                return null; 
            }
        );
    }

    // Récupère un contenu de façon asynchrone
    @Override
    public <I extends ResultReceptionCI> void get(String computationUri, ContentKeyI key, EndPointI<I> caller) throws Exception {
        this.getOwner().runTask(
            executorIndex,
            owner -> {
                try {
                    ((ContentAccessI) owner).get(computationUri, key, caller);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }

    // Insère un contenu de façon asynchrone
    @Override
    public <I extends ResultReceptionCI> void put(String computationUri, ContentKeyI key, ContentDataI value, EndPointI<I> caller) throws Exception {
        this.getOwner().runTask(
            executorIndex,
            owner -> {
                try {
                    ((ContentAccessI) owner).put(computationUri, key, value, caller);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }

    // Supprime un contenu de façon asynchrone
    @Override
    public <I extends ResultReceptionCI> void remove(String computationUri, ContentKeyI key, EndPointI<I> caller) throws Exception {
        this.getOwner().runTask(
            executorIndex,
            owner -> {
                try {
                    ((ContentAccessI) owner).remove(computationUri, key, caller);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }
}

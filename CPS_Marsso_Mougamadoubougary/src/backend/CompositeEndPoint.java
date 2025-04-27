package backend;

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.MapReduceCI;

public class CompositeEndPoint extends BCMCompositeEndPoint {

    private static final long serialVersionUID = 1L;

    // Constructeur initialisant les endpoints ContentAccess et MapReduce
    public CompositeEndPoint(int nbEndPoint) {
        super(nbEndPoint);
        ContentAccessEndpoint contentAccessEndpoint = new ContentAccessEndpoint();
        this.addEndPoint(contentAccessEndpoint);
        MapReduceEndpoint mapReduceEndpoint = new MapReduceEndpoint();
        this.addEndPoint(mapReduceEndpoint);
    }

    // Retourne l'endpoint pour ContentAccess
    public EndPointI<ContentAccessCI> getContentAccessEndpoint() {
        return this.getEndPoint(ContentAccessCI.class);
    }

    // Retourne l'endpoint pour MapReduce
    public EndPointI<MapReduceCI> getMapReduceEndpoint() {
        return this.getEndPoint(MapReduceCI.class);
    }

    // Définit l'executor service index pour ContentAccess
    public void setContentAccessExecutorServiceIndex(int contentAccessExecutorServiceIndex) {
        ((ContentAccessEndpoint) this.retrieveEndPoint(ContentAccessCI.class))
            .setExecutorServiceIndex(contentAccessExecutorServiceIndex);
    }

    // Définit l'executor service index pour MapReduce
    public void setMapReduceExecutorServiceIndex(int mapReduceExecutorServiceIndex) {
        ((MapReduceEndpoint) this.retrieveEndPoint(MapReduceCI.class))
            .setExecutorServiceIndex(mapReduceExecutorServiceIndex);
    }
}

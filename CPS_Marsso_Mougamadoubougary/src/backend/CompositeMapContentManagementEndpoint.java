package backend;

import fr.sorbonne_u.components.endpoints.BCMCompositeEndPoint;
import fr.sorbonne_u.components.endpoints.EndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentAccessCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.endpoints.ContentNodeCompositeEndPointI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.management.DHTManagementCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceCI;
import frontend.DHTManagementEndpoint;

public class CompositeMapContentManagementEndpoint
    extends BCMCompositeEndPoint
    implements ContentNodeCompositeEndPointI<ContentAccessCI, ParallelMapReduceCI, DHTManagementCI> {

    private static final long serialVersionUID = 1L;
    private ContentAccessEndpoint contentAccessEndpoint;
    private ParallelMapReduceEndpoint mapReduceEndpoint;
    private DHTManagementEndpoint dhtManagementEndpoint;

    // Constructeur initialisant les endpoints ContentAccess, MapReduce et DHTManagement
    public CompositeMapContentManagementEndpoint(int numberOfEndPoints) {
        super(numberOfEndPoints);
        this.contentAccessEndpoint = new ContentAccessEndpoint();
        this.addEndPoint(this.contentAccessEndpoint);
        this.mapReduceEndpoint = new ParallelMapReduceEndpoint();
        this.addEndPoint(this.mapReduceEndpoint);
        this.dhtManagementEndpoint = new DHTManagementEndpoint();
        this.addEndPoint(this.dhtManagementEndpoint);
    }

    // Retourne l'endpoint pour la gestion DHT
    @Override
    public EndPointI<DHTManagementCI> getDHTManagementEndpoint() {
        return this.getEndPoint(DHTManagementCI.class);
    }

    // Retourne l'endpoint pour l'accès au contenu
    @Override
    public EndPointI<ContentAccessCI> getContentAccessEndpoint() {
        return this.getEndPoint(ContentAccessCI.class);
    }

    // Retourne l'endpoint pour l'exécution MapReduce parallèle
    @Override
    public EndPointI<ParallelMapReduceCI> getMapReduceEndpoint() {
        return this.getEndPoint(ParallelMapReduceCI.class);
    }

    // Définit l'executor service index pour ContentAccess
    public void setContentAccessExecutorServiceIndex(int contentAccessExecutorServiceIndex) {
        ((ContentAccessEndpoint) this.retrieveEndPoint(ContentAccessCI.class))
            .setExecutorServiceIndex(contentAccessExecutorServiceIndex);
    }

    // Définit l'executor service index pour MapReduce
    public void setMapReduceExecutorServiceIndex(int mapReduceExecutorServiceIndex) {
        ((ParallelMapReduceEndpoint) this.retrieveEndPoint(ParallelMapReduceCI.class))
            .setExecutorServiceIndex(mapReduceExecutorServiceIndex);
    }

    // Définit l'executor service index pour la gestion DHT
    public void setDHTManagementExecutorServiceIndex(int dhtManagementExecutorServiceIndex) {
        ((DHTManagementEndpoint) this.retrieveEndPoint(DHTManagementCI.class))
            .setExecutorServiceIndex(dhtManagementExecutorServiceIndex);
    }
}

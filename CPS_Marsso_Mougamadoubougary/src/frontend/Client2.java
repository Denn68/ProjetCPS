package frontend;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import content.ContentKey;
import content.Personne;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import test.CVM;

@RequiredInterfaces(required = { DHTServicesCI.class, ClocksServerCI.class })
public class Client2 extends AbstractComponent {

    // Constantes explicatives
    private static final long TASK_DELAY_SECONDS = 90;
    private static final int EXPECTED_MAP_REDUCE_RESULT = 296;

    protected AcceleratedClock dhtClock;
    protected ClocksServerOutboundPort clockPort;
    protected DHTServicesEndpoint dhtEndPointClient;

    // Constructeur du client
    protected Client2(int nbThreads, int nbSchedulableThreads, DHTServicesEndpoint dhtEndPointClient) {
        super(nbThreads, nbSchedulableThreads);
        this.dhtEndPointClient = dhtEndPointClient;

        try {
            this.clockPort = new ClocksServerOutboundPort(this);
            clockPort.publishPort();

            this.doPortConnection(
                clockPort.getPortURI(),
                ClocksServer.STANDARD_INBOUNDPORT_URI,
                ClocksServerConnector.class.getCanonicalName()
            );

            this.dhtClock = clockPort.getClock(CVM.TEST_CLOCK_URI);

            this.doPortDisconnection(clockPort.getPortURI());
            clockPort.unpublishPort();
            clockPort.destroyPort();

            if (dhtClock.startTimeNotReached()) {
                dhtClock.waitUntilStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode appelée au démarrage du composant
    @Override
    public void start() throws ComponentStartException {
        super.start();
        try {
            if (!this.dhtEndPointClient.clientSideInitialised()) {
                this.dhtEndPointClient.initialiseClientSide(this);
            }
        } catch (Exception e) {
            throw new ComponentStartException("Erreur lors de l'initialisation du client", e);
        }
    }

    // Opérations DHT de base
    public ContentDataI get(ContentKeyI key) throws Exception {
        return this.dhtEndPointClient.getClientSideReference().get(key);
    }

    public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
        return this.dhtEndPointClient.getClientSideReference().put(key, data);
    }

    public ContentDataI remove(ContentKeyI key) throws Exception {
        return this.dhtEndPointClient.getClientSideReference().remove(key);
    }

    public <R extends Serializable, A extends Serializable> A mapReduce(
            SelectorI selector, ProcessorI<R> processor,
            ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
        return this.dhtEndPointClient.getClientSideReference().mapReduce(selector, processor, reductor, combinator, identity);
    }

    // Planification de la tâche
    @Override
    public void execute() throws Exception {
        Instant startInstant = dhtClock.getStartInstant();
        Instant executionInstant = startInstant.plusSeconds(TASK_DELAY_SECONDS);
        long delay = dhtClock.nanoDelayUntilInstant(executionInstant);

        this.scheduleTask(new AbstractTask() {
            @Override
            public void run() {
                executeClientLogic();
            }
        }, delay, TimeUnit.NANOSECONDS);
    }

    // Tache exécutée
    private void executeClientLogic() {
        try {
            System.out.println("Client2 " + reflectionInboundPortURI);

            final String attributeAge = "AGE";
            final String attributeName = "NOM";

            final ContentKey[] keys = {
                new ContentKey(350), new ContentKey(4700),
                new ContentKey(2800), new ContentKey(1431), new ContentKey(3800)
            };

            final String[] names = { "P1", "P2", "P3", "P4", "P6" };
            final int[] ages = { 50, 25, 41, 80, 18 };

            for (int i = 0; i < keys.length; i++) {
                ContentDataI data = get(keys[i]);
                System.out.println(data.getValue(attributeName));
                System.out.println(data.getValue(attributeAge));

                assert data.getValue(attributeName).equals(names[i]) : "Nom incorrect";
                assert data.getValue(attributeAge).equals(ages[i]) : "Age incorrect";
            }

            // Test MapReduce : double l'âge des personnes d'âge pair, puis somme
            int result = mapReduce(
                item -> ((int) item.getValue(attributeAge)) % 2 == 0,
                item -> new Personne(
                    (String) item.getValue(attributeName),
                    (int) item.getValue(attributeAge) * 2
                ),
                (accumulator, value) -> accumulator + (int) value.getValue(attributeAge),
                Integer::sum,
                0
            );

            System.out.println("MapReduce result: " + result);
            assert result == EXPECTED_MAP_REDUCE_RESULT : "Résultat mapReduce incorrect";

            System.out.println("No problem");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Finalisation du composant
    @Override
    public synchronized void finalise() throws Exception {
        this.logMessage("Stopping client component.");
        this.printExecutionLogOnFile("client");

        this.dhtEndPointClient.cleanUpClientSide();
        super.finalise();
    }
}

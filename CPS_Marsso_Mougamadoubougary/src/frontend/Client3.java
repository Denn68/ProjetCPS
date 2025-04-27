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
public class Client3 extends AbstractComponent {

    // Constantes explicatives pour supprimer les magic numbers
    private static final long TASK_EXECUTION_DELAY_SECONDS = 120;
    private static final int MAP_REDUCE_INITIAL_VALUE = 10;
    private static final int EXPECTED_MAP_REDUCE_RESULT = 472;

    protected AcceleratedClock dhtClock;
    protected ClocksServerOutboundPort clockPort;
    protected DHTServicesEndpoint dhtEndPointClient;

    // Constructeur du composant Client3
    protected Client3(int nbThreads, int nbSchedulableThreads, DHTServicesEndpoint dhtEndPointClient) {
        super(nbThreads, nbSchedulableThreads);
        this.dhtEndPointClient = dhtEndPointClient;

        try {
            // Initialisation du port d'horloge
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

            // Attente éventuelle du démarrage de l'horloge
            if (dhtClock.startTimeNotReached()) {
                dhtClock.waitUntilStart();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     // Démarrage du composant
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

    // Méthodes de base pour la gestion du DHT
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
        Instant executionInstant = startInstant.plusSeconds(TASK_EXECUTION_DELAY_SECONDS);
        long delay = dhtClock.nanoDelayUntilInstant(executionInstant);

        this.scheduleTask(new AbstractTask() {
            @Override
            public void run() {
                executeClientTask();
            }
        }, delay, TimeUnit.NANOSECONDS);
    }

     // Tâche éxecutée
    private void executeClientTask() {
        try {
            System.out.println("Client3 " + reflectionInboundPortURI);

            final String attributeName = "NOM";
            final String attributeAge = "AGE";

            final ContentKey[] keys = {
                new ContentKey(21), new ContentKey(22),
                new ContentKey(250), new ContentKey(1234),
                new ContentKey(1999)
            };

            final String[] names = { "P1", "P2", "P3", "P4", "P5" };
            final int[] ages = { 56, 35, 2, 23, 13 };

            for (int i = 0; i < keys.length; i++) {
                Personne personne = new Personne(names[i], ages[i]);
                ContentDataI previousData = put(keys[i], personne);

                // Affichage et validation des données
                System.out.println(get(keys[i]).getValue(attributeName));
                System.out.println(get(keys[i]).getValue(attributeAge));

                assert previousData == null : "Une donnée précédente existe déjà pour cette clé";
                assert get(keys[i]).getValue(attributeName).equals(names[i]) : "Nom incorrect";
                assert get(keys[i]).getValue(attributeAge).equals(ages[i]) : "Âge incorrect";
            }

            // Test MapReduce : double l'âge des personnes dont l'âge est pair et fait la somme, commence à MAP_REDUCE_INITIAL_VALUE
            int result = mapReduce(
                item -> ((int) item.getValue(attributeAge)) % 2 == 0,
                item -> new Personne(
                    (String) item.getValue(attributeName),
                    (int) item.getValue(attributeAge) * 2
                ),
                (accumulator, value) -> accumulator + (int) value.getValue(attributeAge),
                Integer::sum,
                MAP_REDUCE_INITIAL_VALUE
            );

            System.out.println("MapReduce : " + result);
            assert result == EXPECTED_MAP_REDUCE_RESULT : "Résultat MapReduce incorrect";

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

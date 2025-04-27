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
public class Client1 extends AbstractComponent {

    private static final long EXECUTION_DELAY_SECONDS = 60;

    protected AcceleratedClock dhtClock;
    protected ClocksServerOutboundPort clockPort;
    protected DHTServicesEndpoint dhtEndPointClient;

    // Constructeur du client DHT
    protected Client1(int nbThreads, int nbSchedulableThreads, DHTServicesEndpoint dhtEndPointClient) {
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

    // Méthodes pour interagir avec le DHT
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
        return this.dhtEndPointClient.getClientSideReference()
            .mapReduce(selector, processor, reductor, combinator, identity);
    }

    // Planification de la tache
    @Override
    public void execute() throws Exception {
        Instant startInstant = dhtClock.getStartInstant();
        Instant executionInstant = startInstant.plusSeconds(EXECUTION_DELAY_SECONDS);
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
            System.out.println("Client1 " + reflectionInboundPortURI);

            final String attributeAge = "AGE";
            final String attributeName = "NOM";

            final String[] names = { "P1", "P2", "P3", "P4", "P5", "P6" };
            final int[] ages = { 50, 25, 41, 80, 13, 18 };
            final int[] keys = { 350, 4700, 2800, 1431, 5800, 3800 };

            for (int i = 0; i < names.length; i++) {
                ContentKey key = new ContentKey(keys[i]);
                Personne data = new Personne(names[i], ages[i]);

                ContentDataI previousData = put(key, data);
                System.out.println(get(key).getValue(attributeName));
                System.out.println(get(key).getValue(attributeAge));

                boolean isPreviousDataExpected = (i == names.length - 1);

                assert isPreviousDataExpected == (previousData != null) : 
                    "État inattendu pour les anciennes données";
                assert get(key).getValue(attributeName).equals(names[i]) : "Nom incorrect";
                assert get(key).getValue(attributeAge).equals(ages[i]) : "Age incorrect";
            }

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
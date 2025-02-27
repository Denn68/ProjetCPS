package frontend;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.Serializable;

import backend.ContentKey;
import backend.Personne;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.frontend.DHTServicesCI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.CombinatorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ProcessorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ReductorI;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.SelectorI;

@RequiredInterfaces(required = { DHTServicesCI.class })
public class Client 
extends AbstractComponent
{

	public Client(int nbThreads, int nbSchedulableThreads, DHTServicesEndpoint dhtEndPointClient) {
		super(nbThreads, nbSchedulableThreads);
		this.dhtEndPointClient = dhtEndPointClient;
	}
	
	DHTServicesEndpoint dhtEndPointClient;
	@Override
	public void start() {
		try {
			super.start();
		} catch (ComponentStartException e) {
			e.printStackTrace();
		}
		if(!this.dhtEndPointClient.clientSideInitialised()) {
			try {
				this.dhtEndPointClient.initialiseClientSide(this);
			} catch (ConnectionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ContentDataI get(ContentKeyI key) throws Exception {
		return this.dhtEndPointClient.getClientSideReference().get(key);
	}

	public <R extends Serializable, A extends Serializable> A mapReduce(SelectorI selector, ProcessorI<R> processor,
			ReductorI<A, R> reductor, CombinatorI<A> combinator, A identity) throws Exception {
			return this.dhtEndPointClient.getClientSideReference().mapReduce(selector, processor, reductor, combinator, identity);
	}

	public ContentDataI put(ContentKeyI key, ContentDataI data) throws Exception {
		return this.dhtEndPointClient.getClientSideReference().put(key, data);
	}

	public ContentDataI remove(ContentKeyI key) throws Exception {
		return this.dhtEndPointClient.getClientSideReference().remove(key);
	}
	
	@Override
    public  void execute() throws Exception {
        this.runTask(new AbstractComponent.AbstractTask() {
            @Override
            public void run() {
                try {
                    System.out.println(reflectionInboundPortURI);
                    String P1_nom = "P1";
                    int P1_age = 50;
                    String attAGE = "AGE";
                	String attNOM = "NOM";
                    ContentKey key1 = new ContentKey(350); // 350
            		Personne data1 = new Personne(P1_nom, P1_age);
            		System.out.println(((Client) this.getTaskOwner()).put(key1, data1));
                    System.out.println(((Client) this.getTaskOwner()).get(key1).getValue(attNOM));
                    System.out.println(((Client) this.getTaskOwner()).get(key1).getValue(attAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public synchronized void finalise() throws Exception {
        this.logMessage("stopping client component.");
        this.printExecutionLogOnFile("client");

        this.dhtEndPointClient.cleanUpClientSide();

        super.finalise();
    }

}

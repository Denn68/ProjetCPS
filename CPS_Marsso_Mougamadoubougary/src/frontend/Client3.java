package frontend;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

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
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import test.CVM;

@RequiredInterfaces(required = { DHTServicesCI.class, ClocksServerCI.class })
public class Client3 
extends AbstractComponent
{

	protected Client3(int nbThreads, int nbSchedulableThreads, DHTServicesEndpoint dhtEndPointClient) {
		super(nbThreads, nbSchedulableThreads);
		this.dhtEndPointClient = dhtEndPointClient;
		
		try {
			clk_p = new ClocksServerOutboundPort(this);
			clk_p.publishPort();
			
			this.doPortConnection(
				clk_p.getPortURI(),
				ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());
			
			dhtClock = clk_p.getClock(CVM.TEST_CLOCK_URI);
			this.doPortDisconnection(clk_p.getPortURI());
			clk_p.unpublishPort();
			clk_p.destroyPort();
			if (dhtClock.startTimeNotReached()) {
				dhtClock.waitUntilStart();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected AcceleratedClock dhtClock;
	ClocksServerOutboundPort clk_p;
	
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
		
		Instant i0 = dhtClock.getStartInstant();
		Instant i1 = i0.plusSeconds(120);

		long delay = dhtClock.nanoDelayUntilInstant(i1);
		
        this.scheduleTask(
        	new AbstractComponent.AbstractTask() {
	            @Override
	            public void run() {
	                try {                	
	                    System.out.println("Clent3 " + reflectionInboundPortURI);
	                    
	                    String attAGE = "AGE";
	                	String attNOM = "NOM";
	                	
	                    String P1_nom = "P1";
	                    int P1_age = 56;
	                    ContentKey key1 = new ContentKey(21);
	            		Personne data1 = new Personne(P1_nom, P1_age);
	            		
	            		String P2_nom = "P2";
	            		int P2_age = 35;
	            		ContentKey key2 = new ContentKey(22);
	            		Personne data2 = new Personne(P2_nom, P2_age);
	
	            		String P3_nom = "P3";
	            		int P3_age = 2;		
	            		ContentKey key3 = new ContentKey(250);
	            		Personne data3 = new Personne(P3_nom, P3_age);
	
	            		String P4_nom = "P4";
	            		int P4_age = 23;
	            		ContentKey key4 = new ContentKey(1234);
	            		Personne data4 = new Personne(P4_nom, P4_age);
	
	            		String P5_nom = "P5";
	            		int P5_age = 13;
	            		ContentKey key5 = new ContentKey(1999);
	            		Personne data5 = new Personne(P5_nom, P5_age);
	        			
	            		ContentDataI oldPerson1 = ((Client3) this.getTaskOwner()).put(key1, data1);
	        			System.out.println(((Client3) this.getTaskOwner()).get(key1).getValue(attNOM));
	                    System.out.println(((Client3) this.getTaskOwner()).get(key1).getValue(attAGE));
	        			assert oldPerson1 == null : "Type incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key1).getValue(attNOM).equals(P1_nom) : "Nom incorrect";
	        			assert (((Client3) this.getTaskOwner()).get(key1).getValue(attAGE)).equals(P1_age) : "Age incorrect";
	        		
	        			ContentDataI oldPerson2 = ((Client3) this.getTaskOwner()).put(key2, data2);
	        			System.out.println(((Client3) this.getTaskOwner()).get(key2).getValue(attNOM));
	                    System.out.println(((Client3) this.getTaskOwner()).get(key2).getValue(attAGE));
	        			assert oldPerson2 == null : "Type incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key2).getValue(attNOM).equals(P2_nom) : "Nom incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key2).getValue(attAGE).equals(P2_age) : "Age incorrect";
	        			
	        			ContentDataI oldPerson3 = ((Client3) this.getTaskOwner()).put(key3, data3);
	        			System.out.println(((Client3) this.getTaskOwner()).get(key3).getValue(attNOM));
	                    System.out.println(((Client3) this.getTaskOwner()).get(key3).getValue(attAGE));
	        			assert oldPerson3 == null : "Type incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key3).getValue(attNOM).equals(P3_nom) : "Nom incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key3).getValue(attAGE).equals(P3_age) : "Age incorrect";
	        			
	        			ContentDataI oldPerson4 = ((Client3) this.getTaskOwner()).put(key4, data4);
	        			System.out.println(((Client3) this.getTaskOwner()).get(key4).getValue(attNOM));
	                    System.out.println(((Client3) this.getTaskOwner()).get(key4).getValue(attAGE));
	        			assert oldPerson4 == null : "Type incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key4).getValue(attNOM).equals(P4_nom) : "Nom incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key4).getValue(attAGE).equals(P4_age) : "Age incorrect";
	        			
	        			ContentDataI oldPerson5 = ((Client3) this.getTaskOwner()).put(key5, data5);
	        			System.out.println(((Client3) this.getTaskOwner()).get(key5).getValue(attNOM));
	                    System.out.println(((Client3) this.getTaskOwner()).get(key5).getValue(attAGE));
	        			assert oldPerson5 == null : "Type incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key5).getValue(attNOM).equals(P5_nom) : "Nom incorrect";
	        			assert ((Client3) this.getTaskOwner()).get(key5).getValue(attAGE).equals(P5_age) : "Age incorrect";
	
	        		
	        			int res = ((Client3) this.getTaskOwner()).mapReduce(
	        					(item) -> ((int) item.getValue(attAGE)) % 2 == 0,
	        					(item) -> new Personne(((String)item.getValue(attNOM)), ((int) item.getValue(attAGE))*2),
	        					(accumulator, i) -> accumulator + ((int)i.getValue(attAGE)),
	        					(a1, a2) -> a1 + a2,
	        					10);
	        			
	        			System.out.println("MapReduce : " + res);
	        			assert res == 472 : "MapReduce incorrect";
	        			System.out.println("No problem");
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
        }, delay, TimeUnit.NANOSECONDS);
    }

    @Override
    public synchronized void finalise() throws Exception {
        this.logMessage("stopping client component.");
        this.printExecutionLogOnFile("client");

        this.dhtEndPointClient.cleanUpClientSide();

        super.finalise();
    }

}

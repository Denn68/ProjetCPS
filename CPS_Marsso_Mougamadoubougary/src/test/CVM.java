package test;


import java.time.Instant;
import java.util.concurrent.TimeUnit;

import backend.CompositeEndPoint;
import backend.MapReduceResultReceptionEndpoint;
import backend.Node;
import backend.ResultReceptionEndpoint;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import frontend.Client1;
import frontend.Client2;
import frontend.Client3;
import frontend.DHTServicesEndpoint;
import frontend.Facade;
import frontend.Facade2;

public class CVM 
extends AbstractCVM{

	public CVM() throws Exception {
		super();
	}
	
	public static final String TEST_CLOCK_URI = "test-clock";
	public static final Instant START_INSTANT =
	Instant.now();
	protected static final long START_DELAY = 3000L;
	public static final double ACCELERATION_FACTOR = 60.0;
		
	@Override
	public void deploy() throws Exception{
		
		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
		
		AbstractComponent.createComponent(
			ClocksServer.class.getCanonicalName(),
			new Object[]{
			TEST_CLOCK_URI, unixEpochStartTimeInNanos, START_INSTANT, ACCELERATION_FACTOR});
				
		CompositeEndPoint ep2 = new CompositeEndPoint(2);
		MapReduceResultReceptionEndpoint epMR2 = new MapReduceResultReceptionEndpoint();
		
		CompositeEndPoint ep3 = new CompositeEndPoint(2);
		MapReduceResultReceptionEndpoint epMR3 = new MapReduceResultReceptionEndpoint();
		
		CompositeEndPoint ep4 = new CompositeEndPoint(2);
		MapReduceResultReceptionEndpoint epMR4 = new MapReduceResultReceptionEndpoint();
		
		CompositeEndPoint ep5 = new CompositeEndPoint(2);
		MapReduceResultReceptionEndpoint epMR5 = new MapReduceResultReceptionEndpoint();
		
		CompositeEndPoint ep6 = new CompositeEndPoint(2);
		MapReduceResultReceptionEndpoint epMR6 = new MapReduceResultReceptionEndpoint();
		
		ResultReceptionEndpoint epR0 = new ResultReceptionEndpoint();
		MapReduceResultReceptionEndpoint epMR0 = new MapReduceResultReceptionEndpoint();
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				10, 10, 0, 399, ((CompositeEndPoint) ep2.copyWithSharable()), ((CompositeEndPoint)ep3.copyWithSharable()),
				((MapReduceResultReceptionEndpoint) epMR2.copyWithSharable())});
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				10,10, 400, 799, ((CompositeEndPoint) ep3.copyWithSharable()), ((CompositeEndPoint)ep4.copyWithSharable()),
				((MapReduceResultReceptionEndpoint) epMR3.copyWithSharable())});
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				10, 10, 800, 1199, ((CompositeEndPoint) ep4.copyWithSharable()), ((CompositeEndPoint)ep5.copyWithSharable()),
				((MapReduceResultReceptionEndpoint) epMR4.copyWithSharable())});
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				10, 10, 1200, 1599, ((CompositeEndPoint) ep5.copyWithSharable()), ((CompositeEndPoint)ep6.copyWithSharable()),
				((MapReduceResultReceptionEndpoint) epMR5.copyWithSharable())});
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				10, 10, 1600, 1999, ((CompositeEndPoint) ep6.copyWithSharable()), ((CompositeEndPoint)ep2.copyWithSharable()),
				((MapReduceResultReceptionEndpoint) epMR6.copyWithSharable())});
		
		DHTServicesEndpoint ep1 = new DHTServicesEndpoint(); 
		
		AbstractComponent.createComponent(Facade2.class.getCanonicalName(), new Object[] {
				10, 10, ((DHTServicesEndpoint) ep1.copyWithSharable()), ((CompositeEndPoint)ep2.copyWithSharable()), ((ResultReceptionEndpoint) epR0.copyWithSharable()),
				((MapReduceResultReceptionEndpoint) epMR0.copyWithSharable())});
		
		AbstractComponent.createComponent(Client1.class.getCanonicalName(), new Object[] {
				10, 10, ((DHTServicesEndpoint) ep1.copyWithSharable())});
		
		AbstractComponent.createComponent(Client2.class.getCanonicalName(), new Object[] {
				10, 10, ((DHTServicesEndpoint) ep1.copyWithSharable())});
		
		AbstractComponent.createComponent(Client3.class.getCanonicalName(), new Object[] {
				10, 10, ((DHTServicesEndpoint) ep1.copyWithSharable())});
		
		super.deploy();
	}
	
public static void main(String[] args)
    {
        try {
            CVM a = new CVM();
            a.startStandardLifeCycle(30000L);
            Thread.sleep(1000L);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

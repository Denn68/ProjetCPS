package test;


import backend.CompositeEndPoint;
import backend.Node;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import frontend.Client;
import frontend.DHTServicesEndpoint;
import frontend.Facade;

public class CVM 
extends AbstractCVM{

	public CVM() throws Exception {
		super();
	}
		
	@Override
	public void deploy() throws Exception{
		CompositeEndPoint ep2 = new CompositeEndPoint(2);
		
		CompositeEndPoint ep3 = new CompositeEndPoint(2);
		
		CompositeEndPoint ep4 = new CompositeEndPoint(2);
		
		CompositeEndPoint ep5 = new CompositeEndPoint(2);
		
		CompositeEndPoint ep6 = new CompositeEndPoint(2);
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				0, 2, 0, 399, ((CompositeEndPoint) ep2.copyWithSharable()), ((CompositeEndPoint)ep3.copyWithSharable())});
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				0, 2, 400, 799, ((CompositeEndPoint) ep3.copyWithSharable()), ((CompositeEndPoint)ep4.copyWithSharable())});
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				0, 2, 800, 1199, ((CompositeEndPoint) ep4.copyWithSharable()), ((CompositeEndPoint)ep5.copyWithSharable())});
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				0, 2, 1200, 1599, ((CompositeEndPoint) ep5.copyWithSharable()), ((CompositeEndPoint)ep6.copyWithSharable())});
		
		AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[] {
				0, 2, 1600, 1999, ((CompositeEndPoint) ep6.copyWithSharable()), ((CompositeEndPoint)ep2.copyWithSharable())});
		
		DHTServicesEndpoint ep1 = new DHTServicesEndpoint(); 
		
		AbstractComponent.createComponent(Facade.class.getCanonicalName(), new Object[] {
				0, 2, ((DHTServicesEndpoint) ep1.copyWithSharable()), ((CompositeEndPoint)ep2.copyWithSharable())});
		
		AbstractComponent.createComponent(Client.class.getCanonicalName(), new Object[] {
				0, 2, ((DHTServicesEndpoint) ep1.copyWithSharable())});
		
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

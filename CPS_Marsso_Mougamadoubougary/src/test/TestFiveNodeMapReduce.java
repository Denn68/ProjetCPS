package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.CompositeEndPoint;
import backend.ContentAccessEndpoint;
import backend.ContentKey;
import backend.MapReduceEndpoint;
import backend.Node;
import backend.Personne;
import fr.sorbonne_u.components.exceptions.ConnectionException;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import frontend.Client;
import frontend.DHTServicesEndpoint;
import frontend.Facade;

public class TestFiveNodeMapReduce {
	
	private DHTServicesEndpoint ep1; 
	private CompositeEndPoint ep2;
	private CompositeEndPoint ep3;
	private Facade frontend;
	private Client client;
	private Node node1, node2, node3, node4, node5;
	private ContentKey key1, key2, key3, key4, key5, key6;
	private Personne data1, data2, data3, data4, data5, data6;
	private String P1_nom = "P1";
	private String P2_nom = "P2";
	private String P3_nom = "P3";
	private String P4_nom = "P4";
	private String P5_nom = "P5";
	private String P6_nom = "P6";
	private String attAGE = "AGE";
	private String attNOM = "NOM";
	private int P1_age = 50;
	private int P2_age = 25;
	private int P3_age = 41;
	private int P4_age = 80;
	private int P5_age = 13;
	private int P6_age = 18;
	
	@BeforeEach
    public void setUp() {
		
		ep1 = new DHTServicesEndpoint();
		ep2 = new CompositeEndPoint(2);
		ep3 = new CompositeEndPoint(2);
		
		
		
		try {
			node1 = new Node(10, 10, 0, 399, ep2, ep3);
			/*
			node2 = new Node(400, 799, ep2, ep3);
			node3 = new Node(800, 1199, ep3, ep4);
			node4 = new Node(1200, 1599, ep4, ep5);
			*/
			node5 = new Node(10, 10, 0, 399, ep3, ep2);
			frontend = new Facade(10, 10, ep1, ep2);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		client = new Client(10, 10, ep1);
		
		key1 = new ContentKey(350); // 350
		data1 = new Personne(P1_nom, P1_age);
		
		key2 = new ContentKey(4700); // 700
		data2 = new Personne(P2_nom, P2_age);
		
		key3 = new ContentKey(2800); // 800
		data3 = new Personne(P3_nom, P3_age);

		key4 = new ContentKey(1431); // 1431
		data4 = new Personne(P4_nom, P4_age);

		key5 = new ContentKey(5800); // 1800
		data5 = new Personne(P5_nom, P5_age);
		
		key6 = new ContentKey(3800); // 1800
		data6 = new Personne(P6_nom, P6_age);
		
	}
	
	@Test
    public void testPersonData() throws Exception {
        // Test pour la personne 1
        ContentDataI oldPerson1 = frontend.put(key1, data1);
        assertNull(oldPerson1, "Type incorrect");
        ContentDataI newPerson1 = frontend.get(key1);
        assertEquals(P1_nom, newPerson1.getValue(attNOM), "Nom incorrect");
        assertEquals(P1_age, newPerson1.getValue(attAGE), "Age incorrect");

        // Test pour la personne 2
        ContentDataI oldPerson2 = frontend.put(key2, data2);
        assertNull(oldPerson2, "Type incorrect");
        ContentDataI newPerson2 = frontend.get(key2);
        assertEquals(P2_nom, newPerson2.getValue(attNOM), "Nom incorrect");
        assertEquals(P2_age, newPerson2.getValue(attAGE), "Age incorrect");

        // Test pour la personne 3
        ContentDataI oldPerson3 = frontend.put(key3, data3);
        assertNull(oldPerson3, "Type incorrect");
        ContentDataI newPerson3 = frontend.get(key3);
        assertEquals(P3_nom, newPerson3.getValue(attNOM), "Nom incorrect");
        assertEquals(P3_age, newPerson3.getValue(attAGE), "Age incorrect");

        // Test pour la personne 4
        ContentDataI oldPerson4 = frontend.put(key4, data4);
        assertNull(oldPerson4, "Type incorrect");
        ContentDataI newPerson4 = frontend.get(key4);
        assertEquals(P4_nom, newPerson4.getValue(attNOM), "Nom incorrect");
        assertEquals(P4_age, newPerson4.getValue(attAGE), "Age incorrect");

        // Test pour la personne 5
        ContentDataI oldPerson5 = frontend.put(key5, data5);
        assertNull(oldPerson5, "Type incorrect");
        ContentDataI newPerson5 = frontend.get(key5);
        assertEquals(P5_nom, newPerson5.getValue(attNOM), "Nom incorrect");
        assertEquals(P5_age, newPerson5.getValue(attAGE), "Age incorrect");

        // Test pour la personne 6
        ContentDataI oldPerson6 = frontend.put(key6, data6);
        assertEquals(oldPerson6, newPerson5, "Type incorrect");
        ContentDataI newPerson6 = frontend.get(key6);
        assertEquals(P6_nom, newPerson6.getValue(attNOM), "Nom incorrect");
        assertEquals(P6_age, newPerson6.getValue(attAGE), "Age incorrect");
    }

    @Test
    public void testMapReduce() throws Exception {

        int res = frontend.mapReduce(
                (item) -> ((int) item.getValue(attAGE)) % 2 == 0,
                (item) -> new Personne(((String) item.getValue(attNOM)), ((int) item.getValue(attAGE)) * 2),
                (accumulator, i) -> accumulator + ((int) i.getValue(attAGE)),
                (a1, a2) -> a1 + a2,
                10);
        
        assertEquals(60, res, "MapReduce incorrect");
    }
}

package test;

import backend.ContentKey;
import backend.Node;
import backend.Personne;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import frontend.Facade;

public class TestFiveNode {
	public static void main(String[] args) {
		
		Node node1 = new Node(0, 399);
		Node node2 = new Node(400, 799);
		Node node3 = new Node(800, 1199);
		Node node4 = new Node(1200, 1599);
		Node node5 = new Node(1600, 1999);
		
		node1.setSuivant(node2);
		node2.setSuivant(node3);
		node3.setSuivant(node4);
		node4.setSuivant(node5);
		node5.setSuivant(node1);
		
		Facade frontend = new Facade(node1);
		
		String P1_nom = "P1";
		int P1_age = 50;
		ContentKey key1 = new ContentKey(350);
		Personne data1 = new Personne(P1_nom, P1_age);
		
		String P2_nom = "P2";
		int P2_age = 25;
		ContentKey key2 = new ContentKey(4700);
		Personne data2 = new Personne(P2_nom, P2_age);
		
		String P3_nom = "P3";
		int P3_age = 41;		
		ContentKey key3 = new ContentKey(2800);
		Personne data3 = new Personne(P3_nom, P3_age);
		
		String P4_nom = "P4";
		int P4_age = 80;
		ContentKey key4 = new ContentKey(1431);
		Personne data4 = new Personne(P4_nom, P4_age);
		
		String P5_nom = "P5";
		int P5_age = 13;
		ContentKey key5 = new ContentKey(5800);
		Personne data5 = new Personne(P5_nom, P5_age);
		
		String P6_nom = "P6";
		int P6_age = 18;
		ContentKey key6 = new ContentKey(3800);
		Personne data6 = new Personne(P6_nom, P6_age);
		
		try {
			ContentDataI oldPerson1 = frontend.put(key1, data1);
			assert oldPerson1 == null : "Type incorrect";
			ContentDataI newPerson1 = frontend.get(key1);
			assert newPerson1.getValue("NOM").equals(P1_nom) : "Nom incorrect";
			newPerson1 = frontend.remove(key1);
			assert newPerson1.getValue("AGE").equals(P1_age) : "Age incorrect";
			
			ContentDataI oldPerson2 = frontend.put(key2, data2);
			assert oldPerson2 == null : "Type incorrect";
			ContentDataI newPerson2 = frontend.get(key2);
			assert newPerson2.getValue("NOM").equals(P2_nom) : "Nom incorrect";
			newPerson2 = frontend.remove(key2);
			assert newPerson2.getValue("AGE").equals(P2_age) : "Age incorrect";
			
			ContentDataI oldPerson3 = frontend.put(key3, data3);
			assert oldPerson3 == null : "Type incorrect";
			ContentDataI newPerson3 = frontend.get(key3);
			assert newPerson3.getValue("NOM").equals(P3_nom) : "Nom incorrect";
			newPerson3 = frontend.remove(key3);
			assert newPerson3.getValue("AGE").equals(P3_age) : "Age incorrect";
			
			ContentDataI oldPerson4 = frontend.put(key4, data4);
			assert oldPerson4 == null : "Type incorrect";
			ContentDataI newPerson4 = frontend.get(key4);
			assert newPerson4.getValue("NOM").equals(P4_nom) : "Nom incorrect";
			newPerson4 = frontend.remove(key4);
			assert newPerson4.getValue("AGE").equals(P4_age) : "Age incorrect";
			
			ContentDataI oldPerson5 = frontend.put(key5, data5);
			assert oldPerson5 == null : "Type incorrect";
			ContentDataI newPerson5 = frontend.get(key5);
			assert newPerson5.getValue("NOM").equals(P5_nom) : "Nom incorrect";
			//newPerson5 = frontend.remove(key5);
			assert newPerson5.getValue("AGE").equals(P5_age) : "Age incorrect";
			
			ContentDataI oldPerson6 = frontend.put(key6, data6);
			assert oldPerson6 == newPerson5 : "Type incorrect";
			ContentDataI newPerson6 = frontend.get(key6);
			assert newPerson6.getValue("NOM").equals(P6_nom) : "Nom incorrect";
			newPerson6 = frontend.remove(key6);
			assert newPerson6.getValue("AGE").equals(P6_age) : "Age incorrect";
			
			System.out.println("No problem");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

package test;

import java.util.LinkedList;

import backend.ContentAccessSync;
import backend.ContentKey;
import backend.Node;
import backend.Personne;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import frontend.DHTServices;

public class TestFiveNode {
	public static void main(String[] args) {
		
		LinkedList<Node> listOfNodes = new LinkedList<Node>();
		boolean err = listOfNodes.add(new Node(0, 399));
		if (err == false) {
			System.out.println("Erreur lors de l'ajout du noeud 1");
		}
		
		err = listOfNodes.add(new Node(400, 799));
		if (err == false) {
			System.out.println("Erreur lors de l'ajout du noeud 2");
		}
		err = listOfNodes.add(new Node(800, 1199));
		if (err == false) {
			System.out.println("Erreur lors de l'ajout du noeud 3");
		}
		
		err = listOfNodes.add(new Node(1200, 1599));
		if (err == false) {
			System.out.println("Erreur lors de l'ajout du noeud 4");
		}
		
		err = listOfNodes.add(new Node(1600, 1999));
		if (err == false) {
			System.out.println("Erreur lors de l'ajout du noeud 5");
		}
		ContentAccessSync backend = new ContentAccessSync(listOfNodes);
		DHTServices frontend = new DHTServices(backend);
		
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
			System.out.println(oldPerson1);  // égal a null
			ContentDataI newPerson1 = frontend.get(key1);
			assert newPerson1.getValue("NOM").equals(P1_nom) : "Nom incorrect";
			newPerson1 = frontend.remove(key1);
			assert newPerson1.getValue("AGE").equals(P1_age) : "Age incorrect";
			
			ContentDataI oldPerson2 = frontend.put(key2, data2);
			System.out.println(oldPerson2);  // égal a null
			ContentDataI newPerson2 = frontend.get(key2);
			assert newPerson2.getValue("NOM").equals(P2_nom) : "Nom incorrect";
			newPerson2 = frontend.remove(key2);
			assert newPerson2.getValue("AGE").equals(P2_age) : "Age incorrect";
			
			ContentDataI oldPerson3 = frontend.put(key3, data3);
			System.out.println(oldPerson3);  // égal a null
			ContentDataI newPerson3 = frontend.get(key3);
			assert newPerson3.getValue("NOM").equals(P3_nom) : "Nom incorrect";
			newPerson3 = frontend.remove(key3);
			assert newPerson3.getValue("AGE").equals(P3_age) : "Age incorrect";
			
			ContentDataI oldPerson4 = frontend.put(key4, data4);
			System.out.println(oldPerson4);  // égal a null
			ContentDataI newPerson4 = frontend.get(key4);
			assert newPerson4.getValue("NOM").equals(P4_nom) : "Nom incorrect";
			newPerson4 = frontend.remove(key4);
			assert newPerson4.getValue("AGE").equals(P4_age) : "Age incorrect";
			
			ContentDataI oldPerson5 = frontend.put(key5, data5);
			System.out.println(oldPerson5);  // égal a null
			ContentDataI newPerson5 = frontend.get(key5);
			assert newPerson5.getValue("NOM").equals(P5_nom) : "Nom incorrect";
			newPerson5 = frontend.remove(key5);
			assert newPerson5.getValue("AGE").equals(P5_age) : "Age incorrect";
			
			ContentDataI oldPerson6 = frontend.put(key6, data6);
			System.out.println(oldPerson6);  // égal a addr de P5 si on commente la ligne 105 (remove P5)
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

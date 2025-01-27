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
		
		ContentKey key1 = new ContentKey(399);
		Personne data1 = new Personne("Test", 50);
		
		try {
			ContentDataI oldPerson = frontend.put(key1, data1);
			System.out.println(oldPerson);  // égal a null
			ContentDataI newPerson = frontend.get(key1);
			System.out.println(newPerson.getValue("NOM")); // égal à TEST
			newPerson = frontend.remove(key1);
			System.out.println(newPerson.getValue("AGE")); // égal à 50
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

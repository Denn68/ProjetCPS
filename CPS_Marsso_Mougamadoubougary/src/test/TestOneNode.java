package test;

import backend.*;
import frontend.*;
import java.util.LinkedList;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;

public class TestOneNode {
	public static void main(String[] args) {
		LinkedList<Node> oneNode = new LinkedList<Node>();
		boolean err = oneNode.add(new Node(0, 50));
		if (err == false) {
			System.out.println("Erreur lors de l'ajout du noeud");
		}
		ContentAccessSync backend = new ContentAccessSync(oneNode);
		DHTServices frontend = new DHTServices(backend);
		
		ContentKey key1 = new ContentKey(10);
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

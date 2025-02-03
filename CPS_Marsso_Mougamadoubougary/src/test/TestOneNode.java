package test;

import backend.*;
import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;
import frontend.*;

public class TestOneNode {
	public static void main(String[] args) {
		Node oneNode = new Node(0, 50);
		Facade frontend = new Facade(oneNode);
		
		ContentKey key1 = new ContentKey(10);
		Personne data1 = new Personne("Test", 50);
		
		try {
			ContentDataI oldPerson = frontend.put(key1, data1);
			assert oldPerson == null : "Type incorrect";
			ContentDataI newPerson = frontend.get(key1);
			assert newPerson.getValue("NOM").equals("Test") : "Nom incorrect";
			newPerson = frontend.remove(key1);
			assert newPerson.getValue("AGE").equals(50) : "Age incorrect";
			System.out.println("No problem");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

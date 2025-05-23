package content;

import java.io.Serializable;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;

public class Personne 
implements ContentDataI{

	public Personne (String nom, int age) {
		this.nom = nom;
		this.age = age;
	}
	private static final long serialVersionUID = 1L;
	private String nom;
	private int age;
	
	@Override
	public Serializable	getValue(String attributeName) {
		if (attributeName == "NOM"){
			return nom;
		}
		else if (attributeName == "AGE") {
			return age;
		}
		else {
			throw new IllegalArgumentException(
					"unknown attribute for Personne: " + attributeName);
		}
	}
}

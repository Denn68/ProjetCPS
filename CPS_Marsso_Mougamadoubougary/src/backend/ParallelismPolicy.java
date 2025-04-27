package backend;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.mapreduce.ParallelMapReduceI.ParallelismPolicyI;

public class ParallelismPolicy implements ParallelismPolicyI {

    private static final long serialVersionUID = 1L;

    // Nombre de chords pour gérer le parallélisme
    private final int nbreChords;

    // Constructeur de la politique de parallélisme
    public ParallelismPolicy(int nbreChords) {
        this.nbreChords = nbreChords;
    }

    // Retourne le nombre de chords
    public int getNbreChords() {
        return nbreChords;
    }
}

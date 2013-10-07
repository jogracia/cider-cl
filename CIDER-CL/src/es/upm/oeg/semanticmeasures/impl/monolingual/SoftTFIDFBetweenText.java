package es.upm.oeg.semanticmeasures.impl.monolingual;

import java.util.ArrayList;

/**
 * Computes similarity between two texts based on SoftTFIDF measure
 * @author Jorge Gracia
 *
 */
public class SoftTFIDFBetweenText extends SoftTFIDFMeasure {

	public SoftTFIDFBetweenText(ArrayList<String> corpus) {

		super(corpus);

	}

	public double getValue(Object object1, Object element1, Object object2,	Object element2) {

		return SoftTFIDFMeasure.score((String) element1, (String) element2);
	}

	//uncomment for testing
	public static void main(String[] args) {
			
		// create a SoftTFIDF distance learner
		ArrayList<String> corpus = new ArrayList<String>();
        corpus.add("Yahoo Research");
        corpus.add("Microsoft Research");
        corpus.add("IBM Research");
        corpus.add("Google Labs");
        corpus.add("Bell Labs");
        corpus.add("NEC Research Labs");
        
        SoftTFIDFBetweenText rel = new SoftTFIDFBetweenText(corpus);
        
        // now use the distance metric on some examples
        String term1 = "Microsoft Labs";
        String term2 = "Microsoft Research";
        System.out.println("Sim between " + term1 + " and " + term2 + ": " + rel.getValue(null, term1, null, term2));
        
        term1 = "IBM Research";
        term2 = "Yahoo Research";
        System.out.println("Sim between " + term1 + " and " + term2 + ": " + rel.getValue(null, term1, null, term2));
        
        term1 = "Microsoft Reseach";
        term2 = "Microsafe Research";
        System.out.println("Sim between " + term1 + " and " + term2 + ": " + rel.getValue(null, term1, null, term2));
         
        term1 = "Google Labs";
        term2 = "Googel Research";
        System.out.println("Sim between " + term1 + " and " + term2 + ": " + rel.getValue(null, term1, null, term2));
  
        term1 = "googleLabs";
        term2 = "GoogelResearch";
        System.out.println("Sim between " + term1 + " and " + term2 + ": " + rel.getValue(null, term1, null, term2));
	} 
	
}

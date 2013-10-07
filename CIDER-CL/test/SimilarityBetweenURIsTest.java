//please include cidercl.jar in classpath
import es.upm.oeg.semanticmeasures.impl.monolingual.SoftTFIDFBetweenOntologyEntities;
import es.upm.oeg.semanticmeasures.impl.crosslingual.CLESABetweenOntologyEntities;

public class SimilarityBetweenURIsTest {

	 public static void main(String args[]) {
		  
		SimBetweenURIs sim = new SimBetweenURIs();
		double similarity;
		 
		String uriA = "http://cmt_en#c-2650356-3081301";
		String ontologyA = "file:./cmt-en.owl";
		String uriB = "http://cmt_es#c-8133053-7135690";
		String ontologyB = "file:./cmt-es.owl";
		String uriC = "http://sigkdd_en#c-7864895-0370955";
		String ontologyC = "file:./sigkdd-en.owl";
		   
		CLESABetweenOntologyEntities clesa = new CLESABetweenOntologyEntities();

		similarity = clesa.getValue(ontologyA,  uriA,  null, ontologyB , uriB, null);
		System.out.println( "---CROSS-LINGUAL SIMILARITY between " + uriA + " and " + uriB + ": " + similarity + " ---");		  			  	

		SoftTFIDFBetweenOntologyEntities measure = new SoftTFIDFBetweenOntologyEntities(ontologyA, ontologyC);
		similarity = measure.getValue(OntologyExtractor.modelObtaining(ontologyA), uriA , OntologyExtractor.modelObtaining(ontologyC), uriC));
		System.out.println( "---MONO-LINGUAL SIMILARITY between " + uriA + " and " + uriC + ": " + similarity + " ---")
		
//	
	 }// end-main	
}

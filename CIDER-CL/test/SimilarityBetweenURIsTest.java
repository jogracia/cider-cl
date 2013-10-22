//please include cidercl.jar in classpath
import es.upm.oeg.semanticmeasures.impl.monolingual.SoftTFIDFBetweenOntologyEntities;
import es.upm.oeg.semanticmeasures.impl.crosslingual.CLESABetweenOntologyEntities;
import es.upm.oeg.cidercl.extraction.OntologyExtractor;

public class SimilarityBetweenURIsTest {

	 public static void main(String args[]) {
		  
		double monoSim, clSim;
		 
		String uriA = "http://cmt_en#c-4268400-1612321";
		String ontologyA = "file:./test/cmt-en.owl";
		String uriB = "http://cmt_es#c-7348985-5560772";
		String ontologyB = "file:./test/cmt-es.owl";
		String uriC = "http://sigkdd_en#c-0407849-6361536";
		String ontologyC = "file:./test/sigkdd-en.owl";
		   
		CLESABetweenOntologyEntities clesa = new CLESABetweenOntologyEntities();

		clSim = clesa.getValue(OntologyExtractor.modelObtaining(ontologyA),  uriA,  null, OntologyExtractor.modelObtaining(ontologyB) , uriB, null);
	
		SoftTFIDFBetweenOntologyEntities measure = new SoftTFIDFBetweenOntologyEntities(ontologyA, ontologyC);
		monoSim = measure.getValue(OntologyExtractor.modelObtaining(ontologyA), uriA , OntologyExtractor.modelObtaining(ontologyC), uriC);

		System.out.println( "---CROSS-LINGUAL SIMILARITY between " + uriA + " and " + uriB + ": " + clSim + " ---");		  			  	
		System.out.println( "---MONO-LINGUAL SIMILARITY between " + uriA + " and " + uriC + ": " + monoSim + " ---");
		
	 }// end-main	
}

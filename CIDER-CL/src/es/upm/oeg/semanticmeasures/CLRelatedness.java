package es.upm.oeg.semanticmeasures;

/** 
 * Represents a cross-lingual relatedness relation
 * @author Jorge Gracia
 *
 */
public interface CLRelatedness extends Relatedness, CrossLingual {
	
	/** 
	 * Gives the cross-lingual relatedness between two elements (URIs, words, ...) documented in different languages and 
	 * coming from two different objects (ontologies, documents, etc.) 
	 *  
	 * @param object1 source object
	 * @param element1 element to compare inside the source object
	 * @param lang1 source language
	 * @param object2 target object
	 * @param element2 element to compare within the target object 
	 * @param lang2 target language
	 * @return relatedness value
	 */
	
	double getValue(Object object1, Object element1, String lang1, Object object2, Object element2, String lang2);
	
}

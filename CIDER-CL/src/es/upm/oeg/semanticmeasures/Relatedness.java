package es.upm.oeg.semanticmeasures;

/**
 * Represents a relatedness relation
 * 
 * @author Jorge Gracia
 *
 */
public interface Relatedness {
	
	/** 
	 * Give the relatedness between two elements (URIs, words, ...) coming from two different objects (ontologies, documents, etc.) 
	 *  
	 * @param object1 source object
	 * @param element1 element to compare inside the source object
	 * @param object2 target object
	 * @param element2 element to compare within the target object 
	 * @return relatedness value
	 */
	double getValue(Object object1, Object element1, Object object2, Object element2);
}

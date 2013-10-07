package es.upm.oeg.cidercl.util;

import java.util.ArrayList;
import java.util.Vector;

import com.hp.hpl.jena.ontology.OntModel;

import es.upm.oeg.cidercl.extraction.OntologyExtractor;

/**
 * class with some utilities for ontologies
 * 
 * @author Jorge Gracia
 *
 */
public class OntologyUtil {

	
	public static boolean areEquivalentEntities (Object object1, Object element1, Object object2,	Object element2){
	
		
		if (object1.equals(object2) && element1.equals(element2)) return true;
		else {
			ArrayList<String> equivalencesOfTerm1 = OntologyExtractor.getEquivalentTerms((OntModel) object1, (String) element1);
			ArrayList<String> equivalencesOfTerm2 = OntologyExtractor.getEquivalentTerms((OntModel) object2, (String) element2);
			for (int i = 0; i < equivalencesOfTerm1.size(); i++)
				if (equivalencesOfTerm2.contains(equivalencesOfTerm1.get(i))) return true;
			return false;		
		}
		
	}
	
	
}

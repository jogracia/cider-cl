package es.upm.oeg.cidercl.classifiers.features;

import java.util.HashSet;

import org.apache.log4j.Logger;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;

import es.upm.oeg.cidercl.util.OntologyUtil;
import es.upm.oeg.semanticmeasures.impl.crosslingual.CLESABetweenOntologyEntities;
import es.upm.oeg.semanticmeasures.impl.monolingual.SoftTFIDFBetweenOntologyEntities;

/**
 * Computes CLESA relatedness between the different elements (features) 
 * of two ontology entities and stores the results in a structure that will be used for
 * training the classifier. 
 * 
 * This class is not intended to be used in isolation (it is called from "AlignerFromFeaturesDataGenerationCL")
 * 
 * @author Jorge Gracia
 *
 */
public class CLESAFeaturesDataCreator extends CLESABetweenOntologyEntities{

	private static Logger log = Logger.getLogger(CLESAFeaturesDataCreator.class);
	private Instances class_features, property_features;
	private HashSet<String> alignedURIs;
	
	protected CLESAFeaturesDataCreator(Instances classFeaturesDataset, Instances propFeaturesDataset, HashSet<String> alignedURIsSet){
		this.class_features = classFeaturesDataset;
		this.property_features = propFeaturesDataset;
		this.alignedURIs = alignedURIsSet;
	}
	
	/**
	 * Computes features between two ontology entities and updates the corresponding dataset for classes or properties (depending on the type of the entities)
	 *
	 * @param object1 source ontology
	 * @param element1 source entity
	 * @param object2 target ontology
	 * @param element2 target element
	 * @return The returned value is irrelevant (always -1) 
	 */
	public double getValue(Object object1, Object element1, Object object2,	Object element2) {
	
		OntResource r1 = (OntResource) ((OntModel) object1).getOntResource((String) element1);
		OntResource r2 = (OntResource) ((OntModel) object2).getOntResource((String) element2);
		
		if (r1.isClass() && r2.isClass()){
		
			double[] values = new double[class_features.numAttributes()];
			
			values[0] = class_features.attribute(0).addStringValue((String) element1);
			values[1] = class_features.attribute(1).addStringValue((String) element2);
			values[2] = getScoreBetweenLabels((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);  
			values[3] = getScoreBetweenComments((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[4] = getScoreBetweenEquivalentTerms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			values[5] = getScoreBetweenSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			values[6] = getScoreBetweenSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[7] = getScoreBetweenDirectSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			values[8] = getScoreBetweenDirectSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[9] = getScoreBetweenPropertiesOfClasses((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[10] = getScoreBetweenDirectPropertiesOfClasses((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[11] = getScoreBetweenRelatedClasses((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);			
		
			//substitute missing values (sim -1.0) by proper symbols ("?" in weka).
			for (int i=2; i<=11; i++){
				if (values[i] == -1.0d) 
					values[i] = Utils.missingValue();
			}
			
			if (alignedURIs.contains((String) element1 + (String) element2) || (alignedURIs.contains((String) element2 + (String) element1)))
				values[12] = 1.0;
			else	
				values[12] = 0.0;
			
			
			Instance inst = new DenseInstance(1.0, values);
			
			this.class_features.add(inst);
		
		} else if (r1.isProperty() && r2.isProperty()){
			
			double[] values = new double[property_features.numAttributes()];
			
			values[0] = property_features.attribute(0).addStringValue((String) element1);
			values[1] = property_features.attribute(1).addStringValue((String) element2);
			values[2] = getScoreBetweenLabels((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);  
			values[3] = getScoreBetweenComments((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[4] = getScoreBetweenEquivalentTerms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			values[5] = getScoreBetweenSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			values[6] = getScoreBetweenSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[7] = getScoreBetweenDirectSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			values[8] = getScoreBetweenDirectSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[9] = getScoreBetweenDomainsOfProperties((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[10] = getScoreBetweenDirectDomainsOfProperties((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			values[11] = getScoreBetweenRangesOfProperties((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		

			//substitute missing values (sim -1.0) by proper symbols ("?" in weka).
			for (int i=2; i<=11; i++){
				if (values[i] == -1.0d) 
					values[i] = Utils.missingValue();
			}
			
			if (alignedURIs.contains((String) element1 + (String) element2) || (alignedURIs.contains((String) element2 + (String) element1)))
				values[12] = 1.0;
			else	
				values[12] = 0.0;
			
			Instance inst = new DenseInstance(1.0, values);
			
			this.property_features.add(inst);
				
		} 
			
		log.info("CLESA FEATURES computed between " + element1 + " and " + element2);
		
		return -1;	//during features computation the total score is meaningless
		}
	
}

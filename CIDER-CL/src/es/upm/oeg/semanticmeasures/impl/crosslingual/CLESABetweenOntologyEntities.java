package es.upm.oeg.semanticmeasures.impl.crosslingual;


import java.util.ArrayList;

import org.apache.log4j.Logger;

import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Utils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;

import es.upm.oeg.cidercl.extraction.OntologyExtractor;
import es.upm.oeg.cidercl.util.ANNUtil;
import es.upm.oeg.cidercl.util.OntologyUtil;
import es.upm.oeg.cidercl.util.StopWords;
import es.upm.oeg.semanticmeasures.RelatednessBetweenOntologyEntities;
import eu.monnetproject.wsd.utils.Language;
import eu.monnetproject.wsd.utils.Pair;


/**
 * Computes the semantic similarity between two ontology entities based on the CL-ESA metric
 * 
 * @author Jorge Gracia
 *
 */
public class CLESABetweenOntologyEntities  extends CLESA implements RelatednessBetweenOntologyEntities{

	//attributes
	private static double DEFAULT_SCORE = 0.0; //If the value is set to -1 it will be interpreted as "missing value"
	private final String DEFAULT_LANG = "en"; //TODO move it to a parameters file
	private static Logger log = Logger.getLogger(CLESABetweenOntologyEntities.class);
	private static final String CLASSIFIER_CLASS_FILE = "./ANN/ANN_classCL.model";
	private static final String CLASSIFIER_PROP_FILE = "./ANN/ANN_propCL.model";
	private static Classifier classifier_class = ANNUtil.loadClassifier(CLASSIFIER_CLASS_FILE);
	private static Classifier classifier_prop = ANNUtil.loadClassifier(CLASSIFIER_PROP_FILE);
	
	//constructors
	public CLESABetweenOntologyEntities(String lang1, String lang2) {
		super(lang1, lang2);
	}

	public CLESABetweenOntologyEntities() {
		super();
	}

	//methods
	private String determineEntitylabelLanguage (OntModel model, String uri){
		
		ArrayList <String> languageList = OntologyExtractor.getLabelLanguages((OntModel) model, uri);
		
		String lang;
		//For the moment it uses only the first returned language
		//TODO consider all language pairs
		if (languageList.isEmpty() || (languageList == null)) lang = DEFAULT_LANG;
		else lang = languageList.get(0);
		
		return lang;
		
	}
	
	private String determineEntityCommentLanguage (OntModel model, String uri){
		
		ArrayList <String> languageList = OntologyExtractor.getCommentLanguages((OntModel) model, uri);
		
		String lang;
		//For the moment it uses only the first returned language
		//TODO consider all language pairs
		if (languageList.isEmpty() || (languageList == null)) lang = DEFAULT_LANG;
		else lang = languageList.get(0);
		
		return lang;
		
	}
	
	public double getScoreBetweenLabels(OntModel model1, String uri1, OntModel model2, String uri2){

		
		String langSource, langTarget;
		
		//Determine languages
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
			
				
		// Retrieve labels for the given language. If there is no labels, the URI fragment is used instead
		ArrayList<String> labelsSource = OntologyExtractor.getLabels(model1, uri1, langSource);
		if (labelsSource.isEmpty()) labelsSource.add(OntologyExtractor.getUriFragment(model1, uri1)); 
		
		ArrayList<String> labelsTarget = OntologyExtractor.getLabels(model2, uri2, langTarget);
		if (labelsTarget.isEmpty()) labelsTarget.add(OntologyExtractor.getUriFragment(model2, uri2));
	
		// Compute CLESA
		StopWords stopWords = new StopWords();
		String filteredLabelsSource = (String) stopWords.removeStopWords(Language.getLanguage(langSource), labelsSource.toString());
		String filteredLabelsTarget = (String) stopWords.removeStopWords(Language.getLanguage(langTarget), labelsTarget.toString());
		Pair<String, Language> pair1 = new Pair<String, Language>(filteredLabelsSource, Language.getLanguage(langSource));
		Pair<String, Language> pair2 = new Pair<String, Language>(filteredLabelsTarget, Language.getLanguage(langTarget));
		
		return CLESA.score(pair1, pair2);

		
	}

	
	public double getScoreBetweenComments(OntModel model1, String uri1, OntModel model2, String uri2){
		
		String langSource, langTarget;
		
		//Determine languages
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntityCommentLanguage(model1, uri1);

		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntityCommentLanguage(model2, uri2);
		
		
		//Retrieve comments for the given language
		String commentSource = OntologyExtractor.getComment(model1, uri1, langSource);
		String commentTarget = OntologyExtractor.getComment(model2, uri2, langTarget);
		
		if (commentSource.equals("") || commentTarget.equals("")) return DEFAULT_SCORE;
		else {
			// Compute CLESA
			StopWords stopWords = new StopWords();
			String filteredCommentSource = (String) stopWords.removeStopWords(Language.getLanguage(langSource), commentSource);
			String filteredCommentTarget = (String) stopWords.removeStopWords(Language.getLanguage(langTarget), commentTarget);
			Pair<String, Language> pair1 = new Pair<String, Language>(filteredCommentSource, Language.getLanguage(langSource));
			Pair<String, Language> pair2 = new Pair<String, Language>(filteredCommentTarget, Language.getLanguage(langTarget));
		
			return CLESA.score(pair1, pair2);
		}
		
	}
	
	
	public double getScoreBetweenSuperterms(OntModel model1, String uri1, OntModel model2, String uri2){
		
	
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> supertermsURIsSource = OntologyExtractor.getSuperterms(model1, uri1);
		ArrayList<String> supertermsURIsTarget = OntologyExtractor.getSuperterms(model2, uri2);
		
		return CLESABetweenSetsOfURIs(model1, supertermsURIsSource, langSource, model2, supertermsURIsTarget, langTarget);
	
	}
	
	
	public double getScoreBetweenDirectSuperterms(OntModel model1, String uri1, OntModel model2, String uri2){
		
		
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> directSupertermsURIsSource = OntologyExtractor.getDirectSuperterms(model1, uri1);
		ArrayList<String> directSupertermsURIsTarget = OntologyExtractor.getDirectSuperterms(model2, uri2);
		
		return CLESABetweenSetsOfURIs(model1, directSupertermsURIsSource, langSource, model2, directSupertermsURIsTarget, langTarget);
	
	}
	
	public double getScoreBetweenSubterms(OntModel model1, String uri1, OntModel model2, String uri2){
		
		
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> subtermsURIsSource = OntologyExtractor.getSubterms(model1, uri1);
		ArrayList<String> subtermsURIsTarget = OntologyExtractor.getSubterms(model2, uri2);
		
		return CLESABetweenSetsOfURIs(model1, subtermsURIsSource, langSource, model2, subtermsURIsTarget, langTarget);
	
	}
	
	public double getScoreBetweenDirectSubterms(OntModel model1, String uri1, OntModel model2, String uri2){
		
		
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> directSubtermsURIsSource = OntologyExtractor.getDirectSubterms(model1, uri1);
		ArrayList<String> directSubtermsURIsTarget = OntologyExtractor.getDirectSubterms(model2, uri2);
		
		return CLESABetweenSetsOfURIs(model1, directSubtermsURIsSource, langSource, model2, directSubtermsURIsTarget, langTarget);
	
	}
	
	
	public double getScoreBetweenPropertiesOfClasses(OntModel model1, String uri1, OntModel model2, String uri2){
		
		
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> propertyURIsSource = OntologyExtractor.getPropertiesOfClass(model1, uri1);
		ArrayList<String> propertyURIsTarget = OntologyExtractor.getPropertiesOfClass(model2, uri2);
		
		return CLESABetweenSetsOfURIs(model1, propertyURIsSource, langSource, model2, propertyURIsTarget, langTarget);
	
	}
	
	public double getScoreBetweenDirectPropertiesOfClasses(OntModel model1, String uri1, OntModel model2, String uri2){
		
		
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> propertyURIsSource = OntologyExtractor.getDirectPropertiesOfClass(model1, uri1);
		ArrayList<String> propertyURIsTarget = OntologyExtractor.getDirectPropertiesOfClass(model2, uri2);
		
		return CLESABetweenSetsOfURIs(model1, propertyURIsSource, langSource, model2, propertyURIsTarget, langTarget);
	
	}
	
	public double getScoreBetweenDomainsOfProperties(OntModel model1, String uri1, OntModel model2, String uri2){
		
		
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> domainURIsSource = OntologyExtractor.getDomainsOfProperty(model1, uri1);
		ArrayList<String> domainURIsTarget = OntologyExtractor.getDomainsOfProperty(model2, uri2);
		
		return CLESABetweenSetsOfURIs(model1, domainURIsSource, langSource, model2, domainURIsTarget, langTarget);
	
	}
	
	public double getScoreBetweenDirectDomainsOfProperties(OntModel model1, String uri1, OntModel model2, String uri2){
		
		
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> domainURIsSource = OntologyExtractor.getDirectDomainsOfProperty(model1, uri1);
		ArrayList<String> domainURIsTarget = OntologyExtractor.getDirectDomainsOfProperty(model2, uri2);
		
		return CLESABetweenSetsOfURIs(model1, domainURIsSource, langSource, model2, domainURIsTarget, langTarget);
	
	}
	
	private double getCLESABetweenRangesOfObjectProperties(OntModel model1, String uri1, OntModel model2, String uri2){
		
		OntResource r1 = (OntResource) ((OntModel) model1).getOntResource((String) uri1);
		OntResource r2 = (OntResource) ((OntModel) model2).getOntResource((String) uri2);
		
		if (r1.isObjectProperty() && r2.isObjectProperty()){
		
			String langSource, langTarget;
			
			//Determine languages. If language was not defined, takes it from the label of the term
			if (getLanguageSource() != null) langSource = getLanguageSource();
			else langSource = determineEntitylabelLanguage(model1, uri1);
				
			if (getLanguageTarget() != null) langTarget = getLanguageTarget();
			else langTarget = determineEntitylabelLanguage(model2, uri2);
			
			ArrayList<String> rangeURIsSource = OntologyExtractor.getRangesOfObjectProperty(model1, uri1);
			ArrayList<String> rangeURIsTarget = OntologyExtractor.getRangesOfObjectProperty(model2, uri2);
			
			return CLESABetweenSetsOfURIs(model1, rangeURIsSource, langSource, model2, rangeURIsTarget, langTarget);
		} else return 0.0;
		
	}

	// In this case there is no language associated and comparison is based on equality of URIs purely
	private double getSimilarityBetweenRangesOfDatatypeProperties(OntModel model1, String uri1, OntModel model2, String uri2){
		
		OntResource r1 = (OntResource) ((OntModel) model1).getOntResource((String) uri1);
		OntResource r2 = (OntResource) ((OntModel) model2).getOntResource((String) uri2);
		
		if (r1.isDatatypeProperty() && r2.isDatatypeProperty()){
		
			ArrayList<String> rangeURIsSource = OntologyExtractor.getRangesOfObjectProperty(model1, uri1);
			ArrayList<String> rangeURIsTarget = OntologyExtractor.getRangesOfObjectProperty(model2, uri2);
			
			if (rangeURIsSource.containsAll(rangeURIsTarget) && rangeURIsTarget.containsAll(rangeURIsSource))
				 return 1.0;
			else return 0.0;
		} else return 0.0;
					
	}
	
	
	public double getScoreBetweenRangesOfProperties(OntModel model1, String uri1, OntModel model2, String uri2){
	
		OntResource r1 = (OntResource) ((OntModel) model1).getOntResource((String) uri1);
		OntResource r2 = (OntResource) ((OntModel) model2).getOntResource((String) uri2);
		
		if (r1.isObjectProperty() && r2.isObjectProperty()){
			return getCLESABetweenRangesOfObjectProperties(model1, uri1, model2, uri2);
		} else if (r1.isDatatypeProperty() && r2.isDatatypeProperty()){
			return getSimilarityBetweenRangesOfDatatypeProperties(model1, uri1, model2, uri2);
		}
		else return 0.0;
	
	}
	
	
	public double getScoreBetweenRelatedClasses(OntModel model1, String uri1, OntModel model2, String uri2){
	
		int depth = 1;
		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
			
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> relatedClassesURIsSource = OntologyExtractor.getRelatedTermsOfClass(model1, uri1, depth);
		ArrayList<String> relatedClassesURIsTarget = OntologyExtractor.getRelatedTermsOfClass(model2, uri2, depth);
		
		return CLESABetweenSetsOfURIs(model1, relatedClassesURIsSource, langSource, model2, relatedClassesURIsTarget, langTarget);
	}
	
	public double getScoreBetweenEquivalentTerms(OntModel model1, String uri1, OntModel model2, String uri2) {


		String langSource, langTarget;
		
		//Determine languages. If language was not defined, takes it from the label of the term
		if (getLanguageSource() != null) langSource = getLanguageSource();
		else langSource = determineEntitylabelLanguage(model1, uri1);
					
		if (getLanguageTarget() != null) langTarget = getLanguageTarget();
		else langTarget = determineEntitylabelLanguage(model2, uri2);
		
		ArrayList<String> sourceURIs = OntologyExtractor.getEquivalentTerms(model1, uri1);
		ArrayList<String> targetURIs = OntologyExtractor.getEquivalentTerms(model2, uri2);
	
		return CLESABetweenSetsOfURIs(model1, sourceURIs, langSource, model2, targetURIs, langTarget);
	}
	
	private double CLESABetweenSetsOfURIs(OntModel model1, ArrayList<String> urisSource, String langSource, OntModel model2, ArrayList<String> urisTarget, String langTarget){
		
		double score = DEFAULT_SCORE; //default value
		
		if ((urisSource != null) && (!urisSource.isEmpty()) && (urisTarget != null) && (!urisTarget.isEmpty())){
		
			// if sets of URIs are the same then result is maximum, otherwise compute similarity (y do not use "equals" method as the order in the vector of URIs does not matter in this case, and "equals" considers the order) 
			if (urisSource.containsAll(urisTarget) && urisTarget.containsAll(urisSource)) 
				score = 1.0;
			else{
				// Retrieve labels of terms. If there are no labels, URI fragments are used instead
				ArrayList<String> termsLabelsSource = new ArrayList<String>();
				for (int i=0; i < urisSource.size(); i++){
					ArrayList<String> termLabels = OntologyExtractor.getLabels(model1, urisSource.get(i), langSource);
					if (termLabels.isEmpty()) termLabels.add(OntologyExtractor.getUriFragment(model1, urisSource.get(i))); 
					termsLabelsSource.addAll(termLabels);
				}
					
				ArrayList<String> termsLabelsTarget = new ArrayList<String>();
				for (int i=0; i < urisTarget.size(); i++){
					ArrayList<String> termLabels = OntologyExtractor.getLabels(model2, urisTarget.get(i), langTarget);
					if (termLabels.isEmpty()) termLabels.add(OntologyExtractor.getUriFragment(model2, urisTarget.get(i))); 
					termsLabelsTarget.addAll(termLabels);
				}
				
				// Compute CLESA
				StopWords stopWords = new StopWords();
				String filteredTermsLabelsSource = (String) stopWords.removeStopWords(Language.getLanguage(langSource), termsLabelsSource.toString());
				String filteredTermsLabelsTarget = (String) stopWords.removeStopWords(Language.getLanguage(langTarget), termsLabelsTarget.toString());
					
				
				Pair<String, Language> pair1 = new Pair<String, Language>(filteredTermsLabelsSource, Language.getLanguage(langSource));
				Pair<String, Language> pair2 = new Pair<String, Language>(filteredTermsLabelsTarget, Language.getLanguage(langTarget));
			
				score = CLESA.score(pair1, pair2);	
			}
			
		}
		return score;
						
	}


	private double calculateScore(Object object1, Object element1, Object object2,	Object element2) {
		
		
		double score = 0.0;
		
		OntResource r1 = (OntResource) ((OntModel) object1).getOntResource((String) element1);
		OntResource r2 = (OntResource) ((OntModel) object2).getOntResource((String) element2);
		
		//if ontology + uris are the same, or uris correspond to equivalent entities, then returns 1.0
		if (OntologyUtil.areEquivalentEntities  (object1, element1, object2, element2)){
			log.debug("Found equivalence between " + element1 + " and " + element2);
			return 1.0;
		}
			
		if (r1.isClass() && r2.isClass()){
			
						
			Instance instance;
			
			//UNCOMENT TO CONSIDER ATTIBUTES SELECTION
			//TODO parameterize selected attributes
			double[] sim = new double[5];
			
			sim[0] = getScoreBetweenLabels((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);  
			sim[1] = getScoreBetweenSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			sim[2] = getScoreBetweenDirectSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			sim[3] = getScoreBetweenDirectSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			sim[4] = getScoreBetweenDirectPropertiesOfClasses((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
			
			
//			sim[0] = getScoreBetweenLabels((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);  
//			sim[1] = getScoreBetweenComments((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[2] = getScoreBetweenEquivalentTerms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
//			sim[3] = getScoreBetweenSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
//			sim[4] = getScoreBetweenSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[5] = getScoreBetweenDirectSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
//			sim[6] = getScoreBetweenDirectSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[7] = getScoreBetweenPropertiesOfClasses((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[8] = getScoreBetweenDirectPropertiesOfClasses((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[9] = getScoreBetweenRelatedClasses((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);			
			
			
			//substitute missing values (sim -1.0) by proper symbols ("?" in weka).
			for (int i=0; i < sim.length; i++){
				if (sim[i] == -1.0d) 
					sim[i] = Utils.missingValue();
			}
			
			instance = new DenseInstance(1.0, sim);
			try {
				score = classifier_class.classifyInstance(instance);
			} catch (Exception e) {
				log.error("Classifier error when computing relatedness between " + element1 + " and " + element2 + "\n" + e.toString());
			}
		
			
		} else if (r1.isProperty() && r2.isProperty()){
			
			Instance instance;
			
			//UNCOMENT TO CONSIDER ATTIBUTES SELECTION
			//TODO parameterize selected attributes
			double[] sim = new double[3];
			
			sim[0] = getScoreBetweenLabels((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);  
			sim[1] = getScoreBetweenSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			sim[2] = getScoreBetweenRangesOfProperties((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
			
//			sim[0] = getScoreBetweenLabels((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);  
//			sim[1] = getScoreBetweenComments((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[2] = getScoreBetweenEquivalentTerms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
//			sim[3] = getScoreBetweenSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
//			sim[4] = getScoreBetweenSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[5] = getScoreBetweenDirectSuperterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
//			sim[6] = getScoreBetweenDirectSubterms((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[7] = getScoreBetweenDomainsOfProperties((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[8] = getScoreBetweenDirectDomainsOfProperties((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);
//			sim[9] = getScoreBetweenRangesOfProperties((OntModel) object1, (String) element1, (OntModel) object2, (String) element2);		
		
			//substitute missing values (sim -1.0) by proper symbols ("?" in weka).
			for (int i=0; i < sim.length; i++){
				if (sim[i] == -1.0d) 
					sim[i] = Utils.missingValue();
			}
			
			instance = new DenseInstance(1.0, sim);
			try {
				score = classifier_prop.classifyInstance(instance);
			} catch (Exception e) {
				log.error("Classifier error when computing relatedness between " + element1 + " and " + element2 + "\n" + e.toString());
			}
	
		} else 
			score = 0;
		
		score = Math.min(score, 1.0);
		score = Math.max(0,  score);
		
		log.debug("CLESA-based relatedness between " + element1 + " and " + element2 + ": " + score);
		return score;
		
	}
	
	/**
	 * Computes the CL-ESA-based relatedness value between two entities from two ontologies, 
	 * documented in different languages.
	 * It performs elementary computations of CL-ESA to compare several features of the ontological 
	 * description of the compared entities. Such features are combined by means of multilayer perceptrons (one
	 * for classes and another one for properties) to produce a final value. 
	 * 
	 *  @param object1 URI of the source ontology
	 *  @param element1 URI of the source entity
	 *  @param object2 URI of the target ontology
	 *  @param element2 URI of the target element
	 *  @return cross-lingual relatedness value 
	 */
	public double getValue(Object object1, Object element1, Object object2,	Object element2) {
						
		return calculateScore(object1, element1, object2, element2);		
				
	}
	
	public double getValue(Object object1, Object element1, String lang1, Object object2, Object element2, String lang2) {
		
		setLanguageSource(lang1);
		setLanguageTarget(lang2);
		return calculateScore(object1, element1, object2, element2);
	}
	
	
	//uncomment for testing
	public static void main(String[] args) {
		
		CLESABetweenOntologyEntities clesa = new CLESABetweenOntologyEntities();
	
		String ontology1 = "file:./test/cmt-en.owl";
		String uri1 = "http://cmt_en#c-2650356-3081301";
		String ontology2 = "file:./test/cmt-es.owl";
		String uri2 = "http://cmt_es#c-8133053-7135690";
		   
		System.out.println(clesa.getValue(OntologyExtractor.modelObtaining(ontology1), uri1, "en" , OntologyExtractor.modelObtaining(ontology2), uri2, "es"));
		
	}

	
}

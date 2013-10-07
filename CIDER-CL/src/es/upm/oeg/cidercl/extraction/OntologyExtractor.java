package es.upm.oeg.cidercl.extraction;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.jcs.JCS;
import org.apache.log4j.Logger;
//import org.mindswap.pellet.jena.PelletReasonerFactory;


import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import es.upm.oeg.cidercl.util.CacheHandler;

/**
 * Class to extract different parts of the ontological context of an ontology entity.
 * 
 * @author OEG group (Universidad Politécnica de Madrid) and SID group (Universidad de Zaragoza)
 *
 */
public class OntologyExtractor {

	private static String namespaceRDFS = "http://www.w3.org/2000/01/rdf-schema#";
	private static String namespaceOWL = "http://www.w3.org/2002/07/owl#";
	
	private static final JCS ontologicalModelCache = CacheHandler.initializeCache("ontologicalModelCache");
	private static Logger log = Logger.getLogger(OntologyExtractor.class);

	public static OntModel createOntologicalModel(String ontology){
		
		long iniTime= System.currentTimeMillis();
		
		OntModel model =  ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF, null);
		//TODO parametrise inference level. In principle transitivity is applied
		
		if (!((ontology.startsWith("http:"))||(ontology.startsWith("file:"))))
			model.read("file:" + ontology);
			
		else 
			model.read(ontology);
		
			
		log.debug("Model for " +  ontology + " created after (ms) = " + (System.currentTimeMillis()-iniTime));
		
		return model;
	}

	/** 
	 * Creates a Jena model for the ontology or extract it from the cache if the
	 * model already exists
	 * 
	 * @param ontology
	 * @return ontology model
	 */
	public static OntModel modelObtaining(String ontology){
			
		//Try to load from cache
		OntologyModel om = (OntologyModel) ontologicalModelCache.get(ontology);
		OntModel model = (om!=null) ? om.getOntModel(): null;
		
		if (model==null) { 
			model = OntologyExtractor.createOntologicalModel(ontology);
			log.debug("Ontological Model for " + ontology + " created");
		} else 
			log.debug("Ontological Model for " + ontology + " retrieved from cache");
	
		if (model!=null) {
		
			//create a new ontological model
			OntologyModel ont = new OntologyModel(model);
				
			  	//Put the ontological model into cache 
			  	try {
			  		ontologicalModelCache.put(ontology, ont);		  				  		
			  	} catch (Exception ex) {
			  		//Handle failure putting object to cache
			  		log.error( "Problem putting ontological model in cache " + ex.toString() );		  			  	
			  	}	  					
		}
		
		return model;	
	}
	
	public static String getComment(OntModel model, Object uri, String language) 
	{
		String comment = "";
		OntResource r = model.getOntResource((String) uri);
		if (r.getComment(language) != null) comment = r.getComment(language);
		return comment;
	}

	public static ArrayList<String> getComments(OntModel model, Object uri, String language) 
	{
		ArrayList<String> comments = new ArrayList<String>();
		
		//Get labels of URI
		OntResource r = model.getOntResource((String) uri);
		
		for (ExtendedIterator<RDFNode> i = r.listComments(language); i.hasNext();){ 
				String comm = i.next().asNode().getLiteralLexicalForm();
				comments.add(comm);
		}
	
		return comments;
	}
	
	public static ArrayList<String> getLabels(OntModel model, Object uri, String language) 
	{
		ArrayList<String> synonyms = new ArrayList<String>();
		
		//Get labels of URI
		OntResource r = model.getOntResource((String) uri);
		
		for (ExtendedIterator<RDFNode> i = r.listLabels(language); i.hasNext();){ 
				String syn = i.next().asNode().getLiteralLexicalForm();
				//if (!synonyms.contains(syn))
					  synonyms.add(syn);
		}
		return synonyms;

	}
	
	public static ArrayList<String> getAllLabelsAndComments (OntModel model){
		
		ArrayList<String> tokens = new ArrayList<String>();
		
		//Iterate over classes and get their labels and comments
		for (ExtendedIterator<OntClass> iClasses = model.listClasses(); iClasses.hasNext();){
			
			String uri = iClasses.next().getURI();
			if (uri != null) {
				tokens.addAll(getLabels(model, uri, null));
				tokens.addAll(getComments(model, uri, null));
			}
		}
		
		//Iterate over properties and get their labels and comments
		for (ExtendedIterator<OntProperty> iProp = model.listAllOntProperties(); iProp.hasNext();){
			
			String uri = iProp.next().getURI();
			if (uri != null) {
				tokens.addAll(getLabels(model, uri, null));
				tokens.addAll(getComments(model, uri, null));
			}
		}
		
//		//Iterate over individuals and get their labels and comments
//		for (ExtendedIterator<Individual> iInd = model.listIndividuals(); iInd.hasNext();){
//			
//			String uri = iInd.next().getURI();
//			if (uri != null) {
//				tokens.addAll(getLabels(model, uri, null));
//				tokens.addAll(getComments(model, uri, null));
//			}
//		}
		
		return tokens;
	}
	
	public static String getUriFragment(OntModel model, Object uri){
		
		OntResource r = model.getOntResource((String) uri);
		return r.getLocalName();
		
	}
	
	
	public static ArrayList<String> getAllUriFragments(OntModel model){
		
		ArrayList<String> tokens = new ArrayList<String>();
		
		//Iterate over classes and get their uri fragments
		for (ExtendedIterator<OntClass> iClasses = model.listClasses(); iClasses.hasNext();){
			
			String uri = iClasses.next().getURI();
			if (uri != null) tokens.add(getUriFragment(model, uri));
		}
		
		//Iterate over properties and get their uri fragments
		for (ExtendedIterator<OntProperty> iProp = model.listAllOntProperties(); iProp.hasNext();){
					
			String uri = iProp.next().getURI();
			if (uri != null) tokens.add(getUriFragment(model, uri));
	
		}
	
		return tokens;
	}
	
	public static ArrayList<String> getLabelLanguages(OntModel model, Object uri) {

		ArrayList<String> languages = new ArrayList<String>();
		OntResource resource = (OntResource) model.getOntResource((String) uri);
		
		for (ExtendedIterator<RDFNode> i = resource.listLabels(null); i.hasNext() ;){
			Literal label = i.next().asLiteral();
			if (label.getLanguage()!=null && label.getLanguage()!="") languages.add(label.getLanguage());
		}
		return languages;
	
	}

	
	public static ArrayList<String> getCommentLanguages(OntModel model, Object uri) {

		ArrayList<String> languages = new ArrayList<String>();
		OntResource resource = (OntResource) model.getOntResource((String) uri);

		for (ExtendedIterator<RDFNode> i = resource.listComments(null); i.hasNext() ;){
			Literal comment = i.next().asLiteral();
			if (comment.getLanguage()!=null && comment.getLanguage()!="") languages.add(comment.getLanguage());
		}
		return languages;
	
	}
	
	public static HashSet<String> getAllLanguages(OntModel model) {
		
		HashSet<String> languages = new HashSet<String>();

		//Iterate over classes and get the language of their labels and comments
		for (ExtendedIterator<OntClass> iClasses = model.listClasses(); iClasses.hasNext();){
			
			String uri = iClasses.next().getURI();
			if (uri != null) {
				languages.addAll(getLabelLanguages(model, uri));
				languages.addAll(getCommentLanguages(model, uri));
			}
		}
		
		//Iterate over properties and get the language of their labels and comments
		for (ExtendedIterator<OntProperty> iProp = model.listAllOntProperties(); iProp.hasNext();){
			
			String uri = iProp.next().getURI();
			if (uri != null) {
				languages.addAll(getLabelLanguages(model, uri));
				languages.addAll(getCommentLanguages(model, uri));
			}
		}

		return languages;
		
	}
	
	public static ArrayList<String> getSuperterms(OntModel model, Object uri){
		
		ArrayList<String> superterms = new ArrayList<String>();

		OntResource or = model.getOntResource((String) uri);
		if (or.isClass()){ 
			OntClass concept = (OntClass) or.as(OntClass.class);
			for (ExtendedIterator<OntClass> i = concept.listSuperClasses(); i.hasNext();){
				OntClass superclass = (OntClass) i.next();
				if (superclass.getURI()!=null && !superclass.getURI().equals(namespaceOWL + "Thing") && !superclass.getURI().equals(namespaceRDFS + "Resource"))
					superterms.add(superclass.getURI());
			}
		} else if (or.isProperty()){ 
			OntProperty property = (OntProperty) or.as(OntProperty.class);
			for (ExtendedIterator<? extends OntProperty> i = property.listSuperProperties(); i.hasNext();){
				OntProperty superproperty = (OntProperty) i.next();
				if (superproperty.getURI()!=null)
					superterms.add(superproperty.getURI());
			}
		} 
		// in case the term itself is in the list, it is removed
		superterms.remove(uri);
		return superterms;
			
	}
	
	public static ArrayList<String> getDirectSuperterms(OntModel model, Object uri){
		
		ArrayList<String> superterms = new ArrayList<String>();

		OntResource or = model.getOntResource((String) uri);
		if (or.isClass()){ 
			OntClass concept = (OntClass) or.as(OntClass.class);
			for (ExtendedIterator<OntClass> i = concept.listSuperClasses(true); i.hasNext();){ //this 'true' restricts search to direct superclasses
				OntClass superclass = (OntClass) i.next();
				if (superclass.getURI()!=null && !superclass.getURI().equals(namespaceOWL + "Thing") && !superclass.getURI().equals(namespaceRDFS + "Resource"))
					superterms.add(superclass.getURI());
			}
		} else if (or.isProperty()){ 
			OntProperty property = (OntProperty) or.as(OntProperty.class);
			for (ExtendedIterator<? extends OntProperty> i = property.listSuperProperties(true); i.hasNext();){  //this 'true' restricts search to direct superproperties
				OntProperty superproperty = (OntProperty) i.next();
				if (superproperty.getURI()!=null)
					superterms.add(superproperty.getURI());
			}
		} 
		// in case the term itself is in the list, it is removed
		superterms.remove(uri);
		return superterms;
			
	}
	
	
	public static ArrayList<String> getSubterms(OntModel model, Object uri){
		
		
		ArrayList<String> subterms = new ArrayList<String>();

		OntResource or = model.getOntResource((String) uri);
		if (or.isClass()){ 
			OntClass concept = (OntClass) or.as(OntClass.class);
			for (ExtendedIterator<OntClass> i = concept.listSubClasses(); i.hasNext();){ 
				OntClass subclass = (OntClass) i.next();
				if (subclass.getURI()!=null && !subclass.getURI().equals(namespaceOWL + "Nothing"))
					subterms.add(subclass.getURI());
			}
		} else if (or.isProperty()){ 
			OntProperty property = (OntProperty) or.as(OntProperty.class);
			for (ExtendedIterator<? extends OntProperty> i = property.listSubProperties(); i.hasNext();){ 
				OntProperty subproperty = (OntProperty) i.next();
				if (subproperty.getURI()!=null)
					subterms.add(subproperty.getURI());
			}
		} 
		// in case the term itself is in the list, it is removed
		subterms.remove(uri);
		return subterms;
			
	}
	
public static ArrayList<String> getDirectSubterms(OntModel model, Object uri){
		
		ArrayList<String> subterms = new ArrayList<String>();

		OntResource or = model.getOntResource((String) uri);
		if (or.isClass()){ 
			OntClass concept = (OntClass) or.as(OntClass.class);
			for (ExtendedIterator<OntClass> i = concept.listSubClasses(true); i.hasNext();){ //this 'true' restricts search to direct subclasses
				OntClass subclass = (OntClass) i.next();
				if (subclass.getURI()!=null && !subclass.getURI().equals(namespaceOWL + "Nothing"))
					subterms.add(subclass.getURI());
			}
		} else if (or.isProperty()){ 
			OntProperty property = (OntProperty) or.as(OntProperty.class);
			for (ExtendedIterator<? extends OntProperty> i = property.listSubProperties(true); i.hasNext();){ //this 'true' restricts search to direct subproperties
				OntProperty subproperty = (OntProperty) i.next();
				if (subproperty.getURI()!=null)
					subterms.add(subproperty.getURI());
			}
		} 
		// in case the term itself is in the list, it is removed
		subterms.remove(uri);
		return subterms;
			
	}


	public static ArrayList<String> getPropertiesOfClass(OntModel model, Object uri) {
	
		ArrayList<String> propertiesOfClass = new ArrayList<String>();
	
		OntResource r = model.getOntResource((String) uri);
		OntClass concept = (OntClass) r.as(OntClass.class);

		//BOTTLENECK HERE, this Jena method takes, sometimes, a significant amount of time
		for (ExtendedIterator<OntProperty> i = concept.listDeclaredProperties(false); i.hasNext();){ // boolean in 'listDeclaredProperties': if true, restrict the properties returned to those directly associated with this class. 
			Property prop = (Property) i.next();
			if (prop.getURI()!=null)
				propertiesOfClass.add(prop.getURI());
		}
		return propertiesOfClass;
	}
	
	public static ArrayList<String> getDirectPropertiesOfClass(OntModel model, Object uri) {
		
		ArrayList<String> propertiesOfClass = new ArrayList<String>();
	
		OntResource r = model.getOntResource((String) uri);
		OntClass concept = (OntClass) r.as(OntClass.class);

		//BOTTLENECK HERE, this Jena method takes, sometimes, a significant amount of time
		for (ExtendedIterator<OntProperty> i = concept.listDeclaredProperties(true); i.hasNext();){ // boolean in 'listDeclaredProperties': if true, restrict the properties returned to those directly associated with this class. 
			Property prop = (Property) i.next();
			if (prop.getURI()!=null)
				propertiesOfClass.add(prop.getURI());
		}
		return propertiesOfClass;
	}
	
	
	public static ArrayList<String> getDomainsOfProperty(OntModel model, Object uri) {
		
		ArrayList<String> domains = new ArrayList<String>();

		OntResource r = model.getOntResource((String) uri);
		
		if (r.isProperty()){
			OntProperty property = (OntProperty) r.as(OntProperty.class);
			for (ExtendedIterator<? extends OntResource> i = property.listDeclaringClasses(false); i.hasNext();){ // boolean in 'listDeclaringClasses': if true, restrict the properties returned to those directly associated with this class.
					OntClass domain = (OntClass) i.next();
					if (domain.getURI()!=null)
						domains.add(domain.getURI());
			}
		}		
		return domains;
	}
	
	public static ArrayList<String> getDirectDomainsOfProperty(OntModel model, Object uri) {
		
		ArrayList<String> domains = new ArrayList<String>();

		OntResource r = model.getOntResource((String) uri);
		
		if (r.isProperty()){
			OntProperty property = (OntProperty) r.as(OntProperty.class);
			for (ExtendedIterator<? extends OntResource> i = property.listDeclaringClasses(true); i.hasNext();){ // boolean in 'listDeclaringClasses': if true, restrict the properties returned to those directly associated with this class.
					OntClass domain = (OntClass) i.next();
					if (domain.getURI()!=null)
						domains.add(domain.getURI());
			}
		}		
		return domains;
	}
	

	public static ArrayList<String> getRangesOfObjectProperty(OntModel model, Object uri) {
		
		ArrayList<String> ranges = new ArrayList<String>();

		OntResource r = model.getOntResource((String) uri);
		
		if (r.isObjectProperty()){
			OntProperty property = (OntProperty) r.as(OntProperty.class);
			for (ExtendedIterator<? extends OntResource> i = property.listRange(); i.hasNext();){
					OntClass range = (OntClass) i.next();
					if (range.getURI()!=null)
						ranges.add(range.getURI());
			}
		}		
		return ranges;
	}

	public static ArrayList<String> getRangesOfDatatypeProperty(OntModel model, Object uri) {
		
		ArrayList<String> ranges = new ArrayList<String>();

		OntResource r = model.getOntResource((String) uri);
		
		if (r.isDatatypeProperty()){
			OntProperty property = (OntProperty) r.as(OntProperty.class);
			for (ExtendedIterator<? extends OntResource> i = property.listRange(); i.hasNext();){
					OntClass range = (OntClass) i.next();
					if (range.getURI()!=null)
						ranges.add(range.getURI());
			}
		}		
		return ranges;
	}

	public static ArrayList<String> getRelatedTermsOfClass(OntModel model, Object uri, int depth) {
		
		ArrayList<String> relatedterms = new ArrayList<String>();
		if (depth > 0) {
	
			ArrayList<String> properties = getPropertiesOfClass(model, uri);
			ArrayList<String> ranges = new ArrayList<String>();
		
	
			for (int i=0; i < properties.size(); i++){

				OntResource r = model.getOntResource(properties.get(i));
				if (r.isObjectProperty()) {
					// add ranges of the properties of the class
					ranges.addAll(getRangesOfObjectProperty(model, properties.get(i)));
					relatedterms.addAll(ranges);
					// explore other ranges at deeper level (recursively)
					for (int j=0; j < ranges.size(); j++){
							relatedterms.addAll(getRelatedTermsOfClass(model, ranges.get(j), depth-1));
					}
				}
			
			}
		}	
		return relatedterms;
		
	}
		
	public static ArrayList<String> getEquivalentTerms(OntModel model, Object uri) {

			ArrayList<String> eqterms = new ArrayList<String>();

			OntResource or = model.getOntResource((String) uri);
			or.listSameAs();
			for (ExtendedIterator<?> i = or.listSameAs(); i.hasNext();){ 
				OntResource eqterm = (OntResource) i.next();
				if (eqterm.getURI()!=null)
					eqterms.add(eqterm.getURI());
			}
			
			//NOTE: this gives an error if the equivalent class is an external reference. The way to avoid this is to 
			//create the model with OWL inference activated
			if (or.isClass()){ 
				OntClass concept = (OntClass) or.as(OntClass.class);
				for (ExtendedIterator<?> i = concept.listEquivalentClasses(); i.hasNext();){ 
					OntResource eqclass = (OntResource) i.next();
					if (eqclass.getURI()!=null)
						eqterms.add(eqclass.getURI());
				}
			} else if (or.isProperty()){ 
				OntProperty property = (OntProperty) or.as(OntProperty.class);
				for (ExtendedIterator<? extends OntProperty> i = property.listEquivalentProperties(); i.hasNext();){ 
					OntProperty eqproperty = (OntProperty) i.next();
					if (eqproperty.getURI()!=null)
						eqterms.add(eqproperty.getURI());
				}
				
			} 
			return eqterms;
	}
	
	
	//uncomment for testing
	public static void main(String args[]) {
	
//   	String LOCAL_PATH = "file:///home/exmo/gracia/LOCAL/DATA/Ontologies/Multifarm_OAEI2012/dataset-2013/ont/";
		String LOCAL_PATH = "C:/DATA/Ontologies/Multifarm_OAEI2012/dataset-2013/ont/";
//		String uri = "http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#type";
//		String ontology = "file:/home/exmo/gracia/LOCAL/DATA/ExperimentsDATA/tests_LINKED-PRK/model-bnf-persons.xml";
		String ontology = "http://oaei.ontologymatching.org/2011/benchmarks/203/onto.rdf";
//		String ontology = "file:///C:/DATA/ExperimentsDATA/cider_neuralnetworks/tests_CIDER-CL_v1.0/preliminary_tests/model-bnb-persons_new.xml";
		String uri = "http://oaei.ontologymatching.org/2011/benchmarks/203/onto.rdf#Publisher";
	
			
//		String uri = "http://confOf_es#c-6499258-4069928";
//		String ontology = LOCAL_PATH + "es/confOf-es.owl";
//		String ontology = "http://oaei.ontologymatching.org/2011/benchmarks/207/onto-iso8859.rdf";
//		String uri = "http://oaei.ontologymatching.org/2011/benchmarks/207/onto.rdf#crï¿½ateur";
//		String ontology = "C:/DATA/Ontologies/test/extractedSchemaBNF2.rdf";
//		String uri = "http://iflastandards.info/ns/fr/frbr/frbrer/C1001";
//		String ontology = "http://xmlns.com/foaf/spec/20100809.rdf";
//		String uri = "http://iflastandards.info/ns/fr/frbr/frbrer/C1005";
//		String ontology = "http://iflastandards.info/ns/fr/frbr/frbrer.rdf";
	
		OntModel m = OntologyExtractor.createOntologicalModel(ontology);
		ArrayList<String> synonyms = OntologyExtractor.getLabels(m, uri, null);
		OntResource or = m.getOntResource((String) uri);
		System.out.println("LABELS: " + synonyms.toString());
		System.out.println("NAME: " + OntologyExtractor.getUriFragment(m, uri));
		System.out.println("COMMENTS: " + OntologyExtractor.getComments(m, uri, null));
		System.out.println("Label LANGUAGE: " + OntologyExtractor.getLabelLanguages(m, uri).toString());
		System.out.println("Comment LANGUAGE: " + OntologyExtractor.getCommentLanguages(m, uri).toString());
		System.out.println("Superterms: " + OntologyExtractor.getSuperterms(m, uri).toString());
		System.out.println("Subterms: " + OntologyExtractor.getSubterms(m, uri).toString());
		System.out.println("DirectSuperterms: " + OntologyExtractor.getDirectSuperterms(m, uri).toString());
		System.out.println("DirectSubterms: " + OntologyExtractor.getDirectSubterms(m, uri).toString());
		System.out.println("Equivalent Terms: " + OntologyExtractor.getEquivalentTerms(m, uri).toString());
		
		if (or.isClass()){ 
			System.out.println("Properties of class: " + OntologyExtractor.getPropertiesOfClass(m, uri).toString());
			System.out.println("Direct Properties of class: " + OntologyExtractor.getDirectPropertiesOfClass(m, uri).toString());
			System.out.println("Related terms of class at depth 1: " + OntologyExtractor.getRelatedTermsOfClass(m, uri, 1).toString());
		} else if (or.isProperty()){
			System.out.println("Domains of property: " + OntologyExtractor.getDomainsOfProperty(m, uri).toString());
			System.out.println("Direct Domains of property: " + OntologyExtractor.getDirectDomainsOfProperty(m, uri).toString());
			System.out.println("Ranges of object property: " + OntologyExtractor.getRangesOfObjectProperty(m, uri).toString());
			System.out.println("Ranges of datatype property: " + OntologyExtractor.getRangesOfDatatypeProperty(m, uri).toString());
		}
		
		System.out.println("ALL LEXICAL ENTRIES of the ontology: " + OntologyExtractor.getAllLabelsAndComments(m).toString());
	
		System.out.println("ALL URI fragments of the ontology: " + OntologyExtractor.getAllUriFragments(m).toString());
		
		System.out.println("ALL ontology languages: " + OntologyExtractor.getAllLanguages(m) );
		
		System.out.println("ALL PROPERTIES AND ASSOCIATED DOMAINS and ranges");
		
		
		//Iterate over properties and get their labels and comments
		for (ExtendedIterator<OntProperty> iProp = m.listAllOntProperties(); iProp.hasNext();){
			
			Property prop = iProp.next();
			String uri_prop = prop.getURI();
			if ((uri_prop != null) && (uri_prop.startsWith("http://ontology-distiller.org/"))) {
				System.out.println("Property: "  + uri_prop);
					
				System.out.println("-Domains of property: " + OntologyExtractor.getDomainsOfProperty(m, uri_prop).toString());
				System.out.println("-Equivalent Terms: " + OntologyExtractor.getEquivalentTerms(m, uri_prop).toString());
				System.out.println("-Ranges of object property: " + OntologyExtractor.getRangesOfObjectProperty(m, uri_prop).toString());
				System.out.println("-Ranges of datatype property: " + OntologyExtractor.getRangesOfDatatypeProperty(m, uri_prop).toString());
		
			}
		}
		
		
		
	}
}


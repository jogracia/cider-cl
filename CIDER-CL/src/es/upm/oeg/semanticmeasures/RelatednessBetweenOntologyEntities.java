package es.upm.oeg.semanticmeasures;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * Represents a relatedness relation between classes or properties of two ontologies
 * @author Jorge Gracia
 *
 */
public interface RelatednessBetweenOntologyEntities extends Relatedness{

	double getScoreBetweenLabels(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenComments(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenSuperterms(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenDirectSuperterms(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenSubterms(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenDirectSubterms(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenPropertiesOfClasses(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenDirectPropertiesOfClasses(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenDomainsOfProperties(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenDirectDomainsOfProperties(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenRangesOfProperties(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenRelatedClasses(OntModel model1, String uri1, OntModel model2, String uri2);
	double getScoreBetweenEquivalentTerms(OntModel model1, String uri1, OntModel model2, String uri2);
	
	
	
}

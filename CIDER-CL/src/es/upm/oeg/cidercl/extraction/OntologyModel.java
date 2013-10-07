package es.upm.oeg.cidercl.extraction;

import java.io.Serializable;

import com.hp.hpl.jena.ontology.OntModel;

public class OntologyModel implements Serializable {
	
	private static final long serialVersionUID = 600316677913931168L;
	
	private OntModel model;		//Ontological model
	
	//Constructor
	public OntologyModel(OntModel m) {
		model = m;
	}
	
	public OntModel getOntModel() {
		return model;
	}
}
package es.upm.oeg.semanticmeasures.impl.crosslingual;


import org.apache.lucene.queryParser.QueryParser;

import es.upm.oeg.semanticmeasures.CLRelatedness;
import eu.monnetproject.wsd.cl.esa.CLESAmeasure_MulIndex_StopWords;
import eu.monnetproject.wsd.utils.Language;
import eu.monnetproject.wsd.utils.Pair;

/**
 * Defines the basic methods to operate with the CL-ESA metric. 
 * CL-ESA implementation (wsd.jar) by Kartik Asooja for the Monnet project 
 * 
 * @author Jorge Gracia
 * @author Kartik Asooja
 *
 */
abstract public class CLESA implements CLRelatedness{

	//attributes
	private String languageSource, languageTarget;
	protected final static CLESAmeasure_MulIndex_StopWords clesaService = new CLESAmeasure_MulIndex_StopWords(); 
	String configFilePath = "eu.monnetproject.wsd.properties";
	private static boolean started = false;
	
	//constructors
	public CLESA(){
		super();
		setLanguageSource(null); 
		setLanguageTarget(null);
		if(started == false) {
			clesaService.loadConfig(configFilePath);
			clesaService.getConfigAndIndices();
			started = true;
		}
	}
	
	public CLESA(String lang1, String lang2) {
		super();
		setLanguageSource(lang1);
		setLanguageTarget(lang2);
		
		if(started == false) {
			clesaService.loadConfig(configFilePath);
			clesaService.getConfigAndIndices();
			started = true;
		}
	}

	//methods
	
	/**
	 * Gives the score between two Pairs <String, Language> the CL-ESA measure. 
	 * 
	 * @param s source pair
	 * @param t target pair
	 * @return relatedness value
	 */
	public static double score(Pair<String, Language> pair1, Pair<String, Language> pair2){
		String query1 = pair1.getFirst();
		String query2 = pair2.getFirst();		
		query1 = QueryParser.escape(query1);		
		if(query1.equalsIgnoreCase("")) 
			return 0.0;
		query2 = QueryParser.escape(query2);		
		if(query2.equalsIgnoreCase("")) 
			return 0.0;					
		return clesaService.score(pair1, pair2);		
	}	

	public void setLanguageSource(String language) {
		
		this.languageSource = language;
		
	}

	public String getLanguageSource() {

		return this.languageSource;
	
	}
	
	public void setLanguageTarget(String language) {
			
		this.languageTarget = language;
	}

	public String getLanguageTarget() {

		return this.languageTarget;
	
	}

	
}

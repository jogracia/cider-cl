package es.upm.oeg.semanticmeasures;

/**
 * Represents cross-lingual features
 * 
 * @author Jorge Gracia
 *
 */
public interface CrossLingual {

	
	/**
	 * Set target language
	 * @param language
	 */
	void setLanguageTarget(String language);

	/**
	 * Set source language
	 * @param language
	 */
	void setLanguageSource(String language);
}

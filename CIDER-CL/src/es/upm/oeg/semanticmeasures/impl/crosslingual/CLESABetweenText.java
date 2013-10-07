package es.upm.oeg.semanticmeasures.impl.crosslingual;


import es.upm.oeg.cidercl.util.StopWords;
import eu.monnetproject.wsd.utils.Language;
import eu.monnetproject.wsd.utils.Pair;

/**
 * Computes relatedness between two texts according to CL-ESA measure
 * 
 * @author Jorge Gracia
 *
 */
public class CLESABetweenText extends CLESA{

	
	public CLESABetweenText(String lang1, String lang2) {
	
		super(lang1, lang2);
	
	}

	public CLESABetweenText() {
		super();
	}

	//methods
	public double getValue(Object object1, Object element1, Object object2,	Object element2) {
		
		//Compute clesa between elements. Do nothing with objects 1 and 2
		StopWords stopWords = new StopWords();
		String newElement1 = (String) stopWords.removeStopWords(Language.getLanguage(getLanguageSource()), element1.toString());
		String newElement2 = (String) stopWords.removeStopWords(Language.getLanguage(getLanguageTarget()), element2.toString());
		
		Pair<String, Language> pair1 = new Pair<String, Language>(newElement1, Language.getLanguage(getLanguageSource()));
		Pair<String, Language> pair2 = new Pair<String, Language>(newElement2, Language.getLanguage(getLanguageTarget()));

		return CLESA.score(pair1, pair2);
		
	}
	
	public double getValue(Object object1, Object element1, String lang1, Object object2, Object element2, String lang2) {
		
		setLanguageSource(lang1);
		setLanguageTarget(lang2);
		return getValue(object1, element1, object2, element2);
	}
	
	//uncomment for testing
	public static void main(String[] args) {
		

		String text1 = "administrator";
		String text2 = "administrador";		
		CLESABetweenText measure = new  CLESABetweenText();
				
		System.out.println(measure.getValue(null, text1, "en", null, text2, "es"));
	}


	
}

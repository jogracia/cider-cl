package es.upm.oeg.semanticmeasures.impl.monolingual;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.SoftTFIDF;
import com.wcohen.ss.TFIDF;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

import es.upm.oeg.cidercl.util.StringTools;
import es.upm.oeg.semanticmeasures.Relatedness;

/**
 * Defines the basic methods to operate with the SoftTFIDF metric (see http://secondstring.sourceforge.net/)  
 * 
 * @author Jorge Gracia
 *
 */
public abstract class SoftTFIDFMeasure implements Relatedness{

	private static ArrayList<String> corpus = new  ArrayList<String>();
	private static  Tokenizer tokenizer = new SimpleTokenizer(true,true); //parameters: (boolean) ignore punctuation and ignore case
    private static final double minTokenSimilarity = 0.9; //0.9 is the standard value used by the creators of SoftTFIDF
    protected static SoftTFIDF softTFIDF = new SoftTFIDF(tokenizer,new JaroWinkler(), minTokenSimilarity);
    private static Logger log = Logger.getLogger(SoftTFIDFMeasure.class);
	
	public SoftTFIDFMeasure(){
		
	}

	/**
	 * 
	 * @param corpus A corpus is given in which the computation of TFIDF will be based
	 */
	public SoftTFIDFMeasure(ArrayList<String> corpus){
	
		defineCorpus(corpus);
		
	}

	/**
	 * Gives the score between two strings according to the SoftTFIDF measure. It performs camelCase-based 
	 * split in addition to the default tokenization carried out by the reference implementation of SoftTFIDF
	 *  
	 * @param s source string
	 * @param t target string
	 * @return relatedness value
	 */
	public static double score(String s, String t) {
		
		return softTFIDF.score(StringTools.splitCamelCase(s),StringTools.splitCamelCase(t));
			
	}
	
	/**
	 * Gives and explains the score between two strings according to the SoftTFIDF measure. It performs 
	 * camelCase-based split in addition to the default tokenization carried out by the reference implementation of SoftTFIDF
	 *  
	 * @param s source string
	 * @param t target string
	 * @return
	 */
	public static String explainScore(String s, String t) {
		
		return softTFIDF.explainScore(StringTools.splitCamelCase(s),StringTools.splitCamelCase(t));
	
	}
	
	/**
	 * Defines the corpus in which the TFIDF computation will be based and the vector space model is created
	 * @param corpus
	 */
	public static void defineCorpus(ArrayList<String> corpus){
		
		SoftTFIDFMeasure.corpus = corpus;
		
		softTFIDF = new SoftTFIDF(tokenizer,new JaroWinkler(), minTokenSimilarity);
		List<StringWrapper> list = new ArrayList<StringWrapper>();

		
		String[] corp = corpus.toArray(new String[corpus.size()]);
		
		// Crate vector space
		// for efficiency, you train on an iterator over StringWrapper
        // objects, which are produced with the 'prepare' function.
		log.info("Creating vector space for SoftTFIDF computation" + SoftTFIDFMeasure.corpus.hashCode());
		    for (int i=0; i < corp.length; i++) {
	            list.add( softTFIDF.prepare(StringTools.splitCamelCase(corp[i])) );
	    }
	    softTFIDF.train( new BasicStringWrapperIterator(list.iterator()) );	
	
	}
}
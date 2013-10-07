package es.upm.oeg.cidercl.aligner;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.Annotations;
import fr.inrialpes.exmo.align.impl.Namespace;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Properties;
import java.lang.Long;
import java.lang.reflect.Constructor;

import fr.inrialpes.exmo.align.parser.AlignmentParser;
import fr.inrialpes.exmo.ontowrap.OntologyFactory;

/** This class is a variation of Procalign.java (from the Alignment API) to use the aligner within a java program

@author J Gracia
   */

public class ProcessAlignment {

	URI onto1 = null;
	URI onto2 = null;
	AlignmentProcess result = null;
	String cutMethod = "hard";
	String initName = null;
	Alignment init = null;
	String alignmentClassName = "es.upm.oeg.cidercl.aligner.Aligner";
	//String alignmentClassName = "fr.inrialpes.exmo.align.impl.method.StrucSubsDistAlignment";
	//String alignmentClassName = "es.upm.oeg.cidercl.classifiers.features.AlignerForFeaturesDataGenerationCL";
	String filename = null;
	String paramfile = null;
	String rendererClass = "fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor";
	PrintWriter writer = null;
	AlignmentVisitor renderer = null;
	int debug = 2;
	double threshold = 0;
	Properties params = new Properties();
	
	/**
	 *  
	 * @param ontoSource URI of the source ontology
	 * @param ontoTarget URI of the target ontology
	 * @param outputFile file that will store the produced alignment 
	 * @param thres threshold
	 */
	public ProcessAlignment(URI ontoSource, URI ontoTarget, String outputFile, double thres){
		
		OntologyFactory.setDefaultFactory("fr.inrialpes.exmo.ontowrap.jena25.JENAOntologyFactory");
		//OntologyFactory.setDefaultFactory("fr.inrialpes.exmo.ontowrap.owlapi30.OWLAPI3OntologyFactory");
		onto1 = ontoSource;
		onto2 = ontoTarget;
		filename = outputFile;
		threshold = thres;
	    
	}
	
	/**
	 * Execute the alignment process
	 * 
	 * @throws Exception
	 */
    public Alignment run() throws Exception {


	try {
	    try {
			if (initName != null) {
			    AlignmentParser aparser = new AlignmentParser(debug);
			    Alignment al = aparser.parse( initName );
			    init = al;
			    if (debug > 0) System.err.println(" Init parsed");
			}

			// Create alignment object
			Object[] mparams = {};
			Class<?> alignmentClass = Class.forName(alignmentClassName);
			Class<?>[] cparams = {};
			Constructor<?> alignmentConstructor = alignmentClass.getConstructor(cparams);
			result = (AlignmentProcess)alignmentConstructor.newInstance(mparams);
			result.init( onto1, onto2 );
		    } catch (Exception ex) {
			System.err.println("Cannot create alignment "+alignmentClassName+"\n"
					   +ex.getMessage());
			throw ex;
		    }

		    if (debug > 0) System.err.println(" Alignment structure created");
		  
		        
		    
		    
		    // Compute alignment
		    long time = System.currentTimeMillis();
		    result.align(  init, params ); // add opts
		    long newTime = System.currentTimeMillis();
		    result.setExtension( Namespace.ALIGNMENT.uri, Annotations.TIME, Long.toString(newTime - time) );

		    // Thresholding
		    if (threshold != 0) result.cut( cutMethod, threshold );

		    if (debug > 0) System.err.println(" Matching performed");
		    
		    // Set output file
		    OutputStream stream;
		    if (filename == null) {
			stream = System.out;
		    } else {
			stream = new FileOutputStream(filename);
		    }
		    writer = new PrintWriter (
				  new BufferedWriter(
				       new OutputStreamWriter( stream, "UTF-8" )), true);

		    // Result printing (to be reimplemented with a default value)
		    try {
			Object[] mparams = {(Object) writer };
			java.lang.reflect.Constructor<?>[] rendererConstructors =
			    Class.forName(rendererClass).getConstructors();
			renderer =
			    (AlignmentVisitor) rendererConstructors[0].newInstance(mparams);
		    } catch (Exception ex) {
		    	System.err.println("Cannot create renderer "+rendererClass+"\n"
						   + ex.getMessage());
			throw ex;
		    }
		    
		    // Output
		    result.render(renderer);
		} catch (Exception ex) {
		    throw ex;
		} finally {
		    if ( writer != null ) {
			writer.flush();
			writer.close();
		    }
		}
		return result;
	}

	//uncomment for testing
//    public static void main(String[] args) throws Exception {
//	
//    	
//    	URI o1 = new URI("file:./test/OAEI11/benchmark/101/onto.rdf");
//    	URI o2 = new URI("file:./test/OAEI11/benchmark/203/onto.rdf");
//    	
//		String outputFile = "./test/CIDER-CL_101-203.rdf";
//        double threshold = 0.0025;
//    	ProcessAlignment align =  new ProcessAlignment(o1,o2, outputFile, threshold);
//    	    	
//    	align.run();
//    }
    
}

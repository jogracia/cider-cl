/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    Aligner.java
 *    Copyright (C) 2013 Universidad Politécnica de Madrid & Universidad de Zaragoza (Spain)
 *
 */


package es.upm.oeg.cidercl.aligner; 

import java.util.HashSet;
import java.util.Properties;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentException;

import es.upm.oeg.cidercl.extraction.OntologyExtractor;
import es.upm.oeg.semanticmeasures.RelatednessBetweenOntologyEntities;
import es.upm.oeg.semanticmeasures.impl.crosslingual.CLESABetweenOntologyEntities;
import es.upm.oeg.semanticmeasures.impl.monolingual.SoftTFIDFBetweenOntologyEntities;
import fr.inrialpes.exmo.align.impl.DistanceAlignment;
import fr.inrialpes.exmo.align.impl.MatrixMeasure;
import fr.inrialpes.exmo.ontowrap.OntowrapException;

/** The class for ontology alignment of CIDER-CL. It is run 
 * by using fr.inrialpes.exmo.align.util.Procalign, which is contained in cidercl.jar.
 * The processor will parse ontologies, align them
 * and render the resulting alignment.
 * [For further information about Procalign, please read the Alignmet API documentation].
 * Aligner operates by default in "one to one" mode. Parameter -t can be used 
 * to add a threshold.  
 * If there is no language tags in the ontologies, or the set of language tags are equal, monolingual semantic measures are used, 
 * otherwise (the list of language tags does not match) cross-lingual measures are used instead. 
 * 
 * <br><br>
 * Command synopsis is as follows:
 * <pre>
 * java -jar cidercl.jar -i es.upm.oeg.cidercl.aligner [options] onto1 onto2
 * </pre>
 * where the options are:
 * <pre>
 *   --alignment=filename -a filename  Start from an XML alignment file
 *   --params=filename -p filename     Read the parameters in file
 *   --debug[=n] -d [n]                Report debug info at level n,
 *   --output=filename -o filename     Output the alignment in filename
 *   --impl=className -i classname     Use the given alignment implementation.
 *   --renderer=className -r classname Specifies the alignment renderer
 *   --threshold=double -t double      Filters the similarities under threshold
 *   --cutmethod=hard|perc|prop|best|span -T hard|perc|prop|best|span\tmethod for computing the threshold
 *   --debug[=n] -d [n]                Report debug info at level n
 *   -Dparam=value                     Set parameter
 *   --help -h                         Print this message
 * </pre>
 *
 * <CODE>onto1</CODE> and <CODE>onto2</CODE> should be URLs. If output is
 * requested (<CODE>-o</CODE> flags), then output will be written to
 * <CODE>output</CODE> if present, stdout by default.
 * <br><br>
 * Example of usage (ontologies ont1 and ont2 are aligned with a certain threshold t and the result written in CIDER-CL_test_output.rdf file):
 * <pre>
 * java -jar cidercl.jar -i es.upm.oeg.cidercl.aligner -o ./test/CIDER_test_output.rdf file:./test/onto1.owl file:./test/onto2.owl -t 0.0025 
 * </pre>
 * 
 * @author OEG group (Universidad Politécnica de Madrid) and SID group (University of Zaragoza)
 */
public class Aligner extends DistanceAlignment implements AlignmentProcess {
  
	RelatednessBetweenOntologyEntities sim;
	
		
   // Method dissimilarity = null;
   
	
    protected class SimMatrixMeasure extends MatrixMeasure {
		public SimMatrixMeasure() {
		    similarity = false; // This is a distance matrix
		}
		
		public double measure( Object o1, Object o2 ) throws Exception {

			
		 		try {
		 				String strOntA, strOntB, strTermA, strTermB;
		 					
		 				strOntA = ontology1().getFile().toString();
		 				strTermA = ontology1().getEntityURI(o1).toString();

		 				strOntB = ontology2().getFile().toString();
		 				strTermB = ontology2().getEntityURI(o2).toString();
		 				
		 				
		 			if ((strOntA == null) || (strOntB == null))
		 				return 1.; // i.e., comparison is not made
		 			else {
		 				return (1. - sim.getValue(OntologyExtractor.modelObtaining(strOntA), strTermA, OntologyExtractor.modelObtaining(strOntB), strTermB));		
		 			}
		 			
		 			
		 		} catch ( OntowrapException owex ) {
		 		    throw new AlignmentException( "Error getting entity name", owex );
		 		}

		
		}
		public double classMeasure( Object cl1, Object cl2 ) throws Exception {
		    return measure( cl1, cl2 );
		}
		public double propertyMeasure( Object pr1, Object pr2 ) throws Exception{
		    return measure( pr1, pr2 );
		}
		public double individualMeasure( Object id1, Object id2 ) throws Exception{
		    //return measure( id1, id2 );
			return 1.; //ignore individuals
		}
    }

    /** Creation **/
    public Aligner() {
		setSimilarity( new SimMatrixMeasure() );
		setType("??");  
		//TODO parameterize "type"
	}

    /* Processing */
    public void align( Alignment alignment, Properties params ) throws AlignmentException {
//	// Get function from params
//	String f = params.getProperty("stringFunction");
//	try {
//	    if ( f != null ) methodName = f.trim();
//	    Class sClass = Class.forName("java.lang.String");
//	    Class[] mParams = { sClass, sClass };
//	    dissimilarity = Class.forName("fr.inrialpes.exmo.ontosim.string.StringDistances").getMethod( methodName, mParams );
//	} catch (ClassNotFoundException e) {
//	    e.printStackTrace(); // never happens
//	} catch (NoSuchMethodException e) {
//	    throw new AlignmentException( "Unknown method for StringDistAlignment : "+params.getProperty("stringFunction"), e );
//	}
//	public void align() throws AlignmentException {

    // Choose between monolingual/crosslingual measure
    String strOnt1 = ontology1().getFile().toString();
	HashSet<String> languages1 = OntologyExtractor.getAllLanguages(OntologyExtractor.modelObtaining(strOnt1));
	String strOnt2 = ontology2().getFile().toString();
	HashSet<String> languages2 = OntologyExtractor.getAllLanguages(OntologyExtractor.modelObtaining(strOnt2));
		
	if (languages1.isEmpty() || languages2.isEmpty() || languages1.equals(languages2)) {
		sim = new SoftTFIDFBetweenOntologyEntities();
		SoftTFIDFBetweenOntologyEntities.setCorpusFromOntologies(strOnt1, strOnt2);
	} else sim = new CLESABetweenOntologyEntities();
		
        	
	// Initialize matrix
	getSimilarity().initialize( ontology1(), ontology2());

	// Compute similarity/dissimilarity
	getSimilarity().compute(params);

	// Print matrix if asked
	// Extract alignment
	extract( type, params );
    }

}

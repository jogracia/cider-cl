package es.upm.oeg.cidercl.aligner;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.edoal.EDOALAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

/**
 * Class with some utilities to transform alignment files, such as conversion to EDOAL, threshold filtering, etc.
 * 
 * @author Jorge Gracia
 *
 */
public class AlignmentHandler {

	BasicAlignment alignment;
	String inAlignmentURI;
	String outAlignmentFile;
	String rendererClass = "fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor";
	
	//constructor
	/**
	 * 
	 * @param inputAlignmentURI URI pointing to the initial alignment file
	 * @param outputAlignmentFile file that will contain the transformed alignment file 
	 */
	public AlignmentHandler(String inputAlignmentURI, String outputAlignmentFile){
		
		this.inAlignmentURI = inputAlignmentURI;
		this.outAlignmentFile = outputAlignmentFile;
		
		AlignmentParser parser = new AlignmentParser( 0 ); 
		try {
			alignment = (BasicAlignment) parser.parse( inAlignmentURI );
		} catch (AlignmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Alignment getAlignment(){
		
		return this.alignment;
	
	}
	
	public void writeAlignmentFile(Alignment al){
		

		// Print it in a file
		 try { 
			 
			 OutputStream stream;
			 if (outAlignmentFile == null) {
			    	stream = System.out;
			    } else { 	
						stream = new FileOutputStream(outAlignmentFile);		
			    }
			 
			 PrintWriter writer = new PrintWriter (new BufferedWriter(new OutputStreamWriter( stream, "UTF-8" )), true);
		 
			 AlignmentVisitor renderer = new RDFRendererVisitor(writer);
		 	 al.render((AlignmentVisitor) renderer);
		 	 writer.flush();
		 	 writer.close();
		 	  
		} catch (AlignmentException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		  
	}

	/**
	 * Write the alignment in the output file
	 */
	public void writeAlignmentFile(){
		writeAlignmentFile(this.alignment);
	}

	
	/**
	 * Applies a threshold to the alignment and filters out those correspondences below such threshold
	 * 
	 * @param threshold
	 */
	public void trimAlignment(double threshold){
		  
		try {
			alignment.cut(threshold);
		} catch (AlignmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public EDOALAlignment toEDOAL(BasicAlignment al){
		
		EDOALAlignment edoal = new EDOALAlignment();
		try {
			al.toURIAlignment();
			edoal = EDOALAlignment.toEDOALAlignment(al);
		} catch (AlignmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return edoal;
		
	}
	
	/**
	 * Converts the alignment from the basic Alignment Format to the EDOAL format
	 * 
	 */
	public void toEDOAL(){
		this.alignment = toEDOAL((BasicAlignment) alignment);
	}
	
	//uncomment for testing
	static public void main(String args[]){
		
		
		String in = "file:./test/CIDER-CL_101-203.rdf";
		String out = "./test/CIDER-CL_101-203_FILTERED_EDOAL.rdf";		
		double threshold = 0.90;
		
		//trim alignment
		AlignmentHandler handler = new AlignmentHandler(in, out);
		handler.trimAlignment(threshold);
		
		//convert to EDOAL
		handler.toEDOAL();
		
		//write tansformed alignment
		handler.writeAlignmentFile();	
		
	}
	
}

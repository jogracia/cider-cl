package es.upm.oeg.cidercl.classifiers.refalignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Class that parses a set of files in the alignment format into a single file in text format 
 * containing: source URI, target URI, and alignment score (separated by commas) from the alignment file.
 * 
 * Command line: 
 *  <pre>
 * java -cp cidercl.jar  es.upm.oeg.cidercl.classifiers.refalignment.OAEIBenchReferenceAlignmentsParser alignments_dir [csv_output_file]
 * </pre>
 * where 
 * <pre> 
 * alignments_dir - directory containing reference alignment files as they are typically organised in the OAEI Benchmark tracks
 * output_file - path and name of the created file containing the alignments, comma separated (default: "./refalign.csv")
 * </pre> 
 * 
 * @author Jorge Gracia
 *
 */
public class OAEIBenchReferenceAlignmentsParser {



	public void doCreate (String currentDir){
		doCreate (currentDir, null);
	}
	public void doCreate (String currentDir, String outputFile){

    	File [] subdir = null;
    	try {
    		   subdir = (new File(currentDir)).listFiles();
    		
    	} catch (Exception e) {
    	    System.err.println("Cannot stat dir "+ e.getMessage());
      	}
    	int size = subdir.length;
            Arrays.sort(subdir);
 
    	for ( int j=0 ; j < size; j++ ) {
    		
    	    if( subdir[j].isDirectory() ) {
	    		System.out.println("\nEntering directory "+subdir[j]);
	    		String refalignName = subdir[j].getAbsolutePath() + "/refalign.rdf";
	    		try{
	    			FileReader refalignFile = new FileReader(refalignName);
	    			if (refalignFile != null) {
	    				ReferenceAlignmentFileParser parser; 
	    				if ((outputFile == null)||(outputFile.isEmpty()))
	    					 parser = new ReferenceAlignmentFileParser(refalignName);
	    				else parser = new ReferenceAlignmentFileParser(refalignName, outputFile);
		    			parser.doParse();
	    			}
	    			refalignFile.close();
				} catch (FileNotFoundException e){
	    			System.out.println("Alignment file not found, " + e.toString());
	    		} catch (IOException e) {
					e.printStackTrace();
				}
	    		
    	    }
    	}
    }

	public void usage() {
		System.err.println("\n usage: OAEIBenchReferenceAlignmentsParser directory [output_file]");
		System.err.println("\t alignments_dir is the root path where the alignment files can be found");
		System.err.println("\t output_file is a text file containing the alignments, comma separated (if ommited, \"refalign.csv\" is created in the current folder)");
	}
  
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		OAEIBenchReferenceAlignmentsParser obrf = new OAEIBenchReferenceAlignmentsParser();
		
		if (args.length==1) obrf.doCreate(args[0]);
		else if (args.length==2) obrf.doCreate(args[0], args[1]);
		else obrf.usage(); //obrf.doCreate("C:/DATA/Ontologies/OAEI11_bench/benchmarks", "./ANN/refalignNEW.csv");
		
	}

}

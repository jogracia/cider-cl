package es.upm.oeg.cidercl.classifiers.refalignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

/**
 * Class that parses a set of files in the alignment format into a single file in text format 
 * containing: source URI, target URI, and alignment score (separated by commas) from the alignment files.
 * 
 * Command line: 
 *  <pre>
 * java -cp cidercl.jar es.upm.oeg.cidercl.classifiers.refalignment.OAEIMultifarmReferenceAlignmentsParser alignments_dir [output_file]
 * </pre>
 * where 
 * <pre> 
 * alignments_dir - directory containing reference files as they were organised in OAEI'13 MULTIFARM track
 * output_file - path and name of the created file containing the alignments, comma separated (default: "./refalignCL.csv")
 * </pre> 
 * @author Jorge Gracia
 *
 */
public class OAEIMultifarmReferenceAlignmentsParser {

	private final static String NAME_OUTPUT_DEFAULT_FILE = "refalignCL.csv";
	OAEIConfReferenceAlignmentsParser cbrf = new OAEIConfReferenceAlignmentsParser();

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
	    
	    		try {
					cbrf.doCreate(subdir[j].getCanonicalPath(), outputFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
	    		

        }
	
	}
	
	public void usage() {
		System.err.println("\n usage: OAEIMultifarmReferenceAlignmentsParser alignments_dir [output_file]");
		System.err.println("\t alignments_dir is the root path where the alignment files can be found");
		System.err.println("\t output_file is a text file containing the alignments, comma separated (if ommited, \"refalignCL.csv\" is created in the current folder)");
	}
  
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		OAEIMultifarmReferenceAlignmentsParser mrf = new OAEIMultifarmReferenceAlignmentsParser();
		if (args.length==1) mrf.doCreate(args[0], NAME_OUTPUT_DEFAULT_FILE); 
		else if (args.length==2) mrf.doCreate(args[0], args[1]);
		else mrf.usage(); //mrf.doCreate("C:/DATA/Ontologies/Multifarm_OAEI2012/dataset-2013/ref", "./ANN/refalignCL.csv");
	}

}

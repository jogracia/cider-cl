package es.upm.oeg.cidercl.classifiers.refalignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Vector;

/**
 * Class that parses a set of files in the alignment format into a single file in text format 
 * containing: source URI, target URI, and alignment score (separated by commas) from the alignment file.
 *
 * Command line: 
 *  <pre>
 * java -cp cidercl.jar  es.upm.oeg.cidercl.classifiers.refalignment.OAEIConfReferenceAlignmentsParser alignments_dir [output_file]
 * </pre>
 * where 
 * <pre> 
 * alignments_dir - directory containing reference files as they were organised in OAEI'11 CONFERENCE track
 * output_file - path and name of the created file containing the alignments, comma separated (default: "./refalign.csv")
 * </pre> 
 * 
 * @author Jorge Gracia
 *
 */
public class OAEIConfReferenceAlignmentsParser {


	public void doCreate (String currentDir){
		doCreate (currentDir, null);
	}
	
	public void doCreate (String currentDir, String outputFile){

        File [] files = null;
        	try {
        		   files = (new File(currentDir)).listFiles();
        		
        	} catch (Exception e) {
        	    System.err.println("Cannot stat dir "+ e.getMessage());
          	}
         int size = files.length;
         Arrays.sort(files);
        	
        	
    	for ( int j=0 ; j < size; j++ ) {
    		
    	    if( files[j].isFile() ) {
	    		try{
	    			FileReader refalignFile = new FileReader(files[j]);
	    			if (refalignFile != null) {
	    				System.out.println("processing " + files[j].getName());
	    				ReferenceAlignmentFileParser parser; // = new ReferenceAlignmentFileParser(files[j].getAbsolutePath());
	    				if ((outputFile == null)||(outputFile.isEmpty()))
	    					 parser = new ReferenceAlignmentFileParser(files[j].getAbsolutePath());
	    				else parser = new ReferenceAlignmentFileParser(files[j].getAbsolutePath(), outputFile);
		    			parser.doParse();
		    			
	    			}
	    		} catch (FileNotFoundException e){
	    			System.out.println("Alignment file not found");
	    		}
	    		
    	    }
    	}

    }
	
	public void usage() {
		System.err.println("\n usage: OAEIConfReferenceAlignmentsParser alignments_dir [output_file]");
		System.err.println("\t alignments_dir is the root path where the alignment files can be found");
		System.err.println("\t output_file is a text file containing the alignments, comma separated (if ommited, \"refalign.csv\" is created in the current folder)");
	}
  
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		OAEIConfReferenceAlignmentsParser cbrf = new OAEIConfReferenceAlignmentsParser();
		if (args.length==1) cbrf.doCreate(args[0]); 
		else if (args.length==2) cbrf.doCreate(args[0], args[1]);
		else cbrf.usage(); //cbrf.doCreate("C:/DATA/Ontologies/OAEI13_conf/reference-alignment", "./ANN/refalignConf.csv");
	}

}

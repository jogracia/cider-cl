package es.upm.oeg.cidercl.classifiers.refalignment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/** 
 * This class takes as input an RDF file in the Alignment Format, which is passed as execution parameter (arg[0]) 
 * and gives a CSV file as output (arg[1]) containing: source URI, target URI, and alignment score (separated by commas)
 * from the alignment file (default: "./refalign.csv"). If the output file exists, the new data is appended at the end. Thus, if there are several
 * alignment files to convert, it has to be executed several times to produce a single converted file.
 *
 * Command line: 
 *  <pre>
 * java -cp cidercl.jar  es.upm.oeg.cidercl.classifiers.refalignment.ReferenceAlignmentFileParser alignment_file [csv_output_file]
 * </pre>
 * where 
 * <pre> 
 * alignment_file is the source RDF file in the Alignment format and
 * csv_output_file is a text file containing the alignments, comma separated
 * </pre> 
 * @author Jorge Gracia
 *
 */
public class ReferenceAlignmentFileParser {

  private final static String PATH_OUTPUT_DEFAULT_FILE = "./";
  private final static String NAME_OUTPUT_DEFAULT_FILE = "refalign.csv";

  private FileWriter fileOutputWriter;
  private String fileToParse;
  private String fileOutput;
  
  public void openOutputFile(){
	  try{
			fileOutputWriter = new FileWriter(fileOutput, true);
		}catch (IOException e){
			String err = e.toString();
			System.out.println(err);
		}
  }
  
  public ReferenceAlignmentFileParser(String fileInput){
	  this.fileToParse = fileInput;
	  this.fileOutput = PATH_OUTPUT_DEFAULT_FILE + NAME_OUTPUT_DEFAULT_FILE;
  }
  
  public ReferenceAlignmentFileParser(String fileIn, String fileOut){
	  this.fileToParse = fileIn;
	  this.fileOutput = fileOut;
  }
  
  public ReferenceAlignmentFileParser() {
	// TODO Auto-generated constructor stub
  }

  public void setFileToParse(String file){
	  fileToParse = file;
  }
  
  public void setFileOutput(String file){
	  fileOutput = file;
  }
 
  public void doParse(){
	   try {
	    	 
		    openOutputFile();
	   		
	        SAXBuilder builder=new SAXBuilder(false); 

	        // args[0] will contain the alignment file in the alignment format
	        Document doc=builder.build(fileToParse);

	        Element root=doc.getRootElement();
	   
	        Namespace nsrdf = root.getNamespace();
	        Namespace nsbase = Namespace.getNamespace("http://knowledgeweb.semanticweb.org/heterogeneity/alignment");
	    
	        List<Element> alignments = root.getChildren("Alignment", nsbase);

	      
	        for (int i=0; i< alignments.size(); i++)
	        {
	          Element alignment = alignments.get(i);
	          
	          List<Element> maps =  alignment.getChildren("map",nsbase);
	                   
	          for (int j=0; j< maps.size(); j++)
	          {
	        	  
	        	  Element map = maps.get(j);
	          	  List cells = map.getChildren("Cell", nsbase);
	              for (int k=0; k< cells.size(); k++)
	              {       	  
	            	  	Element cell= (Element) cells.get(k);
	            	  
	            	  	try{
	            	  		Element entity1 = (Element) cell.getChild("entity1",nsbase);
	                	  	fileOutputWriter.append(entity1.getAttributeValue("resource", nsrdf) + ",");
	            	  		Element entity2 = (Element) cell.getChild("entity2",nsbase);
	            	  		fileOutputWriter.append(entity2.getAttributeValue("resource", nsrdf) + ",");
	            	  		Element measure = (Element) cell.getChild("measure",nsbase);
	            	  		fileOutputWriter.append(measure.getText() +  "\r\n");
	            	  		fileOutputWriter.flush();
	    				} catch (IOException e){
	    					String err = e.toString();
	    					System.out.println(err);
	    				}
	    		
	              }
	             
	          }
	          System.out.println("Saved " + maps.size() + " reference mappings in " + fileOutput);
	        }

	        try{
	        	fileOutputWriter.close();
	        } catch (IOException e){
				String err = e.toString();
				System.out.println(err);
			}
	        
	        
	     }catch (Exception e){
	        e.printStackTrace();
	     }
  
  }
  
  public void usage() {
		System.err.println("\n usage: ReferenceAlignmentFileParser alignment_file [output_file]");
		System.err.println("\t alignment_file is the source RDF file in the Alignment format and");
		System.err.println("\t output_file is a text file containing the alignments, comma separated (default: \"./refalign.csv\")");
  }
  
  public static void main(String[] args) {

	  ReferenceAlignmentFileParser parser = new ReferenceAlignmentFileParser();
	  if (args.length == 1){
		  parser.setFileToParse(args[0]);
		  parser.setFileOutput(PATH_OUTPUT_DEFAULT_FILE + NAME_OUTPUT_DEFAULT_FILE);
		  parser.doParse();
	  } else if (args.length == 2){
		  parser.setFileToParse(args[0]);
		  parser.setFileOutput(args[1]);
		  parser.doParse();
	  } else
		  parser.usage();
	  
  }
}

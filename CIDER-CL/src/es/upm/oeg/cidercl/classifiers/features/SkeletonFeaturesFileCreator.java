package es.upm.oeg.cidercl.classifiers.features;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

/**
 * Class to create the skeleton structure for the files that will contain 
 * the data (features) that will be used to train the classifiers. 
 * Files are created with ".arff" extension.
 * 
 * @author Jorge Gracia
 *
 */
public class SkeletonFeaturesFileCreator {
	
	private static Logger log = Logger.getLogger(SkeletonFeaturesFileCreator.class);
	
	Instances dataset_class;
	Instances dataset_prop;

	static public void defineDatasetOfClasses(String classFeaturesFilePath){
	    	
	    	//DEFINING DATASET IN MEMORY
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			attributes.add(new Attribute("URI1", (ArrayList<String>) null));
			attributes.add(new Attribute("URI2", (ArrayList<String>) null));
			attributes.add(new Attribute("SIM1-labels"));
			attributes.add(new Attribute("SIM2-comments"));
			attributes.add(new Attribute("SIM3-equiv"));		
			attributes.add(new Attribute("SIM4-sub"));
			attributes.add(new Attribute("SIM5-super"));		
			attributes.add(new Attribute("SIM6-directSuper"));
			attributes.add(new Attribute("SIM7-directSub"));
			attributes.add(new Attribute("SIM8-prop"));
			attributes.add(new Attribute("SIM9-directProp"));
			attributes.add(new Attribute("SIM10-related"));
			attributes.add(new Attribute("reference"));

	    	log.info("Features file structure defined (classes)");
			Instances dataset = new Instances("Features file for classes", attributes,1000);
			
			// save data as ARFF
			try {
				DataSink.write(classFeaturesFilePath, dataset);
				log.info("Empty features file created (classes)");
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	 
	 }
	    
	 static  public void defineDatasetOfProperties(String propFeaturesFilePath){
	    	
	    	//DEFINING DATASET IN MEMORY
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			attributes.add(new Attribute("URI1", (ArrayList<String>) null));
			attributes.add(new Attribute("URI2", (ArrayList<String>) null));
			attributes.add(new Attribute("SIM1-labels"));
			attributes.add(new Attribute("SIM2-comments"));
			attributes.add(new Attribute("SIM3-equiv"));		
			attributes.add(new Attribute("SIM4-sub"));
			attributes.add(new Attribute("SIM5-super"));		
			attributes.add(new Attribute("SIM6-directSuper"));
			attributes.add(new Attribute("SIM7-directSub"));
			attributes.add(new Attribute("SIM8-dom"));
			attributes.add(new Attribute("SIM9-directDom"));
			attributes.add(new Attribute("SIM10-ranges"));
			attributes.add(new Attribute("reference"));

			Instances dataset = new Instances("Features file for properties", attributes,1000);

	    	log.info("Features file structure defined (properties)");
			
	    	// save data as ARFF
			try {
				DataSink.write(propFeaturesFilePath, dataset);
				log.info("Empty features file created (properties)");
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	
	    }
	    
	//uncomment for testing
//	public static void main(String[] args) {
//
//		SkeletonFeaturesFileCreator.defineDatasetOfClasses(args[0]);
//		SkeletonFeaturesFileCreator.defineDatasetOfProperties(args[1]);
//		
//	}

}

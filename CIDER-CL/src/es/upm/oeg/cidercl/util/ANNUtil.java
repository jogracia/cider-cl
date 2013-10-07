package es.upm.oeg.cidercl.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Some utilities to work with classifiers
 * 
 * @author Jorge Gracia
 *
 */
public class ANNUtil {

	public static Classifier loadClassifier(String classifierFile){
		
		Classifier classifier = null;
		
		// load classifier
		try {
			classifier = (Classifier) SerializationHelper.read(classifierFile);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classifier;
	}

	//XX UNDER CONSTRUCTION!!!!!!!!!!!
//	public static void removeDuplicatedRowsInFeaturesFile (String featuresFile, String newFeaturesFile){
//		
//		Instances dataset=null, datasetUnique;
//		
//		// read features data from files
//		try {
//			dataset = DataSource.read(featuresFile);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		// filter (to remove the URIs attributes from the data) 
//		Remove rm_uris = new Remove();
//		rm_uris.setAttributeIndicesArray(new int[] {0,1}); // remove 1st and 2nd attributes (URIs) and [TODO] attributes with less predictive power
//		try {
//			rm_uris.setInputFormat(dataset);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//				
//		// apply filters 
//		try {
//			dataset = Filter.useFilter(dataset, rm_uris);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//			
//		
//	    // copy data
//		datasetUnique = new Instances(dataset, dataset.size()); 
//	 
//		//XX EN OBRAS!!!!!!!!!!!!!!!
//	    
//	    System.out.println("original dataset: " + dataset.toSummaryString());
//
//	    System.out.println("num rows in new dataset: " + datasetUnique.toSummaryString());
//	    
//	    // write data
//		try {
//			BufferedWriter writer = new BufferedWriter(new FileWriter(newFeaturesFile));
//			writer.write(datasetUnique.toString());
//			writer.flush();
//			writer.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//	    
//		
//	}
	
//	public static void main(String[] args) throws Exception {
//		
//		
//	}
}

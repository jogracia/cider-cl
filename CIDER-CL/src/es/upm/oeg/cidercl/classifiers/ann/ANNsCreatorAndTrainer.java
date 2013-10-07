package es.upm.oeg.cidercl.classifiers.ann;

import java.util.Enumeration;

import org.apache.log4j.Logger;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveRange;

/**
  Class to create and train the neural networks that will be used by CIDER-CL. 
  To be run from command line:
 <pre>
    ANNsCreatorAndTrainer [class_features_file prop_features_file class_ann prop_ann flag_balanced]
</pre>
	where: 
<pre>
	class_features_file is the file (.arff) that will contain the features data computed for classes (default: ./ANN/ClassFeaturesFile.arff)
	prop_features_file is the file (.arff) that will contain the features data computed for properties (default: ./ANN/PropFeaturesFile.arff)
	class_ann is the file (.model) containing the generated artificial neural network for classes (default: ./ANN/ANN_class.model)
	prop_ann is the file (.model) containing the generated artificial neural network for properties (default: ./ANN/ANN_prop.model)
	flag_balanced is a boolean indicating whether equal number of positive/negative alignments will be collected in the features files, or just everything it gets (default: false)
		
</pre>
	
  Example of usage: 
<pre>
  java -cp  cidercl.jar es.upm.oeg.cidercl.classifiers.ann.ANNsCreatorAndTrainer  "./ANN/ClassFeaturesFile.arff" "./ANN/PropFeaturesFile.arff" "./ANN/ANN_class.model" "./ANN/ANN_prop.model" "false"
</pre>
  
  @author Jorge Gracia
 
 */
public class ANNsCreatorAndTrainer {
	
	
	//default values
	static final String CLASS_FEATURES_FILE = "./ANN/ClassFeaturesFile.arff";
	static final String PROP_FEATURES_FILE = "./ANN/PropFeaturesFile.arff";
	static final String CLASS_ANN_FILE = "./ANN/ANN_class.model";
	static final String PROP_ANN_FILE = "./ANN/ANN_prop.model";
	
	String classFeaturesFile, propFeaturesFile, classAnnFile, propAnnFile;
	Boolean balancedFeaturesFiles = false; // this indicates whether there will be used equal number of positive/negative cases for training, or the whole dataset
	
	Instances dataset_class, dataset_prop;
	private static Logger log = Logger.getLogger(ANNsCreatorAndTrainer.class);
	
	
	public ANNsCreatorAndTrainer(){
		this.classFeaturesFile = CLASS_FEATURES_FILE;
		this.propFeaturesFile = PROP_FEATURES_FILE;
		this.classAnnFile = CLASS_ANN_FILE;
		this.propAnnFile = PROP_ANN_FILE;
		
	}
	
	public ANNsCreatorAndTrainer(String classFeaturesFile, String propFeaturesFile, String classAnnFile, String propAnnFile, Boolean balancedFeaturesFiles){
		this.classFeaturesFile = classFeaturesFile;
		this.propFeaturesFile = propFeaturesFile;
		this.classAnnFile = classAnnFile;
		this.propAnnFile = propAnnFile;
		this.balancedFeaturesFiles = balancedFeaturesFiles;
	}
	
	private Instances createBalancedFeatureFile(Instances dataset){
		
		Instances new_dataset = new Instances(dataset);
		int numPositiveAlign = 0;
		int numNegativeAlign = 0;
		
		//count class values
		Enumeration<?> enumInstances = (Enumeration<?>) new_dataset.enumerateInstances();
		
		// count number of cases in each class value (ref=1, ref=0)
		while(enumInstances.hasMoreElements()){
			Instance inst=(Instance) enumInstances.nextElement();
			if (inst.classValue() == 1.0d)
					numPositiveAlign++;
			else if (inst.classValue() == 0.0d)
					numNegativeAlign++;
		}

		log.info("positive reference align = " + numPositiveAlign);
		log.info("neg reference align = " + numNegativeAlign);
		
		// randomize data
		new_dataset.randomize(new_dataset.getRandomNumberGenerator(1));
	
		// sort data according to last attribute (reference alignment)
		new_dataset.sort(new_dataset.numAttributes()-1);
		
		// delete
		int limit = new_dataset.size() - Math.min(numPositiveAlign,numNegativeAlign)*2;
		RemoveRange rm_range = new RemoveRange();
		rm_range.setInstancesIndices("first-" + String.valueOf(limit));
		
		//filter
		try {
			rm_range.setInputFormat(new_dataset);
			new_dataset = Filter.useFilter(new_dataset,  rm_range);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		// randomize data again
		new_dataset.randomize(new_dataset.getRandomNumberGenerator(1));
			
		return new_dataset;
		
	}
	
	public void run(){
	

		log.info("Reading features files..." + this.classFeaturesFile + " and " + this.propFeaturesFile);		
		
		// read features data from files
		try {
			dataset_class = DataSource.read(classFeaturesFile);
			dataset_prop = DataSource.read(propFeaturesFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		log.info("Preprocesing data (randomizing, filtering)...");
		
		// uses the last attribute as class attribute
		if (dataset_class.classIndex() == -1)
			dataset_class.setClassIndex(dataset_class.numAttributes() - 1);
		if (dataset_prop.classIndex() == -1)
			dataset_prop.setClassIndex(dataset_prop.numAttributes() - 1);	

		
		// randomize data
		dataset_class.randomize(dataset_class.getRandomNumberGenerator(1));
		dataset_prop.randomize(dataset_prop.getRandomNumberGenerator(1));
	
		
		// filters (to remove the URIs attributes from the data) 
		Remove rm_uris_class = new Remove();
		Remove rm_uris_prop = new Remove();
		rm_uris_class.setAttributeIndicesArray(new int[] {0,1}); // remove 1st and 2nd attributes (URIs) and [TODO] attributes with less predictive power
		rm_uris_prop.setAttributeIndicesArray(new int[] {0,1}); // remove 1st and 2nd attributes (URIs)
		try {
			rm_uris_class.setInputFormat(dataset_class);
			rm_uris_prop.setInputFormat(dataset_prop);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		// apply filters 
		try {
			dataset_class = Filter.useFilter(dataset_class, rm_uris_class);
			dataset_prop = Filter.useFilter(dataset_prop, rm_uris_prop);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	
		
		if (balancedFeaturesFiles == true){
			log.info("Create balanced dataset...");
			dataset_class = createBalancedFeatureFile(dataset_class);
			dataset_prop = createBalancedFeatureFile(dataset_prop);
		}
		
		log.info("Creating ANNs..." + this.classAnnFile + " and " + this.propAnnFile);
				
		// define classifiers
		MultilayerPerceptron mlp_class = new MultilayerPerceptron();
		//mlp_class.setHiddenLayers("8,8");
		mlp_class.setAutoBuild(true); // configure the network automatically
		MultilayerPerceptron mlp_prop = new MultilayerPerceptron();
		//mlp_prop.setHiddenLayers("10,10");
		mlp_prop.setAutoBuild(true); // configure the network automatically
		
		log.info("Training ANNs...");
		
		// train and output model
		try {
			mlp_class.buildClassifier(dataset_class);
			mlp_prop.buildClassifier(dataset_prop);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		log.debug(mlp_class);
		log.debug(mlp_prop);

		log.info("Saving ANNs...");		
		
		// save classifier
		try {
			SerializationHelper.write(classAnnFile, mlp_class);
			SerializationHelper.write(propAnnFile, mlp_prop);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

   public void usage() {
			System.err.println("\n usage: ANNsCreatorAndTrainer [class_features_file prop_features_file class_ann prop_ann flag_balanced]");
			System.err.println("\t class_features_file is the file (.arff) that will contain the features data computed for classes (default: " + CLASS_FEATURES_FILE + ")");
			System.err.println("\t prop_features_file is the file (.arff) that will contain the features data computed for properties (default: " + PROP_FEATURES_FILE + ")");
			System.err.println("\t class_ann is the file (.model) containing the generated artificial neural network for classes (default: " + CLASS_ANN_FILE + ")");
			System.err.println("\t prop_ann is the file (.model) containing the generated artificial neural network for properties (default: " + PROP_ANN_FILE + ")");
			System.err.println("\t flag_balanced is a boolean indicating whether equal number of positive/negative alignments will be collected in the features files, or just everything it gets (default: false)");
    }

    public static void main(String[] args) throws Exception {
    	
    	ANNsCreatorAndTrainer ann = new ANNsCreatorAndTrainer();
    	if (args.length == 5) {
    		ann = new ANNsCreatorAndTrainer(args[0], args[1], args[2], args[3], Boolean.valueOf(args[4]));
    		//ann = new ANNsCreatorAndTrainer("./ANN/ClassFeaturesFile.arff", "./ANN/PropFeaturesFile.arff", "./ANN/ANN_class.model", "./ANN/ANN_prop.model", false);
    	    ann.run();
    	}
    	else{
       		ann.usage();
       		System.out.println("Some argument is missing (or it is wrong). Applying default values...");
       		ann.run();
    	}
    
    }
}

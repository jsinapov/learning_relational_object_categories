package exp;

import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

import learning.classification.features.ContextFeatureInstancesCreator;
import learning.classification.features.IContextInstancesCreator;
import learning.classification.labels.IClassLabelFunction;
import learning.classification.labels.LabelFunctionMR;
import learning.classification.model.CategoryRecognitionBM;
import learning.setclassification.SetGeneratorMR;
import learning.setclassification.data.SetDataPoint;
import learning.setclassification.features.SetInstancesGenerator;

import data.contextmodel.ContextDataDB;
import data.contextmodel.DataPointBM;
import data.contextmodel.DataUtils;
import data.contextmodel.InteractionTrial;

public class LearningVariationsEXP {

	final static String datapath = "/home/jsinapov/research/datasets/matrix_reasoning/features/";
	final static String [] behaviors = {"look","grasp","lift_slow","hold","shake","high_velocity_shake",
								"low_drop","tap","poke","push","crush"};
	final static String [] modalities = {"color","audio","proprioception"};
	
	
	public static void main(String[] args){
		// TODO Auto-generated method stub
		ArrayList<String[]> contexts = new ArrayList<String[]>();
		ArrayList<String> contexts_strings = new ArrayList<String>();
				
		for (int b = 0; b < behaviors.length;b++){
			for (int m = 0; m < modalities.length; m++){
				if (DataUtils.validCombination(behaviors[b],modalities[m])){
					String [] context = new String[2];
					context[0]=behaviors[b];
					context[1]=modalities[m];
					contexts.add(context);
					contexts_strings.add(DataUtils.contextString(context));
				}
			}		
		}
		System.out.println("Using "+contexts_strings.size()+" contexts: "+contexts_strings.toString());
				
		//load database
		ContextDataDB CDB = DataUtils.loadMatrixReasoningDB(datapath, contexts);
				
		//get objects
		ArrayList<String> objects = DataUtils.getObjectsMR();
		System.out.println("\t"+objects.size()+" objects: "+objects.toString());
		
		int num_tests = 1;
		int num_train = 5;
		int num_test = 5;
		int num_trials = 10;
		
		IClassLabelFunction LF = new LabelFunctionMR("weight");
		IContextInstancesCreator IC = new ContextFeatureInstancesCreator(LF);
		
		
		ArrayList<InteractionTrial> trials = DataUtils.generateTrialsMR(objects, contexts, num_trials);
		
		for (int t = 0; t < num_tests; t++){
			ArrayList<String> shuffled_objects = DataUtils.shuffleList(objects, t);
			
			ArrayList<String> train_objects = new ArrayList<String>();
			train_objects.addAll(shuffled_objects.subList(0, num_train));
			
			ArrayList<String> test_objects = new ArrayList<String>();
			test_objects.addAll(shuffled_objects.subList(objects.size()-num_test, objects.size()));
			System.out.println("Test "+t);
			System.out.println("\tTrain objects: "+train_objects);
			System.out.println("\tTrain objects: "+test_objects);
			
			//create classifier
			CategoryRecognitionBM CBM = new CategoryRecognitionBM(CDB,new J48(),LF,IC);
			
			ArrayList<InteractionTrial> train_trials = DataUtils.getTrialsWithObjects(trials, test_objects);
			ArrayList<InteractionTrial> test_trials = DataUtils.getTrialsWithObjects(trials, test_objects);
			
			CBM.train(train_trials);
			
			
			//create evaluation
			IContextInstancesCreator IC_temp = IC.copy();
			IC_temp.setContextInfo(contexts_strings.get(0),CDB.getContextFeatureDim(contexts_strings.get(0)));
			
			try {
				Evaluation EV = new Evaluation(IC_temp.generateHeader());
				
				for (int k = 0; k < test_trials.size(); k++){
					
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void varTest() {
		// TODO Auto-generated method stub
		ArrayList<String[]> contexts = new ArrayList<String[]>();
		ArrayList<String> contexts_strings = new ArrayList<String>();
		
		for (int b = 0; b < behaviors.length;b++){
			for (int m = 0; m < modalities.length; m++){
				if (DataUtils.validCombination(behaviors[b],modalities[m])){
					String [] context = new String[2];
					context[0]=behaviors[b];
					context[1]=modalities[m];
					contexts.add(context);
					contexts_strings.add(DataUtils.contextString(context));
				}
			}		
		}
		System.out.println("Using "+contexts_strings.size()+" contexts: "+contexts_strings.toString());
		
		//load database
		ContextDataDB CDB = DataUtils.loadMatrixReasoningDB(datapath, contexts);
		
		//get objects
		ArrayList<String> objects = DataUtils.getObjectsMR();
		System.out.println("\t"+objects.size()+" objects: "+objects.toString());
		
		//generate object sets
		SetGeneratorMR SG = new SetGeneratorMR();
		SG.setObjects(objects);
		
		//generate positive and negative data
		int rseed = 1;
		int num_tasks = 500;
		int num_per_set = 4;
		String property = "contents";
		ArrayList<SetDataPoint> full = SG.generateBalancedDataset(num_tasks, num_per_set, rseed, property);
		
		//setup instances creator
		ArrayList<String> class_values = new ArrayList<String>();
		class_values.add("-1");
		class_values.add("+1");
		
		SetInstancesGenerator IC = new SetInstancesGenerator(CDB,contexts_strings,class_values);
		IC.generateHeader();
		
		Instances data = IC.generateInstances(full);
		try {
			Evaluation EV = new Evaluation(data);
			
			Classifier C_base = new J48();
			AdaBoostM1 C = new AdaBoostM1();
			C.setClassifier(C_base);
			
			//PolyKernel K  = new PolyKernel(data,99721,3.0,false);
			/*RBFKernel K  = new RBFKernel(data,99721,5.0);
			SMO C = new SMO();
			C.setKernel(K);
			C.setC(100.0);*/
			//IBk C = new IBk(3);
			
			System.out.println("performing cross validation");
			EV.crossValidateModel(C, data, 10, new Random(1));
			
			System.out.println(EV.toClassDetailsString());
			System.out.println(EV.toMatrixString());
			System.out.println(EV.toSummaryString());
			
			J48 DT = new J48();
			DT.buildClassifier(data);
			System.out.println(DT.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*for (int t = 0; t < sample_tasks.size();t++){
			System.out.println(sample_tasks.get(t).toString());
		}*/
	}

}

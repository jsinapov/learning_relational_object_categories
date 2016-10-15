package learning.relational.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import utils.UtilsJS;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.functions.SGD;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import data.contextmodel.ContextDataDB;
import data.contextmodel.DataPointBM;
import data.contextmodel.DataUtils;
import data.contextmodel.InteractionTrial;

import learning.classification.eval.EvalDelegate;
import learning.classification.features.ContextFeatureInstancesCreator;
import learning.classification.labels.BinaryLabelFunctionMR;
import learning.classification.model.RelCatRecognitionBM;
import learning.memory.SensorimotorMemory;
import learning.relational.features.RelFeatureCreatorMR;
import learning.relational.features.RelInstancesCreator;
import learning.relational.labels.OracleMR;
import learning.relational.rep.RelDatapoint;

public class RelationalMultiLabelLearner {

	//the set of the currenty observed unique labels so far
	ArrayList<String> known_labels;
	ArrayList<String> label_schemas;
	
	ArrayList<String> labels_a1; //unary labels
	
	//map from labels to RelDatapoints that match that label
	public HashMap<String,ArrayList<RelDatapoint>> label_to_data_map;
	
	//map from label to list of objects associated with that label
	public HashMap<String,ArrayList<String>> label_to_objects_map; //not used
	
	//for each label we know, we store the negative examples here
	public HashMap<String,ArrayList<RelDatapoint>> label_to_data_map_negative;
	
	//here we store data based on the rel. schema
	public HashMap<String,ArrayList<RelDatapoint>> schema_to_data_map;
	
	//here we store classifiers for binary relations that use the output of classifier
	//for unary relations
	public HashMap<String,BinaryRelationModelLC> binary_label_to_label_classifier_map;
	//public HashMap<String,Instances> binary_label_to_label_inst_map;
	
	//list of classification models for each relation
	ArrayList<RelCatRecognitionBM> r_models;
	
	//memory used to story sensorimotor observation
	SensorimotorMemory SMM;
	
	//sensorimotor database
	ContextDataDB cDB;
	
	//Classifier to use
	Classifier C;
	
	//instances creator
	RelInstancesCreator RIC;
	
	//used for temporary storage
	ArrayList<double[]> distr_list;
	
	Random r;
	
	//oracle -- used only for generating negative examples
	
	public RelationalMultiLabelLearner(ContextDataDB db){
		binary_label_to_label_classifier_map = new HashMap<String,BinaryRelationModelLC>();
		//binary_label_to_label_inst_map = new HashMap<String,Instances>();
		
		label_to_data_map = new HashMap<String,ArrayList<RelDatapoint>>();
		label_to_data_map_negative = new HashMap<String,ArrayList<RelDatapoint>>();
		label_to_objects_map = new HashMap<String,ArrayList<String>> ();
		schema_to_data_map = new HashMap<String,ArrayList<RelDatapoint>>();
		known_labels = new ArrayList<String>();
		labels_a1 = new ArrayList<String>();
		label_schemas = new ArrayList<String>();
		r_models = new ArrayList<RelCatRecognitionBM>();
		SMM=new SensorimotorMemory();
		cDB=db;
		
		C = new J48();
		//C = new IBk(3);
		//C = new SGD();
		//((SGD)C).setLambda(0.1);
		
		//C = new AdaBoostM1();
		//((AdaBoostM1)C).setClassifier(new J48());
		
		//System.out.println("SGD:\t"+((SGD)C).getLambda() +" "+ ((SGD)C).getLambda());
		//C = new LWL();
		
		//C = new KStar();
		
		RIC = new RelInstancesCreator(new RelFeatureCreatorMR(),new OracleMR());
		r = new Random(1);
	}
	
	public RelInstancesCreator getRIC(){
		return RIC;
	}
	
	public double [] getLabelContextWeights(String label){
		int m_index = known_labels.indexOf(label);
		return r_models.get(m_index).getContextWeights();
	}
	
	public ArrayList<double[]> getDistrList(){
		return distr_list;
	}
	
	public ArrayList<String> getKnownLabels(){
		return known_labels;
	}
	
	public boolean hasDataForLabel(String label){
		return label_to_data_map.containsKey(label);
	}
	
	public ArrayList<RelDatapoint> getDataForLabel(String label){
		if (this.hasDataForLabel(label)){
			return label_to_data_map.get(label);
		}
		else {
			return null;
		}
	}
	
	public void addSensorimotorObservation(DataPointBM obs){
		SMM.addObservation(obs);
	}
	
	public void addSensorimotorObservations(ArrayList<DataPointBM> data){
		for (DataPointBM obs : data)
			SMM.addObservation(obs);
	}
	
	public void addTrial(InteractionTrial T){
		this.addSensorimotorObservations(cDB.getDataForTrial(T));
		SMM.addTrial(T);
	}
	
	public void addTrials(ArrayList<InteractionTrial> T_list){
		for (InteractionTrial T : T_list)
			this.addTrial(T);
	}
	
	public void printDebug(){
		System.out.println("Known labels: "+known_labels);
		System.out.println("Label schemas: "+label_schemas);
		
		for (String label : known_labels){
			System.out.println("Label:"+label);
			System.out.println(label_to_data_map_negative.get(label));
			System.out.println(label_to_data_map.get(label));
		}
	}
	
	public ArrayList<String> predictLabels(RelDatapoint R,  ArrayList<InteractionTrial> trials){
		 ArrayList<String> estimated_labels = new  ArrayList<String>();
		 
		 for (int i = 0; i < known_labels.size(); i++){
			
			 if (r_models.get(i).isTrained()){
				 if (R.getSchema().equals(label_schemas.get(i))){
					 
					 /*if (r_models.get(i).isTrained()){
						 double [] d = r_models.get(i).classify(test_data);
						 System.out.println(known_labels.get(i)+":\t"+d[0]+","+d[1]);
					 }*/
					 
					 double prob = this.getLabelProb(R,known_labels.get(i),trials);
					// System.out.println(known_labels.get(i)+":\t"+prob);
					 if (prob > 0.5)
						 estimated_labels.add(known_labels.get(i));
				 }
			 }
		 }
		 
		 return estimated_labels;
	}
	
	public ArrayList<String> predictLabelBasedLabels(RelDatapoint R,  ArrayList<InteractionTrial> trials){
		 ArrayList<String> estimated_labels = new  ArrayList<String>();
		 
		 for (int i = 0; i < known_labels.size(); i++){
			 if (label_schemas.get(i).equals("2 aa")){
				 double prob = this.getLabelBasedLabelProb(R, known_labels.get(i), trials);
				 if (prob > 0.5)
					 estimated_labels.add(known_labels.get(i));
			 }
		 }
		 
		 return estimated_labels;
	}
	
	public ArrayList<String> predictLabelsHybrid(RelDatapoint R,  ArrayList<InteractionTrial> trials){
		 ArrayList<String> estimated_labels = new  ArrayList<String>();
		 
		 for (int i = 0; i < known_labels.size(); i++){
			 if (label_schemas.get(i).equals("2 aa")){
				 double prob_lb = this.getLabelBasedLabelProb(R, known_labels.get(i), trials);
				 double prob_sm = this.getLabelProb(R,known_labels.get(i),trials);
					
				 
				 if (prob_lb+prob_sm > 1.0)
					 estimated_labels.add(known_labels.get(i));
			 }
		 }
		 
		 return estimated_labels;
	}
	
	//this assumes that trials will have at least 1 trial with each object in D
	public double getLabelProb(RelDatapoint R, String label, ArrayList<InteractionTrial> trials){
		double prob = 0.0;
		int model_index = known_labels.indexOf(label);

		
		if (r_models.get(model_index).isTrained()==false)
			return 0.0;
		
		ArrayList<String> objects = R.getAtomicElementIDs();
		
		if (R.getSchema().equals("1 a")){
			//simply go over each trial and add up the predictions
			
			//r_models.get(model_index).classify_multiple_contexts(D, R)
			double [] distr = new double[2];
			distr[0]=0.0;distr[1]=0.0;
			
			for (int i = 0; i < trials.size();i++){
				double [] distr_i = r_models.get(model_index).classify_multiple_contexts(cDB.getDataForTrial(trials.get(i)), R);
				distr[0]+=distr_i[0];
				distr[1]+=distr_i[1];
			}
			
			if (Utils.sum(distr) != 0) Utils.normalize(distr);
			prob = distr[1];
		}
		else if (R.getSchema().equals("2 aa")){
			ArrayList<InteractionTrial> trials_A = DataUtils.getTrialsWithObject(trials, objects.get(0));
			ArrayList<InteractionTrial> trials_B = DataUtils.getTrialsWithObject(trials, objects.get(1));
			
			double [] distr = new double[2];
			distr[0]=0.0;distr[1]=0.0;
			
			//System.out.println("Testing relation "+label+" on "+R);
			
			distr_list = new ArrayList<double[]>();
			
			for (int i = 0; i < trials_A.size(); i++){
				for (int j = 0; j < trials_B.size(); j++){
					ArrayList<DataPointBM> data_A = cDB.getDataForTrial(trials_A.get(i));
					ArrayList<DataPointBM> data_B = cDB.getDataForTrial(trials_B.get(j));
					ArrayList<DataPointBM> data_AB = new ArrayList<DataPointBM>();
					data_AB.addAll(data_A);
					data_AB.addAll(data_B);
					double [] distr_i = r_models.get(model_index).classify_multiple_contexts(data_AB, R);
					distr[0]+=distr_i[0];
					distr[1]+=distr_i[1];
					
				}
			}
			if (Utils.sum(distr) != 0.0)
				Utils.normalize(distr);
			prob=distr[1];
		}
		else if (R.getSchema().equals("1 s")){
			double [] distr = new double[2];
			distr[0]=0.0;distr[1]=0.0;
			ArrayList<String> set_objects = R.getAtomicElementIDs();
			int [] trials_counts = new int[set_objects.size()];
			HashMap<String,ArrayList<InteractionTrial>> trials_map = new HashMap<String,ArrayList<InteractionTrial>>();
			for (int k = 0; k < set_objects.size(); k ++){
				ArrayList<InteractionTrial> trials_k = DataUtils.getTrialsWithObject(trials,set_objects.get(k) );
				trials_map.put(set_objects.get(k), trials_k);
				trials_counts[k]=trials_k.size();
			}
			
			//generate a number of combinations and compute distribution
			int num_test_combos = 50;
			int num_possible_combos = 1;
			for (int k = 0; k < trials_counts.length;k++){
				num_possible_combos *= trials_counts[k];
			}
			
			if (num_possible_combos < num_test_combos)
				num_test_combos = num_possible_combos;
			
			for (int c = 0; c < num_test_combos; c++){
				ArrayList<InteractionTrial> trials_c = new ArrayList<InteractionTrial>();
				for (int k = 0; k < trials_counts.length;k++){
					trials_c.add(trials_map.get(set_objects.get(k)).get(r.nextInt(trials_counts[k])));
				}
				
				double [] distr_i = r_models.get(model_index).classify_multiple_contexts(cDB.getDataForTrials(trials_c), R);
				distr[0]+=distr_i[0];
				distr[1]+=distr_i[1];
			}
			
			if (Utils.sum(distr) != 0.0)
				Utils.normalize(distr);
			prob=distr[1];
			
			//ArrayList<DataPointBM> objects_tokens = cDB.getDataForTrials(objects_trials);
			
			/*HashMap<String,ArrayList<DataPointBM>> data_map = new HashMap<String,ArrayList<DataPointBM>>();
			for (int k = 0; k < set_objects.size(); k++)
				data_map.put(set_objects.get(k), DataUtils.getDataWithObject(objects_tokens, set_objects.get(k)));
			*/
		
		}
		
		return prob;
	}
	
	public double getLabelBasedLabelProb(RelDatapoint R, String label,ArrayList<InteractionTrial> trials){
		double prob = 0.0;
		
		if (R.getSchema().equals("2 aa")){
			ArrayList<String> objects = R.getAtomicElementIDs();
			ArrayList<InteractionTrial> trials_A = DataUtils.getTrialsWithObject(trials, objects.get(0));
			ArrayList<InteractionTrial> trials_B = DataUtils.getTrialsWithObject(trials, objects.get(1));
			
			prob = this.getLabelContextBinaryProb(label, R, trials_A, trials_B);
		}
		
		return prob;
	}
	
	//comphte the model's uncertainty about D
	public void uncertaintyScore(RelDatapoint R, ArrayList<InteractionTrial> trials){
		for (int i = 0; i < known_labels.size(); i++){
			if (r_models.get(i).isTrained()){
				if (R.getSchema().equals(label_schemas.get(i))){
					double prob = this.getLabelProb(R,known_labels.get(i),trials);
						 
				}
			}
		}
	}
	
	public void addRelDatapoint(RelDatapoint D, ArrayList<String> labels){
		//see if this kind of schema has been observed
		if (schema_to_data_map.containsKey(D.getSchema())){
			schema_to_data_map.get(D.getSchema()).add(D);
		}
		else {
			ArrayList<RelDatapoint> data = new ArrayList<RelDatapoint>();
			data.add(D);
			schema_to_data_map.put(D.getSchema(),data);
		}
		
		boolean [] update = new boolean[known_labels.size()];
		
		
		//see if the example should be added as a negative example for certian labels
		for (int i = 0; i < known_labels.size(); i++){
			update[i]=false;
			
			if (!labels.contains(known_labels.get(i))){
				//this data point could be a negative example for known_labels.get(i) IF it has the same schema
				if (D.getSchema().equals(label_schemas.get(i))){
					String target_label = known_labels.get(i);
					
					//check if we have negative data for this label
					if (label_to_data_map_negative.containsKey(target_label)){
						label_to_data_map_negative.get(target_label).add(D);
					}
					else {
						ArrayList<RelDatapoint> data = new ArrayList<RelDatapoint>();
						data.add(D);
						label_to_data_map_negative.put(target_label,data);
					}
					
					//add negative example to model
					r_models.get(i).addNegativeExample(D);
					update[i]=true;
				}
			}
		}
		
		
		for (int i = 0; i < labels.size(); i++){
			//label has been seen before
			if (label_to_data_map.containsKey(labels.get(i))){
				label_to_data_map.get(labels.get(i)).add(D);
				
				r_models.get(known_labels.indexOf(labels.get(i))).addPositiveExample(D);
				update[known_labels.indexOf(labels.get(i))]=true;
			}
			else {
				//label is new -- save the schema for it
				known_labels.add(labels.get(i));
				label_schemas.add(D.getSchema());
				
				if (D.getSchema().equals("1 a"))
					labels_a1.add(labels.get(i));
				
				//add new array list to data
				ArrayList<RelDatapoint> data = new ArrayList<RelDatapoint>();
				data.add(D);
				label_to_data_map.put(labels.get(i),data);
				
				
				RelCatRecognitionBM rC = new RelCatRecognitionBM(cDB,C,labels.get(i),RIC,D.getSchema());
				rC.addPositiveExample(D);
				
				//go through existing data and see if any should be used as negative examples
				ArrayList<RelDatapoint> negative = new ArrayList<RelDatapoint>();
				negative.addAll(schema_to_data_map.get(D.getSchema()));
				for (int p = 0; p < negative.size(); p++)
					if (negative.get(p) == D){
						negative.remove(p);
						break;
					}
				
				System.out.println("Adding negative data for new label "+labels.get(i)+":\t"+negative);
				for (RelDatapoint r : negative){
					rC.addNegativeExample(r);
				}
				label_to_data_map_negative.put(labels.get(i),negative);
				
				r_models.add(rC);
				
				//to do -- go through 
			} 
			

		}
		
		
		//re-train models if needed
		//retrainModels(update);
	}
	
	public void retrainModels(boolean [] update){
		if (update == null){
			update = new boolean[r_models.size()];
			for (int i = 0; i < update.length;i++){
				if (label_to_data_map_negative.get(known_labels.get(i)).size() > 0)
					update[i]=true;
				else update[i]=false;
			}
		}
		
		
		for (int i = 0; i < update.length;i++){
			if (update[i]==true){
				//System.out.println("\nUpdating label "+known_labels.get(i));
				//System.out.println(label_to_data_map_negative.get(known_labels.get(i)).size()+" negative:\t"+label_to_data_map_negative.get(known_labels.get(i)));
				//System.out.println(label_to_data_map.get(known_labels.get(i)).size()+" positive:\t"+label_to_data_map.get(known_labels.get(i)));
				
				//r_models.get(i).train2(SMM.getAllData());
				
				if (C instanceof UpdateableClassifier){

					if (r_models.get(i).isTrained() == false)
						r_models.get(i).train2(SMM.getAllData());
					else r_models.get(i).update(SMM.getAllData());
				}
				else {
					r_models.get(i).train2(SMM.getAllData());
				}
			}
		}
	}
	
	public double getLabelContextBinaryProb(String binary_label, RelDatapoint R, ArrayList<InteractionTrial> trials_A, ArrayList<InteractionTrial> trials_B){
		double [] probs_a = new double[labels_a1.size()];
		double [] probs_b = new double[labels_a1.size()];
		
		
		ArrayList<String> pair = R.getAtomicElementIDs();
		for (int l = 0; l < labels_a1.size(); l++){
			probs_a[l] = this.getLabelProb(RelDatapoint.generateUnaryDatapoint(pair.get(0)), labels_a1.get(l), trials_A);
			probs_b[l]= this.getLabelProb(RelDatapoint.generateUnaryDatapoint(pair.get(1)), labels_a1.get(l), trials_B);
		}
		
		double p = binary_label_to_label_classifier_map.get(binary_label).getProb(R, probs_a, probs_b);
		return p;
	}
	
	public void trainLabelContextBinaryModel(String binary_label){
		if (known_labels.contains(binary_label)){
			System.out.println("Learning label-based model for "+binary_label);
			
			int l_index = known_labels.indexOf(binary_label);
			
			//step 1: get the model and get it's positive and negative data
			ArrayList<RelDatapoint> model_data = new ArrayList<RelDatapoint>();
			ArrayList<String> class_labels = new ArrayList<String>();

			ArrayList<RelDatapoint> negative = r_models.get(l_index).getNegativeData();
			ArrayList<RelDatapoint> positive = r_models.get(l_index).getPositiveData();
			
			for (RelDatapoint r : negative){
				model_data.add(r);
				class_labels.add("-1");
			}
			
			for (RelDatapoint r : positive){
				model_data.add(r);
				class_labels.add("+1");
			}
			
			//step 2: create model and add all data
			BinaryRelationModelLC LM = new BinaryRelationModelLC(binary_label);
			LM.setUnaryLabels(this.labels_a1);
			LM.buildHeader();
			for (int i = 0; i < model_data.size(); i++){
				RelDatapoint r_i = model_data.get(i);
				ArrayList<String> pair = r_i.getAtomicElementIDs();
				double [] probs_a = new double[labels_a1.size()];
				double [] probs_b = new double[labels_a1.size()];

				for (int l = 0; l < labels_a1.size(); l++){
					probs_a[l] = this.getLabelProb(RelDatapoint.generateUnaryDatapoint(pair.get(0)), labels_a1.get(l), SMM.getTrialsWithObject(pair.get(0)));
					probs_b[l] = this.getLabelProb(RelDatapoint.generateUnaryDatapoint(pair.get(1)), labels_a1.get(l), SMM.getTrialsWithObject(pair.get(1)));
				}
				
				LM.addDatapoint(model_data.get(i), class_labels.get(i), probs_a, probs_b);
			}

			//step 3: train it
			LM.train();
			//if (LM.getNumInstances()>2)
			//	LM.crossValidate(LM.getNumInstances());
			
			//save it
			if (binary_label_to_label_classifier_map.containsKey(binary_label)){
				binary_label_to_label_classifier_map.remove(binary_label);
			}
			binary_label_to_label_classifier_map.put(binary_label,LM);
			
			//step 2: compute unary label probabilities for each object in each pair
			/*for (int i = 0; i < model_data.size(); i++){
				RelDatapoint r_i = model_data.get(i);
				ArrayList<String> pair = r_i.getAtomicElementIDs();
				
				//System.out.println(r_i.toString());
				
				double [] probs_a = new double[labels_a1.size()];
				double [] probs_b = new double[labels_a1.size()];
				double [] probs_diff = new double[labels_a1.size()];
				
				for (int l = 0; l < labels_a1.size(); l++){
					probs_a[l] = this.getLabelProb(RelDatapoint.generateUnaryDatapoint(pair.get(0)), labels_a1.get(l), SMM.getTrialsWithObject(pair.get(0)));
					probs_b[l]= this.getLabelProb(RelDatapoint.generateUnaryDatapoint(pair.get(1)), labels_a1.get(l), SMM.getTrialsWithObject(pair.get(1)));
					probs_diff[l]=probs_a[l]-probs_b[l];
				}
				
				label_features.add(probs_diff);
				
				//print
				for (int l = 0; l < labels_a1.size(); l++)
					System.out.print("\t"+probs_a[l]);
				System.out.println();
				
				for (int l = 0; l < labels_a1.size(); l++)
					System.out.print("\t"+probs_b[l]);
				System.out.println();
				
				for (int l = 0; l < labels_a1.size(); l++)
					System.out.print("\t"+probs_diff[l]);
				System.out.println();
			}
			
			//step 3: compute instances and train classifier
			Instances label_instances = UtilsJS.toInstances(label_features, class_labels, null);
			if (label_instances.numInstances()>5){
				try {
					EvalDelegate EV = new EvalDelegate(label_instances);
					EV.crossValidateModel(new J48(), label_instances, 5, new Random(1));
					System.out.println(EV.toClassDetailsString());
					System.out.println(EV.toSummaryString());
					System.out.println(EV.toMatrixString());
					
					J48 C_temp = new J48();
					C_temp.buildClassifier(label_instances);
					binary_label_to_label_classifier_map.put(binary_label, C_temp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		
		}
	}
}

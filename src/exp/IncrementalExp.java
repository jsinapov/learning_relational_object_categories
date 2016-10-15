package exp;

import java.util.ArrayList;
import java.util.Random;

import learning.classification.eval.EvalDelegate;
import learning.relational.features.RelInstancesCreator;
import learning.relational.labels.OracleMR;
import learning.relational.model.PerformanceTracker;
import learning.relational.model.RelationalMultiLabelLearner;
import learning.relational.rep.RelDatapoint;

import utils.CombinationGenerator;
import utils.DistUtils;
import utils.UtilsJS;
import weka.core.Utils;

import data.contextmodel.ContextDataDB;
import data.contextmodel.DataPointBM;
import data.contextmodel.DataUtils;
import data.contextmodel.InteractionTrial;

public class IncrementalExp {

	final static String datapath = "/home/jsinapov/research/datasets/matrix_reasoning/features/";
	
	
	final static String [] behaviors = {"look"/*"look","grasp","lift_slow","hold","shake","high_velocity_shake","low_drop","tap","poke","push","crush"*/};
	final static String [] modalities = {"patch","color","audio","proprioception"};
	
	final static String logPath = "/home/jsinapov/research/datasets/matrix_reasoning/logs/";
	
	public static void main(String[] args) {
		//computeLabelDistanceMatrix();
		exp();
	}
	
	public static void computeLabelDistanceMatrix(){
		String weights_file = new String(logPath+"temp_weights.txt");
		ArrayList<double[]> features = UtilsJS.loadFeaturesFromFile(weights_file);
		double [][] DM = new double[features.size()][features.size()];
		for (int i = 0; i < features.size();i++){
			for (int j = i; j < features.size(); j++){
				double v_ij = DistUtils.computeL2(features.get(i), features.get(j));
				DM[i][j]=v_ij;
				DM[j][i]=v_ij;
			}
		}
		
		UtilsJS.saveMatrix(DM, new String(logPath+"temp_labels_DM.txt"));
	}
	
	public static void exp() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		ArrayList<String[]> contexts = new ArrayList<String[]>();
		ArrayList<String> contexts_strings = new ArrayList<String>();
						
		for (int m = 0; m < modalities.length; m++){
			for (int b = 0; b < behaviors.length;b++){	
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
		
		//generate list of trials
		int num_trials = 10;
		ArrayList<InteractionTrial> trials = DataUtils.generateTrialsMR(objects, contexts, num_trials);
		
		//setup oracle
		OracleMR oracle = new OracleMR();
		
		//iterate over number of tests to be done
		int num_tests = 200;
		
		//setup start object sets
		int num_test_objects = 12;
		int max_num_train = 5;
		int start_test = 0;
		
		//store results here
		double [][][] kappas = new double[objects.size()-num_test_objects][oracle.getAllLabels().size()][num_tests];
		
		boolean doUnary = true;
		boolean doBinary = false;
		boolean doSets = false;
		int set_size = 3;
		
		boolean doLabelBased = false;
		
		ArrayList<String> possible_labels = new ArrayList<String>();
		if (doUnary)
			possible_labels.addAll(oracle.getLabelsForSchema("1 a"));
		if (doBinary)
			possible_labels.addAll(oracle.getLabelsForSchema("2 aa"));
		if (doSets)
			possible_labels.addAll(oracle.getLabelsForSchema("1 s"));
		//store results here for binary based label based models
		double [][][] kappas_lb = new double[objects.size()-num_test_objects][oracle.getLabelsForSchema("2 aa").size()][num_tests];
		double [][][] kappas_lb_hybrid = new double[objects.size()-num_test_objects][oracle.getLabelsForSchema("2 aa").size()][num_tests];
		
		ArrayList<String> labels_lb = new ArrayList<String>();	
		labels_lb.addAll(oracle.getLabelsForSchema("2 aa"));
		
		
		
		double [][][] weights = new double[possible_labels.size()][contexts.size()][num_tests];	
		boolean [] evaluated = new boolean[oracle.getAllLabels().size()];
			
		//setup averaged confusion matrices
		String [] color_props = {"brown","green","blue"};
		String [] weight_props = {"heavy","medium","light"};
		String [] contents_props = {"glass","screws","beans","rice"};
		double [][] CM_color;
		double [][] CM_weight;
		double [][] CM_contents;
		CM_color = new double[color_props.length][color_props.length];
		CM_weight = new double[weight_props.length][weight_props.length];
		CM_contents = new double[contents_props.length][contents_props.length];
		for (int i = 0; i < CM_color.length;i++)
			for (int j = 0; j < CM_color.length; j++)
				CM_color[i][j]=0.0;
		for (int i = 0; i < CM_weight.length;i++)
			for (int j = 0; j < CM_weight.length; j++)
				CM_weight[i][j]=0.0;
		for (int i = 0; i < CM_contents.length;i++)
			for (int j = 0; j < CM_contents.length; j++)
				CM_contents[i][j]=0.0;
		
		
		for (int test = 0; test < num_tests; test++){
			
			System.out.println("Current test = "+test);
			
			ArrayList<PerformanceTracker> evaluations = new ArrayList<PerformanceTracker>();
			ArrayList<PerformanceTracker> evaluations_lb = new ArrayList<PerformanceTracker>();
			ArrayList<PerformanceTracker> evaluations_lb_hybrid = new ArrayList<PerformanceTracker>();
			
			int seed = test+2;
			Random R = new Random(seed);
			
			//shuffle object order for test
			ArrayList<String> objects_t = UtilsJS.shuffle(objects, new Random(seed+1));
			
			
			ArrayList<String> known_objects = new ArrayList<String>();
			ArrayList<String> unknown_objects = new ArrayList<String>();
			ArrayList<String> test_objects = new ArrayList<String>();
			unknown_objects.addAll( objects_t.subList(0, objects_t.size()-num_test_objects));
			test_objects.addAll(objects_t.subList(objects_t.size()-num_test_objects,objects_t.size()));
			
			System.out.println(unknown_objects.size()+" unknown objects:\t"+unknown_objects);
			System.out.println(test_objects.size()+" test objects:\t"+test_objects);
			
			//setup learner
			RelationalMultiLabelLearner RMLL = new RelationalMultiLabelLearner(CDB);
			
			
			
			int counter = 0;
			
			
			//while there are remaining objects
			while (unknown_objects.size()>0){
				
				
				
				int o_index = R.nextInt(unknown_objects.size());
				String next_object = unknown_objects.get(o_index);
				unknown_objects.remove(o_index);
				
				System.out.println("\n\nAdding object "+(counter+1)+":\t"+next_object);
				
				//get trials with next object
				ArrayList<InteractionTrial> object_trials = DataUtils.getTrialsWithObject(trials, next_object);
				//ArrayList<DataPointBM> object_observations = CDB.getDataForTrials(object_trials);
				
				//robot now performs all behaviors
				System.out.println("Adding "+object_trials.size()+ " trials...");
				RMLL.addTrials(object_trials);
				
				//setup object for evaluation
				PerformanceTracker PT = new PerformanceTracker(oracle.getAllLabels(),RelInstancesCreator.getStaticHeader());
				
				//setup object for evaluation using labels
				PerformanceTracker PT_lb = new PerformanceTracker(oracle.getLabelsForSchema("2 aa"),RelInstancesCreator.getStaticHeader());
				PerformanceTracker PT_lb_hybrid = new PerformanceTracker(oracle.getLabelsForSchema("2 aa"),RelInstancesCreator.getStaticHeader());
				
				
				/*------ TRAIN ----- */
				
				/*---------------- UNARY LABELS -----------------*/
				if (doUnary){
					//generate next train data example for unary relation
					RelDatapoint D = RelDatapoint.generateUnaryDatapoint(next_object);
					ArrayList<String> single_object_labels = oracle.getLabels(D);
					
					//System.out.println("\nObject: "+next_object+"\tLabels:\t"+single_object_labels.toString());
					
					//test model before updating
					//ArrayList<String> object_labels_estimated = RMLL.predictLabels(D, object_trials);
	
					//add data point
					//System.out.println("Adding "+D +" as an example...");
					RMLL.addRelDatapoint(D, single_object_labels);
				}
				
				//shuffle train set of objects
				ArrayList<String> known_objects_copy = new ArrayList<String>();
				known_objects_copy.addAll(known_objects);
				known_objects_copy = UtilsJS.shuffle(known_objects_copy, new Random(counter*(test+1)));
				
				
				if (doBinary){
					/*---------------- BINARY LABELS ------------*/
					int num_train_pairs = 5;//how many training pairs involving the novel object do we do?
					
					//generate all potential new pair data points with the new object that have labels
					ArrayList<RelDatapoint> pair_candidates = new ArrayList<RelDatapoint>();
					for (int k = 0; k < known_objects_copy.size(); k++){
						RelDatapoint D_bin = RelDatapoint.generateBinaryObjectDatapoint(next_object, known_objects_copy.get(k));
						pair_candidates.add(D_bin);
					}
					
					//randomly add K of them to train set
					for (int k = 0; k < pair_candidates.size(); k++){
						RelDatapoint D_bin = pair_candidates.get(k);
						ArrayList<String> pair_labels = oracle.getLabels(D_bin);
						
						//System.out.println("\nLabels for "+D_bin.toString()+":\t"+pair_labels);
						
						//update model						
						RMLL.addRelDatapoint(D_bin, pair_labels);
						
						if (k >= num_train_pairs-1)
							break;
					}
				}
				
				if (doSets){
					int num_set_candidates = 250;
					int num_set_examples = 20;
					
					//check if we have enough for 
					if (known_objects_copy.size()+1 >= set_size){
						ArrayList<RelDatapoint> candidates = new ArrayList<RelDatapoint>();
						
						//check to see how many ways there are to pick set_size-1 objects from the known objects
						CombinationGenerator CG = new CombinationGenerator(known_objects_copy.size(),set_size-1);
						int num_possible = CG.getNumLeft().intValue();
						//System.out.println(CG.getNumLeft().intValue() +" ways to choose");
					
						int mod_k = (int)Math.ceil((double)CG.getNumLeft().intValue()/(double)num_set_candidates);
					
						for (int k = 0; k < num_possible; k++){
							int [] c_k = CG.getNext();
							if (k % mod_k == 0){
								ArrayList<String> set_k = new ArrayList<String>();
								for (int p = 0; p < c_k.length; p++)
									set_k.add(known_objects_copy.get(c_k[p]));
								set_k.add(next_object);
								RelDatapoint R_k = RelDatapoint.generateSetDatapoint(set_k);
								candidates.add(R_k);
								//ArrayList<String> labels_k = oracle.getLabels(R_k);
								//System.out.println(R_k.toString()+"\t"+labels_k);
							
								//update model						
								//RMLL.addRelDatapoint(R_k, labels_k);
							}
						}
						
						//from the candidates, select the ones that have mixed labels
						int num_added = 0;
						ArrayList<RelDatapoint> remaining_candidates = new ArrayList<RelDatapoint>();
						for (int k =0; k < candidates.size(); k++){
							ArrayList<String> labels_k = oracle.getLabels(candidates.get(k));
							if (labels_k.size() < oracle.getLabelsForSchema("1 s").size()){
								RMLL.addRelDatapoint(candidates.get(k), labels_k);
								num_added++;
							}
							else 
								remaining_candidates.add(candidates.get(k));
							
							if (num_added >= num_set_examples)
								break;
						}
						
						if (num_added < num_set_examples){
							for (int k = 0; k < remaining_candidates.size(); k++){
								ArrayList<String> labels_k = oracle.getLabels(remaining_candidates.get(k));
								RMLL.addRelDatapoint(remaining_candidates.get(k), labels_k);
								num_added++;
								if (num_added >= num_set_examples)
									break;
							}
						}
						
					}
				}
				
				if (counter >= start_test-1){
				
					//retrain models
					RMLL.retrainModels(null);
					
					if (doBinary && doLabelBased){
						ArrayList<String> b_labels = oracle.getLabelsForSchema("2 aa");
						for (String L : b_labels)
							RMLL.trainLabelContextBinaryModel(L);
					}
					
					//TEST 
					if (doUnary){
						//System.out.println("Testing on:\t"+test_objects);
						for (int t = 0; t < test_objects.size(); t++){
							ArrayList<InteractionTrial> test_object_trials = DataUtils.getTrialsWithObject(trials, (test_objects.get(t)));
							RelDatapoint D_t = RelDatapoint.generateUnaryDatapoint(test_objects.get(t));
							ArrayList<String> true_labels = oracle.getLabels(D_t);
							ArrayList<String> estimated_labels = RMLL.predictLabels(D_t, test_object_trials);
							
							PT.updateStats(estimated_labels, true_labels, oracle.getLabelsForSchema(D_t.getSchema()));
						
							//System.out.println("\tTest "+t+":\tpredicted="+estimated_labels+"\t\tactual="+true_labels);
						}
					}
	
					if (doBinary){
						for (int t = 0; t < test_objects.size(); t++){
							for (int s = 0; s < test_objects.size(); s++){
								if (s != t){
									RelDatapoint D_test = RelDatapoint.generateBinaryObjectDatapoint(test_objects.get(t), test_objects.get(s));
									ArrayList<String> true_labels = oracle.getLabels(D_test);
									ArrayList<String> pair_objects = D_test.getAtomicElementIDs();
	
									ArrayList<InteractionTrial> pair_trials = DataUtils.getTrialsWithObjects(trials, pair_objects);
									
									//test model grounded in sensorimotor data
									ArrayList<String> pair_labels_estimated = RMLL.predictLabels(D_test, pair_trials);
									PT.updateStats(pair_labels_estimated, true_labels, oracle.getLabelsForSchema(D_test.getSchema()));
									
									if (doLabelBased){
										//test model grounded in other labels
										ArrayList<String> pair_labels_estimated2 = RMLL.predictLabelBasedLabels(D_test, pair_trials);
										PT_lb.updateStats(pair_labels_estimated2, true_labels, oracle.getLabelsForSchema(D_test.getSchema()));
										
										//test hybrid
										ArrayList<String> pair_labels_estimated3 = RMLL.predictLabelsHybrid(D_test, pair_trials);
										PT_lb_hybrid.updateStats(pair_labels_estimated3, true_labels, oracle.getLabelsForSchema(D_test.getSchema()));
									
									}
								}
							}
						}
					}
					
					if (doSets){
						CombinationGenerator CG = new CombinationGenerator(test_objects.size(),set_size);
						int num_possible = CG.getNumLeft().intValue();
						//System.out.println(CG.getNumLeft().intValue() +" ways to choose "+set_size + " from " + test_objects.size());
					
						int num_test_set_examples = 50;
						int mod_k = (int)Math.ceil((double)CG.getNumLeft().intValue()/(double)num_test_set_examples);
					
						for (int k = 0; k < num_possible; k++){
							int [] c_k = CG.getNext();
							if (k % mod_k == 0){
								ArrayList<String> set_k = new ArrayList<String>();
								for (int p = 0; p < c_k.length; p++)
									set_k.add(test_objects.get(c_k[p]));
				
								RelDatapoint R_k = RelDatapoint.generateSetDatapoint(set_k);
								ArrayList<String> labels_k = oracle.getLabels(R_k);
								//System.out.println("Testing on " +R_k.toString()+"\t"+labels_k);
							
								ArrayList<InteractionTrial> set_trials = DataUtils.getTrialsWithObjects(trials, set_k);
								
								//query model
								ArrayList<String> set_labels_estimated = RMLL.predictLabels(R_k, set_trials);
								PT.updateStats(set_labels_estimated, labels_k, oracle.getLabelsForSchema(R_k.getSchema()));
								
							}
						}
						
					}
				}
				System.out.println("\nEvaluation using sensorimotor data:\n"+PT.toString());
				evaluations.add(PT);
				
				System.out.println("Test objects:\t"+test_objects);
				System.out.println("\nColor labels CM:");
				UtilsJS.printMatrix(PT.CM_color);
				System.out.println("\nWeights labels CM:");
				UtilsJS.printMatrix(PT.CM_weight);
				System.out.println("\nContents labels CM:");
				UtilsJS.printMatrix(PT.CM_contents);
				
				if (doLabelBased){
					System.out.println("\nEvaluation using unary labels:\n"+PT_lb.toString());
					evaluations_lb.add(PT_lb);
					
					System.out.println("\nEvaluation using unary labels + sm data:\n"+PT_lb_hybrid.toString());
					evaluations_lb_hybrid.add(PT_lb_hybrid);
				}
				
				//add object to set of known objects
				known_objects.add(next_object);
				
				counter++;
				
				if (known_objects.size() >= max_num_train){
					for (int i = 0; i < CM_color.length;i++)
						for (int j = 0; j < CM_color.length; j++)
							CM_color[i][j]+=PT.CM_color[i][j];
					for (int i = 0; i < CM_weight.length;i++)
						for (int j = 0; j < CM_weight.length; j++)
							CM_weight[i][j]+=PT.CM_weight[i][j];
					for (int i = 0; i < CM_contents.length;i++)
						for (int j = 0; j < CM_contents.length; j++)
							CM_contents[i][j]+=PT.CM_contents[i][j];
					
					
					break;
					
				}
			}
			
			//for each label, check the data
			//System.out.println(RMLL.getDataForLabel("brown"));
			RMLL.printDebug();
			
			//print evaulation
			ArrayList<String> eval_labels = evaluations.get(evaluations.size()-1).getLabels();
			System.out.println("Evaluation for: "+eval_labels);
			
			for (int i = 0;i < eval_labels.size(); i++){
				if (evaluations.get(evaluations.size()-1).getNumEvaluations(eval_labels.get(i)) > 0)
					evaluated[i]=true;
				else evaluated[i]=false;
			}
			
			for (int i = 0; i < evaluations.size();i++){
				ArrayList<EvalDelegate> ev_list_i = evaluations.get(i).getEvaluations();
				for (int p = 0; p < ev_list_i.size(); p++){
					System.out.print(ev_list_i.get(p).kappa()+"\t");
					kappas[i][p][test]=ev_list_i.get(p).kappa();
				}
				System.out.println("");
			}
			
			if (doLabelBased){
				System.out.println("\nLabel-based Evaluation for: "+labels_lb);
				for (int i = 0; i < evaluations_lb.size();i++){
					ArrayList<EvalDelegate> ev_list_i = evaluations_lb.get(i).getEvaluations();
					for (int p = 0; p < ev_list_i.size(); p++){
						System.out.print(ev_list_i.get(p).kappa()+"\t");
						kappas_lb[i][p][test]=ev_list_i.get(p).kappa();
					}
					System.out.println("");
				}
				
				System.out.println("\nLabel-based + SM data Evaluation for: "+labels_lb);
				for (int i = 0; i < evaluations_lb_hybrid.size();i++){
					ArrayList<EvalDelegate> ev_list_i = evaluations_lb_hybrid.get(i).getEvaluations();
					for (int p = 0; p < ev_list_i.size(); p++){
						System.out.print(ev_list_i.get(p).kappa()+"\t");
						kappas_lb_hybrid[i][p][test]=ev_list_i.get(p).kappa();
					}
					System.out.println("");
				}
				
				
			}
			
			//get context weights
			ArrayList<String> known_labels = RMLL.getKnownLabels();
			System.out.println("\nWeights for labels "+known_labels);
			System.out.println("contexts: "+contexts_strings);
			for (int i = 0;i < known_labels.size(); i++){
				double [] w_i = RMLL.getLabelContextWeights(known_labels.get(i));
				for (int j = 0; j < w_i.length; j++){
					if (j < w_i.length-1) System.out.print(w_i[j]+",");
					else System.out.print(w_i[j]+"\n");
					
					weights[possible_labels.indexOf(known_labels.get(i))][j][test]=w_i[j];
				}
			}
			
			//System.out.println("Weights for same_weight:");
			//UtilsJS.printMatrix(weights[possible_labels.indexOf("same_weight")]);
		}
		
		
		
		System.out.println("\n\nContext weights for labels:\t"+possible_labels);
		for (int i = 0; i < possible_labels.size(); i++){
			for (int j = 0; j < weights[i].length;j++){
				double w_cj = Utils.mean(weights[i][j]);
				if (j < weights[i].length-1) System.out.print(w_cj+",");
				else System.out.print(w_cj+"\n");
				
				
			}
			
			/*if (possible_labels.get(i).equals("same_weight")){
				System.out.println("\n\n");
				UtilsJS.printMatrix(weights[i]);
				System.out.println("\n\n");
			}*/
		}
		
		System.out.println("\n\nKappa performance vs. num. train objects:\n");
		ArrayList<String> all_labels = oracle.getAllLabels();
		for (int i= 0; i < all_labels.size(); i++)
			if (evaluated[i])
				System.out.print(all_labels.get(i)+"\t\t\t");
		
		System.out.println();
		for (int n = 0; n < kappas.length;n++){
			int num_objects = n+1;
			System.out.print(num_objects+"\t");
			for (int l = 0; l < kappas[n].length; l++){
				if (evaluated[l]){
					double mean_kappa = Utils.mean(kappas[n][l]);
					double var_kappa = Utils.variance(kappas[n][l]);
					System.out.print(mean_kappa+" "+var_kappa+"\t\t");
				}
			}
			System.out.println();
			
			if (num_objects >= max_num_train)
				break;
		}
		
		if (doBinary && doLabelBased){
			System.out.println("\n\nKappa performance vs. num. train objects for label-based models:\n");
	
			for (int i= 0; i < labels_lb.size(); i++)
				System.out.print(labels_lb.get(i)+"\t\t\t");
			
			System.out.println();
			for (int n = 0; n < kappas_lb.length;n++){
				int num_objects = n+1;
				System.out.print(num_objects+"\t");
				for (int l = 0; l < kappas_lb[n].length; l++){
					if (evaluated[l]){
						double mean_kappa = Utils.mean(kappas_lb[n][l]);
						double var_kappa = Utils.variance(kappas_lb[n][l]);
						System.out.print(mean_kappa+" "+var_kappa+"\t\t");
					}
				}
				System.out.println();
				
				if (num_objects >= max_num_train)
					break;
			}
			
			System.out.println("\n\nKappa performance vs. num. train objects for hybrid models:\n");
			
			for (int i= 0; i < labels_lb.size(); i++)
				System.out.print(labels_lb.get(i)+"\t\t\t");
			
			System.out.println();
			for (int n = 0; n < kappas_lb_hybrid.length;n++){
				int num_objects = n+1;
				System.out.print(num_objects+"\t");
				for (int l = 0; l < kappas_lb_hybrid[n].length; l++){
					if (evaluated[l]){
						double mean_kappa = Utils.mean(kappas_lb_hybrid[n][l]);
						double var_kappa = Utils.variance(kappas_lb_hybrid[n][l]);
						System.out.print(mean_kappa+" "+var_kappa+"\t\t");
					}
				}
				System.out.println();
				
				if (num_objects >= max_num_train)
					break;
			}
		}
		
		System.out.println("\nColor labels CM:");
		
		for (int i = 0; i < CM_color.length;i++){
			double row_i_sum = Utils.sum(CM_color[i]);
			if (row_i_sum > 0.0){
				for (int j = 0; j < CM_color.length; j++){
					CM_color[i][j] = CM_color[i][j] / row_i_sum;
				}
			}
			else {
				for (int j = 0; j < CM_color.length; j++)
					CM_color[i][j] = 1.0/(double)CM_color.length;
			}
		}
		UtilsJS.printMatrix(CM_color);
		
		System.out.println("\nWeights labels CM:");
		for (int i = 0; i < CM_weight.length;i++){
			double row_i_sum = Utils.sum(CM_weight[i]);
			if (row_i_sum > 0.0){
				for (int j = 0; j < CM_weight.length; j++){
					CM_weight[i][j] = CM_weight[i][j] / row_i_sum;
				}
			}
			else {
				for (int j = 0; j < CM_weight.length; j++)
					CM_weight[i][j] = 1.0/(double)CM_weight.length;
			}
		}
		UtilsJS.printMatrix(CM_weight);
		
		System.out.println("\nContents labels CM:");
		for (int i = 0; i < CM_contents.length;i++){
			double row_i_sum = Utils.sum(CM_contents[i]);
			if (row_i_sum > 0.0){
				for (int j = 0; j < CM_contents.length; j++){
					CM_contents[i][j] = CM_contents[i][j] / row_i_sum;
				}
			}
			else {
				for (int j = 0; j < CM_contents.length; j++)
					CM_contents[i][j] = 1.0/(double)CM_contents.length;
			}
		}
		UtilsJS.printMatrix(CM_contents);
		
		//save them
		String save_cm_path = "/home/jsinapov/research/datasets/matrix_reasoning/logs/pomdp_observation_models/train5_tests200";
		UtilsJS.saveMatrixComma(CM_color, new String(save_cm_path+"/"+behaviors[0]+"_color_model.txt"));
		UtilsJS.saveMatrixComma(CM_weight, new String(save_cm_path+"/"+behaviors[0]+"_weight_model.txt"));
		UtilsJS.saveMatrixComma(CM_contents, new String(save_cm_path+"/"+behaviors[0]+"_contents_model.txt"));
		
	
	}

}

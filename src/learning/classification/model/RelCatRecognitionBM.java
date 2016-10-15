package learning.classification.model;

import java.util.ArrayList;
import java.util.Random;

import learning.classification.eval.EvalDelegate;
import learning.classification.eval.EvaluationJS;
import learning.classification.features.IContextInstancesCreator;
import learning.classification.labels.IClassLabelFunction;
import learning.relational.features.RelInstancesCreator;
import learning.relational.rep.AtomicElement;
import learning.relational.rep.RelDatapoint;
import learning.relational.rep.SetElement;
import learning.relational.rep.iface.Element;

import utils.UtilsJS;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import data.contextmodel.ContextDataDB;
import data.contextmodel.DataPointBM;
import data.contextmodel.DataUtils;
import data.contextmodel.InteractionTrial;

public class RelCatRecognitionBM {

	ArrayList<String> contexts;
	ArrayList<Classifier> c_list;
	ArrayList<EvalDelegate> e_list;
	ArrayList<RelInstancesCreator> ic_list;
	Classifier C_base;
	
	ContextDataDB cDB;
	String label;
	RelInstancesCreator IC;
	
	ArrayList<RelDatapoint> positive;
	ArrayList<RelDatapoint> negative;
	ArrayList<RelDatapoint> positive_update;
	ArrayList<RelDatapoint> negative_update;
	ArrayList<String> objects_observed;
	
	boolean trained = false;
	
	double [] weights;
	
	public RelCatRecognitionBM(ContextDataDB db, Classifier C,
								String l, RelInstancesCreator ic_input, String schema){
		this.setContexts(db);
		
		objects_observed = new ArrayList<String>();
		positive = new ArrayList<RelDatapoint>();
		negative = new ArrayList<RelDatapoint>();
		
		positive_update = new ArrayList<RelDatapoint>();
		negative_update = new ArrayList<RelDatapoint>();
		
		label = l;
		IC=ic_input;
		
		try {
			c_list = new ArrayList<Classifier>();
			e_list = new ArrayList<EvalDelegate>();
			ic_list = new ArrayList<RelInstancesCreator>();
			C_base = AbstractClassifier.makeCopy(C);
			
			for (int c = 0; c < contexts.size(); c++){
				Classifier M_c = AbstractClassifier.makeCopy(C_base);
				c_list.add(M_c);
				
				RelInstancesCreator IC_c = IC.copy();
				IC_c.setInfo(contexts.get(c), db.getContextFeatureDim(contexts.get(c)),schema,label);
				IC_c.generateHeader();
				ic_list.add(IC_c);
				
				e_list.add(new EvalDelegate(IC_c.getHeader()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public ArrayList<RelDatapoint> getNegativeData(){
		return negative;
	}
	
	public ArrayList<RelDatapoint> getPositiveData(){
		return positive;
	}
	
	public double [] getContextWeights(){
		return weights;
	}
	
	//assumption -- there is only 1 DataPointBM per unique object in R
	public double [] classify_single_context(ArrayList<DataPointBM> data, RelDatapoint R){
		int context_index = contexts.indexOf(data.get(0).getContextName());
		
		if (context_index != -1){
			Instance inst = ic_list.get(context_index).computeInstance(R,data,null);
			
			//if (context_index == 1)
				//System.out.println("Instance:\t"+inst);
			
			try {
				double [] distr = c_list.get(context_index).distributionForInstance(inst);
				//if (context_index == 1)
					//System.out.println(distr[0]+"\t"+distr[1]);
				return distr;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		double [] distr = new double[2];
		for (int i = 0; i < distr.length; i++)
			distr[i]=0.0;
		return distr;
	}
	
	//assumes: 1 trial of data for each object in R
	public double [] classify_multiple_contexts(ArrayList<DataPointBM> D, RelDatapoint R){
		double [] distr = new double[2];
		for (int i = 0; i < distr.length; i++)
			distr[i]=0.0;
		
		//System.out.println("Contexts:\t"+contexts);
		for (int c=0; c < contexts.size(); c++){
			ArrayList<DataPointBM> D_c = DataUtils.getDataFromContext(D, contexts.get(c));
			
			double [] distr_c = this.classify_single_context(D_c, R);
			
			double weight = 1.0;
			if (weights != null){
				weight = Math.max(weights[c],0.0);
			}
			for (int i = 0; i < distr.length; i++)
				distr[i]+=weight*distr_c[i];
		}
		
		if (Utils.sum(distr) != 0){
		
			Utils.normalize(distr);
		}
		
		//System.out.println("\t"+distr[0]+"\t"+distr[1]);
		
		return distr;
	}
	
	
	public void update(ArrayList<DataPointBM> data){
		
		System.out.println("Updating model with positive: "+positive_update+" and negative: "+negative_update);
		trained=true;
		
		for (int c = 0; c < contexts.size();c++){
			weights[c]=0.0;
			//System.out.print(contexts.get(c));
			ArrayList<DataPointBM> data_c = DataUtils.getDataFromContext(data, contexts.get(c));
			Instances train_update_data = ic_list.get(c).computeInstances(data_c,positive_update,negative_update);
		
			//System.out.println(data_c.size()+"\t..."+train_update_data.numInstances());
			
			for (int i = 0; i < train_update_data.numInstances();i++){
				try {
					//get prediction
					double [] distr = c_list.get(c).distributionForInstance(train_update_data.instance(i));
					int pred_class = Utils.maxIndex(distr);
					int actual_class = IC.getClassValues().indexOf(train_update_data.instance(i).stringValue(train_update_data.classAttribute()));
					e_list.get(c).evaluateModelOnce(distr, train_update_data.instance(i));
					
					
					((UpdateableClassifier)c_list.get(c)).updateClassifier(train_update_data.instance(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		//do cross validation
		positive.addAll(positive_update);
		positive_update.clear();
		negative.addAll(negative_update);
		negative_update.clear();

		for (int i = 0; i < e_list.size();i++){
			System.out.print(e_list.get(i).kappa()+" "+e_list.get(i).numInstances()+"\t");
			if (e_list.get(i).numInstances()>0)
				weights[i]=e_list.get(i).kappa();
			else weights[i]=1.0;
		}
		System.out.println();

	}
	
	public void train2(ArrayList<DataPointBM> data){
		
		weights = new double[contexts.size()];
		
		if (positive.size() == 0 || negative.size() == 0)
			return;
		
		for (int c = 0; c < contexts.size();c++){
			weights[c]=0.0;
			//System.out.print(contexts.get(c));
			ArrayList<DataPointBM> data_c = DataUtils.getDataFromContext(data, contexts.get(c));
			Instances train_data = ic_list.get(c).computeInstances(data_c,positive,negative);
			Instances train_data_update = ic_list.get(c).computeInstances(data_c,positive_update,negative_update);
			
			//classify new data to update context weights
			for (int i = 0; i < train_data_update.numInstances();i++){
				try {
					//get prediction
					double [] distr = c_list.get(c).distributionForInstance(train_data_update.instance(i));
					int pred_class = Utils.maxIndex(distr);
					int actual_class = IC.getClassValues().indexOf(train_data_update.instance(i).stringValue(train_data_update.classAttribute()));
					e_list.get(c).evaluateModelOnce(distr, train_data_update.instance(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			train_data.addAll(train_data_update);
			
			//System.out.println(train_data.toString());
			
			//train_data = UtilsJS.balanceDataset(train_data, "+1");
			
			//System.out.print(train_data.numInstances()+","+train_data_update.numInstances()+"\t");
			try {
				c_list.get(c).buildClassifier(train_data);
				trained=true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		positive.addAll(positive_update);
		negative.addAll(negative_update);
		positive_update.clear();
		negative_update.clear();
		
		//System.out.println();
		
		for (int i = 0; i < e_list.size();i++){
			//System.out.print(e_list.get(i).kappa()+" "+e_list.get(i).numInstances()+"\t");
			if (e_list.get(i).numInstances()>0)
				weights[i]=e_list.get(i).kappa();
			else weights[i]=1.0;
		}
		//System.out.println();
	}
	
	public void setContexts(ContextDataDB db){
		contexts = db.getContextsNames();
		cDB=db;
	}
	
	public boolean isTrained(){
		return trained;
	}

	public void addPositiveExample(RelDatapoint D){
		updateObservedObjects(D);
		if (!trained){
			positive.add(D);
		}
		else {
			positive_update.add(D);
		}
	}
	
	public void addNegativeExample(RelDatapoint D){
		updateObservedObjects(D);
		if (!trained){
			negative.add(D);
		}
		else {
			negative_update.add(D);
		}
	}
	
	public void addExample(RelDatapoint D, String label){
		if (label.equals("+1"))
			this.addPositiveExample(D);
		else this.addNegativeExample(D);
		
		
	}
	
	public void updateObservedObjects(RelDatapoint D){
		ArrayList<String> objects_in_D = new ArrayList<String>();
		ArrayList<Element> r_list = D.getElements();
		for (int i = 0; i < r_list.size(); i++){
			if (r_list.get(i).isSet()){
				SetElement t = (SetElement)r_list.get(i);
				ArrayList<String> t_ids = t.getSetIDs();
				for (String id_k : t_ids){
					if (!objects_in_D.contains(id_k)){
						objects_in_D.add(id_k);
					}
				}
			}
			else {
				AtomicElement t = (AtomicElement)r_list.get(i);
				if (!objects_in_D.contains(t.getID())){
					objects_in_D.add(t.getID());
				}
			}
		}
		
		//System.out.println("Observed objects: " + objects_in_D +" in data point "+D);
		
		for (int i = 0; i < objects_in_D.size();i++){
			if (!objects_observed.contains(objects_in_D.get(i))){
				objects_observed.add(objects_in_D.get(i));
			}
		}
	}
}

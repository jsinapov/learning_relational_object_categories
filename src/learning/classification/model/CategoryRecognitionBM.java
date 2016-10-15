package learning.classification.model;

import java.util.ArrayList;
import java.util.Random;

import learning.classification.eval.EvaluationJS;
import learning.classification.features.IContextInstancesCreator;
import learning.classification.labels.IClassLabelFunction;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import data.contextmodel.ContextDataDB;
import data.contextmodel.DataPointBM;
import data.contextmodel.DataUtils;
import data.contextmodel.InteractionTrial;

public class CategoryRecognitionBM {

	ArrayList<String> contexts;
	ArrayList<Classifier> c_list;
	ArrayList<IContextInstancesCreator> ic_list;
	Classifier C_base;
	
	ContextDataDB cDB;
	IClassLabelFunction LF;
	IContextInstancesCreator IC;
	
	double [] weights;
	
	public CategoryRecognitionBM(ContextDataDB db, Classifier C,
								IClassLabelFunction l, IContextInstancesCreator ic_input){
		this.setContexts(db);
		
		
		
		LF=l;
		IC=ic_input;
		
		try {
			c_list = new ArrayList<Classifier>();
			ic_list = new ArrayList<IContextInstancesCreator>();
			C_base = AbstractClassifier.makeCopy(C);
			
			for (int c = 0; c < contexts.size(); c++){
				Classifier M_c = AbstractClassifier.makeCopy(C_base);
				c_list.add(M_c);
				
				IContextInstancesCreator IC_c = IC.copy();
				IC_c.setContextInfo(contexts.get(c), db.getContextFeatureDim(contexts.get(c)));
				IC_c.generateHeader();
				ic_list.add(IC_c);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public double [] classify(DataPointBM d){
		int context_index = contexts.indexOf(d.getContextName());
		
		if (context_index != -1){
			Instance inst = ic_list.get(context_index).computeInstance(d);
			try {
				double [] distr = c_list.get(context_index).distributionForInstance(inst);
				return distr;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		double [] distr = new double[LF.getLabelSet().size()];
		for (int i = 0; i < distr.length; i++)
			distr[i]=0.0;
		return distr;
	}
	
	public double [] classify(ArrayList<DataPointBM> D){
		double [] distr = new double[LF.getLabelSet().size()];
		for (int i = 0; i < distr.length; i++)
			distr[i]=0.0;
		
		for (int c = 0; c < D.size(); c++){
			
			
			
			double [] distr_c = this.classify(D.get(c));
			int c_index = contexts.indexOf(D.get(c).getContextName());
			double weight = 1.0;
			if (weights != null){
				weight = Math.max(weights[c_index],0.0);
			}
			
			for (int i = 0; i < distr.length; i++)
				distr[i]+=weight*distr_c[i];
		}
		
		Utils.normalize(distr);
		return distr;
	}
	
	public void train(ArrayList<InteractionTrial> trials){
		for (int c = 0; c < contexts.size();c++){
			ArrayList<DataPointBM> data_c = DataUtils.getContextData(trials, cDB.getConextData(contexts.get(c)), contexts.get(c));
			
			Instances train_data = ic_list.get(c).computeInstances(data_c);
			//System.out.println(train_data);
			try {
				c_list.get(c).buildClassifier(train_data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//function for training unary labels
	public void train2(ArrayList<DataPointBM> data){
		weights = new double[contexts.size()];
		
		for (int c = 0; c < contexts.size();c++){
			weights[c]=0.0;
			//System.out.print(contexts.get(c));
			ArrayList<DataPointBM> data_c = DataUtils.getDataFromContext(data, contexts.get(c));
			Instances train_data = ic_list.get(c).computeInstances(data_c);
			//System.out.println(train_data);
			try {
				c_list.get(c).buildClassifier(train_data);
				
				EvaluationJS Ev = new EvaluationJS(train_data);
				if (train_data.numInstances()>5){
					Ev.crossValidateModel(C_base, train_data, 5, new Random());
					weights[c]=Ev.kappa();
				}
				else {
					weights[c]=1.0;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < weights.length; i++)
			System.out.print(weights[i]+"\t");
		System.out.println();
	}
	
	public void setContexts(ContextDataDB db){
		contexts = db.getContextsNames();
		cDB=db;
	}
	
	
}

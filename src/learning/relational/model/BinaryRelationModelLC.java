package learning.relational.model;

import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import learning.classification.eval.EvalDelegate;
import learning.relational.rep.RelDatapoint;

public class BinaryRelationModelLC {

	String label;
	ArrayList<String> label_features;
	Instances header;
	Classifier C;
	
	public BinaryRelationModelLC(String l){
		label = l;
		
		label_features = new ArrayList<String>();
		
		C = new J48();
	}
	
	public int getNumInstances(){
		return header.numInstances();
	}
	
	public void setUnaryLabels(ArrayList<String> u_labels){
		label_features.addAll(u_labels);
	}
	
	public double [] constructFeatures(double [] probs_a, double [] probs_b){
		
		double [] f = new double[probs_a.length*probs_b.length];
		int i = 0;
		for (int a = 0; a < probs_a.length; a++){
			for (int b = 0; b < probs_b.length;b++){
				f[i]=probs_a[a]-probs_b[b];
				i++;
			}
		}
		
		//difference features
		/*double [] f = new double[probs_a.length];
		for (int i = 0; i < probs_a.length; i++){
			f[i]=probs_a[i]-probs_b[i];
		}*/
		
		//raw features
		/*double [] f = new double[probs_a.length*2];
		for (int i = 0; i < probs_a.length; i++){
			f[i]=probs_a[i];
		}
		for (int i = 0; i < probs_b.length; i++){
			f[i+probs_b.length]=probs_b[i];
		}*/
		
		return f;
	}
	
	public void buildHeader(){
		ArrayList<Attribute> attrInfo = new ArrayList<Attribute>();
		
		//input attributes
		for (int a = 0; a <label_features.size(); a++){
			for (int b = 0; b < label_features.size();b++){
				attrInfo.add(new Attribute(new String(label_features.get(a)+"_"+label_features.get(b))));
			}
		}
		
		/*for (int i = 0; i < label_features.size();i++){
			Attribute a = new Attribute(new String("diff_"+label_features.get(i)));
			attrInfo.add(a);
		}*/
		
		/*for (int i = 0; i < label_features.size();i++)
			attrInfo.add(new Attribute(new String(label_features.get(i)+"_A")));
		for (int i = 0; i < label_features.size();i++)
			attrInfo.add(new Attribute(new String(label_features.get(i)+"_B")));*/
			
		
		//class attribute
		ArrayList<String> class_vals = new ArrayList<String>();
		class_vals.add("-1");
		class_vals.add("+1");
		Attribute a_class = new Attribute(new String("class"),class_vals);
		attrInfo.add(a_class);
				
		//compute instances
		header= new Instances("data", attrInfo, 0);
		header.setClassIndex(header.numAttributes()-1);
	}
	
	public void train(){
		try {
			//System.out.println(header.toString());
			
			C = new SMO();
			Kernel K;
			try {
				K = new PolyKernel(header,250007,2.0,false);
				((SMO)C).setKernel(K);
				((SMO)C).setBuildLogisticModels(true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			C.buildClassifier(header);
			//System.out.println(((J48)C).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void crossValidate(int numFolds){
		EvalDelegate EV;
		try {
			EV = new EvalDelegate(header);
			EV.crossValidateModel(new J48(), header, numFolds, new Random(1));
			//System.out.println(EV.toClassDetailsString());
			System.out.println(EV.toSummaryString());
			System.out.println(EV.toMatrixString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public double getProb(RelDatapoint R, double [] probs_a, double [] probs_b){
		double [] f_r = this.constructFeatures(probs_a, probs_b);
		Instance inst_i = new DenseInstance(header.numAttributes());
		inst_i.setDataset(header);
		
		for (int j = 0; j < f_r.length; j++)
			inst_i.setValue(j, f_r[j]);
		inst_i.setClassMissing();
		
		try {
			double [] distr = C.distributionForInstance(inst_i);
			return distr[1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public void addDatapoint(RelDatapoint R, String classlabel, double [] probs_a, double [] probs_b){
		double [] f_r = this.constructFeatures(probs_a, probs_b);
	
		Instance inst_i = new DenseInstance(header.numAttributes());
		inst_i.setDataset(header);
		
		for (int j = 0; j < f_r.length; j++)
			inst_i.setValue(j, f_r[j]);
		
		if (classlabel.equals("?"))
			inst_i.setClassMissing();
		else
			inst_i.setClassValue(classlabel);
		
		header.add(inst_i);
	 
	}
	
}

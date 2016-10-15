package learning.relational.model;

import java.util.ArrayList;

import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;

import data.contextmodel.ContextDataDB;
import data.contextmodel.DataPointBM;
import data.contextmodel.DataUtils;

import learning.classification.features.IContextInstancesCreator;
import learning.classification.labels.IClassLabelFunction;
import learning.classification.model.CategoryRecognitionBM;
import learning.relational.rep.AtomicElement;
import learning.relational.rep.RelDatapoint;
import learning.relational.rep.SetElement;
import learning.relational.rep.iface.Element;

public class BinaryModelBM {

	ContextDataDB cDB;
	
	ArrayList<RelDatapoint> positive;
	ArrayList<RelDatapoint> negative;
	String r_schema;
	String label;
	ArrayList<String> objects_observed;
	
	//things for unary relations over individual objects
	CategoryRecognitionBM C_cat;
	IClassLabelFunction LF;
	IContextInstancesCreator IC;
	
	boolean isTrained=false;
	
	public BinaryModelBM(String s, String l, ContextDataDB db){
		objects_observed = new ArrayList<String>();
		positive = new ArrayList<RelDatapoint>();
		negative = new ArrayList<RelDatapoint>();
		r_schema=s;
		label=l;
		cDB=db;
	}
	
	public boolean isTrained(){
		return isTrained;
	}
	
	public void setClassifcationParams(IClassLabelFunction LF_unary, IContextInstancesCreator inst_c){
		LF=LF_unary;
		IC=inst_c;
	}
	
	public void initialize(){
		//check schema and initialize different things
		/*if (r_schema.equals("1 a")){
			C_cat = new CategoryRecognitionBM(cDB,new J48(),LF,IC);
		}*/
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
	
	public void addPositiveExample(RelDatapoint D){
		positive.add(D);
		updateObservedObjects(D);
	}
	
	public void addNegativeExample(RelDatapoint D){
		negative.add(D);
		updateObservedObjects(D);
	}
	
	public void addExample(RelDatapoint D, String label){
		if (label.equals("+1"))
			this.addPositiveExample(D);
		else this.addNegativeExample(D);
		
		updateObservedObjects(D);
	}
	
	public double [] classify(ArrayList<DataPointBM> object_observations){
		double [] d = {0.0,0.0};
		
		/*if (r_schema.equals("1 a")){ //category over object
			d = C_cat.classify(object_observations);
		}*/
		
		return d;
	}
	
	public void train(ArrayList<DataPointBM> sm_data){
		isTrained=true;
		
		/*if (r_schema.equals("1 a")){ //category over object
			C_cat.train2(DataUtils.getDataWithObjects(sm_data,objects_observed));
		}*/
	}
	
}

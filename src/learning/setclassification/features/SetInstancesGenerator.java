package learning.setclassification.features;

import java.util.ArrayList;

import learning.setclassification.data.SetDataPoint;

import utils.DistUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Utils;

import data.contextmodel.ContextDataDB;

public class SetInstancesGenerator {

	ContextDataDB CDB;
	ArrayList<String> contexts;
	ArrayList<String> classValues;
	
	Instances header;
	
	public SetInstancesGenerator(ContextDataDB c, ArrayList<String> ctxs, ArrayList<String> cv){
		CDB = c;
		contexts = ctxs;
		classValues=cv;
	}
	
	public Instance generateInstance(SetDataPoint dp){
		ArrayList<String> objects = dp.getObjects();
		String cv = dp.getClassValue();
		
		double [] f = new double[2*contexts.size()];
		
		int num_trials = 10;
		
		for (int c = 0; c < contexts.size(); c++){
			//measure the variation in context c using all 10 trials
			
			ArrayList<Double> observed_differences = new ArrayList<Double>();
			
			for (int i = 0; i < objects.size(); i++){
				for (int j = i+1; j < objects.size(); j++){
					String object_i = objects.get(i);
					String object_j = objects.get(j);
					
					for (int ti = 0; ti < num_trials; ti++){
						for (int tj=ti; tj < num_trials; tj++){
							double [] a = CDB.getDataPoint(contexts.get(c), object_i, ti).getFeatures();
							double [] b = CDB.getDataPoint(contexts.get(c), object_j, tj).getFeatures();
							double l2norm = DistUtils.computeL2(a,b);
							observed_differences.add(new Double(l2norm));
						}
					}
				}
			}
			
			//compute average of observed distances in that context
			double [] obs = new double[observed_differences.size()];
			for (int k = 0; k < observed_differences.size(); k++)
				obs[k]=observed_differences.get(k).doubleValue();
			
			double mean = Utils.mean(obs);
			f[2*c]=Utils.mean(obs);
			f[2*c+1]=Utils.variance(obs);
		}
		
		DenseInstance inst = new DenseInstance(header.numAttributes());
		inst.setDataset(header);
		for (int i = 0; i < f.length; i++){
			inst.setValue(i, f[i]);
		}
		inst.setClassValue(dp.getClassValue());
		
		return inst;
	}
	
	public Instances generateInstances(ArrayList<SetDataPoint> sets){
		Instances data = new Instances(header);
		data.delete();
		
		for (int i = 0; i < sets.size(); i++){
			Instance x_i = this.generateInstance(sets.get(i));
			x_i.setDataset(data);
			data.add(x_i);
		
		}
		
		return data;
	}
	
	public void generateHeader(){
		//in this case, we have one feature per context
		ArrayList<Attribute> attrInfo = new ArrayList<Attribute>();
		
		for (int i = 0; i < contexts.size(); i++){
			Attribute a_i = new Attribute(new String("attr_"+contexts.get(i)));
			attrInfo.add(a_i);
			
			Attribute a_i2 = new Attribute(new String("attr_var_"+contexts.get(i)));
			attrInfo.add(a_i2);
		}

		Attribute classAttribute = new Attribute("class",classValues);
		attrInfo.add(classAttribute);
		header = new Instances("data",attrInfo,0);
		header.setClassIndex(header.numAttributes()-1);
		System.out.println(header.toString());
	}
}

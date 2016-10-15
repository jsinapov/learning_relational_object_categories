package learning.relational.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import data.contextmodel.DataPointBM;
import data.contextmodel.DataUtils;
import learning.relational.labels.ILabelOracle;
import learning.relational.rep.RelDatapoint;

public class RelInstancesCreator {

	IRelationalFeatureCreator FC;
	ILabelOracle LO;
	
	int context_dim;
	String context;
	
	String schema;
	String label;
	
	Instances header;
	int dim;
	
	ArrayList<String> class_values;
	int seed = 1;
	
	public RelInstancesCreator(IRelationalFeatureCreator FC_i, ILabelOracle LO_i){
		FC=FC_i;
		LO=LO_i;
		class_values=new ArrayList<String>();
		class_values.add("-1");
		class_values.add("+1");
	}
	
	public int getSchemaDim(String schema){
		if (schema.equals("1 a")){
			return context_dim;
		}
		else if (schema.equals("2 aa")){
			//for now: absolute difference
			return context_dim;
		}
		return context_dim;
	}
	
	public RelInstancesCreator copy(){
		return new RelInstancesCreator(FC,LO);
	}

	public void setInfo(String c, int contextFeatureDim, String sch, String l) {
		// TODO Auto-generated method stub
		context_dim = contextFeatureDim;
		context = c;
		schema = sch;
		label=l;
	}

	public void generateHeader() {
		// TODO Auto-generated method stub
		dim = this.getSchemaDim(schema);
		
		ArrayList<Attribute> attrInfo = new ArrayList<Attribute>();
		for (int i = 0; i < dim; i ++){
			Attribute a_i = new Attribute(new String("attr_"+context+"_"+i));
			attrInfo.add(a_i);
		}
		
		
		Attribute classAttribute = new Attribute("class",class_values);
	
		attrInfo.add(classAttribute);
		header = new Instances("data",attrInfo,0);
		header.setClassIndex(header.numAttributes()-1);
	}
	
	public static Instances getStaticHeader(){
		ArrayList<Attribute> attrInfo = new ArrayList<Attribute>();
		for (int i = 0; i < 1; i ++){
			Attribute a_i = new Attribute(new String("attr_"+i));
			attrInfo.add(a_i);
		}
		
		ArrayList<String> class_values=new ArrayList<String>();
		class_values.add("-1");
		class_values.add("+1");
		
		Attribute classAttribute = new Attribute("class",class_values);
		
		attrInfo.add(classAttribute);
		Instances header = new Instances("data",attrInfo,0);
		header.setClassIndex(header.numAttributes()-1);
		return header;
	}

	public Instance computeInstance(RelDatapoint R, ArrayList<DataPointBM> tokens, String class_label) {
	
		double [] f = FC.generateFeatures(R, tokens);
		
		if (class_label == null){
			if (LO.getLabels(R).contains(label))
				class_label="+1";
			else class_label = "-1";
		}
		
		Instance new_inst = new DenseInstance(header.numAttributes());
		new_inst.setDataset(header);
		for (int i = 0; i < dim; i ++){
			new_inst.setValue(i, f[i]);
		}
		
		new_inst.setClassValue(class_label);
		return new_inst;
	}
	
	public Instances computeSetInstances(HashMap<String,ArrayList<DataPointBM>> data_map,
		RelDatapoint D_set){
		Instances weka_data = new Instances(header);
		weka_data.delete();
		
		int max_num_combos = 10;
		
		Random R = new Random(seed);
		seed++;
		
		for (int i =0; i < max_num_combos; i++){
			ArrayList<DataPointBM> data_i = new ArrayList<DataPointBM>();
			for (String object : data_map.keySet())
				data_i.add(data_map.get(object).get(R.nextInt(data_map.get(object).size())));
			
			Instance inst_k = this.computeInstance(D_set, data_i, null);
			inst_k.setDataset(weka_data);
			weka_data.add(inst_k);
		}
		
		return weka_data;
	}
	public Instances computePairInstances(ArrayList<DataPointBM> data_a, ArrayList<DataPointBM> data_b,
			RelDatapoint D_bin){
		Instances weka_data = new Instances(header);
		weka_data.delete();
		
		int max_num_pairs = 10;
		
		//for each pair, generate features and add
		for (int a = 0; a < data_a.size();a++){
			for (int b = 0; b < data_b.size();b++){
				ArrayList<DataPointBM> pair_data = new 	ArrayList<DataPointBM>();
				pair_data.add(data_a.get(a));
				pair_data.add(data_b.get(b));
				Instance inst_k = this.computeInstance(D_bin, pair_data, null);
				inst_k.setDataset(weka_data);
				weka_data.add(inst_k);
			}
		}
		
		weka_data.randomize(new Random(seed));
		seed++;
		
		Instances subset = new Instances(weka_data);
		subset.delete();
		
		for (int i = 0; i < max_num_pairs; i++){
			subset.add(weka_data.instance(i));
		}
		
		return subset;
	}

	public Instances computeInstances(ArrayList<DataPointBM> data_c, 
			ArrayList<RelDatapoint> positive,
			ArrayList<RelDatapoint> negative) {
		
		Instances weka_data = new Instances(header);
		//System.out.println(weka_data);
		weka_data.delete();
		
		//System.out.println("Computing instances:\n\tPositive:"+positive+"\n\tNegative:"+negative);
		
		for (int i=0; i < positive.size(); i ++){
			RelDatapoint R_i = positive.get(i);
			ArrayList<String> current_objects = R_i.getAtomicElementIDs();
			
			/* To do: figure out how to generate individual instances
			 *  given all combinations. 
			 */
			if (schema.equals("1 a")){
				ArrayList<DataPointBM> data_o = DataUtils.getDataWithObjects(data_c, current_objects);
				//1 instance per token
				for (int k = 0; k < data_o.size(); k++){
					ArrayList<DataPointBM> data_k = new ArrayList<DataPointBM>();
					data_k.addAll(data_o.subList(k, k+1));
					Instance inst_k = this.computeInstance(R_i, data_k, null);
					inst_k.setDataset(weka_data);
					weka_data.add(inst_k);
				}
			}
			else if (schema.equals("2 aa")){
				ArrayList<DataPointBM> data_a = DataUtils.getDataWithObject(data_c, current_objects.get(0));
				ArrayList<DataPointBM> data_b = DataUtils.getDataWithObject(data_c, current_objects.get(1));
				
				Instances pair_data = this.computePairInstances(data_a, data_b, R_i);
				weka_data.addAll(pair_data);
			}
			else if (schema.equals("1 s")){
				ArrayList<String> set_objects = R_i.getAtomicElementIDs();
				HashMap<String,ArrayList<DataPointBM>> data_map = new HashMap<String,ArrayList<DataPointBM>>();
				for (int k = 0; k < set_objects.size(); k++)
					data_map.put(set_objects.get(k), DataUtils.getDataWithObject(data_c, set_objects.get(k)));
				Instances set_data = this.computeSetInstances(data_map, R_i);
				weka_data.addAll(set_data);
			}
		}
		
		
		for (int i=0; i < negative.size(); i ++){
			RelDatapoint R_i = negative.get(i);
			ArrayList<String> current_objects = R_i.getAtomicElementIDs();
			
			/* To do: figure out how to generate individual instances
			 *  given all combinations. 
			 */
			if (schema.equals("1 a")){
				String object = current_objects.get(0);
				ArrayList<DataPointBM> data_o = DataUtils.getDataWithObjects(data_c, current_objects);
				//1 instance per token
				for (int k = 0; k < data_o.size(); k++){
					ArrayList<DataPointBM> data_k = new ArrayList<DataPointBM>();
					data_k.addAll(data_o.subList(k, k+1));
					Instance inst_k = this.computeInstance(R_i, data_k, null);
					inst_k.setDataset(weka_data);
					weka_data.add(inst_k);
				}
			}
			else if (schema.equals("2 aa")){
				ArrayList<DataPointBM> data_a = DataUtils.getDataWithObject(data_c, current_objects.get(0));
				ArrayList<DataPointBM> data_b = DataUtils.getDataWithObject(data_c, current_objects.get(1));
				
				Instances pair_data = this.computePairInstances(data_a, data_b, R_i);
				weka_data.addAll(pair_data);
				
			}
			else if (schema.equals("1 s")){
				ArrayList<String> set_objects = R_i.getAtomicElementIDs();
				HashMap<String,ArrayList<DataPointBM>> data_map = new HashMap<String,ArrayList<DataPointBM>>();
				for (int k = 0; k < set_objects.size(); k++)
					data_map.put(set_objects.get(k), DataUtils.getDataWithObject(data_c, set_objects.get(k)));
				Instances set_data = this.computeSetInstances(data_map, R_i);
				weka_data.addAll(set_data);
			}
		}
		
		// TODO Auto-generated method stub
		return weka_data;
	}
	
	public ArrayList<String> getClassValues(){
		return class_values;
	}
	
	public Instances getHeader(){
		return header;
	}
}

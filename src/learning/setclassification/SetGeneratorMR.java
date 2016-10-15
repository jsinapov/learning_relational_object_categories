package learning.setclassification;

import java.util.ArrayList;
import java.util.Random;

import learning.setclassification.data.SetDataPoint;

public class SetGeneratorMR {

	String [] weights = {"light","medium","heavy"};
	String [] colors = {"brown","green","blue"};
	String [] contents = {"glass","rice","beans","screws"};
	String [][] properties = {weights,colors,contents};
	
	ArrayList<String> objects;
	
	public SetGeneratorMR(){
		
	}
	
	public ArrayList<SetDataPoint> generateBalancedDataset(int num_tasks, int num_per_set, int rseed, String property ){
		ArrayList<SetDataPoint> positive = this.generateConstantSet(num_tasks/2, num_per_set, property, rseed, "+1");
		ArrayList<SetDataPoint> negative = this.generateVarryingSet(num_tasks/2, num_per_set, property, rseed, "-1");
		
		ArrayList<SetDataPoint> full = new ArrayList<SetDataPoint>();
		full.addAll(positive);
		full.addAll(negative);
		
		return full;
	}
	
	public String getContents(String object){
		for (int i = 0; i < contents.length; i++)
			if (object.indexOf(contents[i]) != -1)
				return contents[i];
		return new String("null");
	}
	
	public String getColor(String object){
		for (int i = 0; i < colors.length; i++)
			if (object.indexOf(colors[i]) != -1)
				return colors[i];
		return new String("null");
	}
	
	public String getWeight(String object){
		for (int i = 0; i < weights.length; i++)
			if (object.indexOf(weights[i]) != -1)
				return weights[i];
		return new String("null");
	}
	
	public boolean variesBy(ArrayList<String> object_set, String property, boolean cover_all_values){
		ArrayList<String> values = new ArrayList<String>();
		int num_values = 3;
		if (property.equals("weight")){
			for (int i = 0; i < object_set.size(); i++){
				String v_i = this.getWeight(object_set.get(i));
				if (!values.contains(v_i)){
					values.add(v_i);
				}
			}
		}
		else if (property.equals("color")){
			for (int i = 0; i < object_set.size(); i++){
				String v_i = this.getColor(object_set.get(i));
				if (!values.contains(v_i)){
					values.add(v_i);
				}
			}
		}
		else if (property.equals("contents")){
			num_values = 4;
			
			for (int i = 0; i < object_set.size(); i++){
				String v_i = this.getContents(object_set.get(i));
				if (!values.contains(v_i)){
					values.add(v_i);
				}
			}
		}
		
		if (!cover_all_values){
			if (values.size() > 1)
				return true;
			else return false;
		}
		else if (values.size() == num_values)
			return true;
		else return false;
	}
	
	public void setObjects(ArrayList<String> obj){
		objects = new ArrayList<String>();
		objects.addAll(obj);
	}
	
	public ArrayList<SetDataPoint> generateConstantSet(int N, int num_per_set, String property, int seed, String class_val){
		ArrayList<SetDataPoint> examples = new ArrayList<SetDataPoint>();
		
		Random R = new Random(seed);
		
		while (examples.size() < N){
			ArrayList<String> set_i = new ArrayList<String>();
			
			int constant_value = 3;
			if (property.equals("color") || property.equals("weight"))
				constant_value = R.nextInt(3);
			else constant_value = R.nextInt(4);
			 
			for (int j = 0; j < num_per_set;j++){
				while (true){
					//pick random weight, color and contents
					int w = R.nextInt(3);
					int c = R.nextInt(3);
					int s = R.nextInt(4);
					
					if (property.equals("color")) c= constant_value;
					else if (property.equals("contents")) s= constant_value;
					else if (property.equals("weight")) w= constant_value;
					
					
					String object_j = new String(weights[w]+"_"+colors[c]+"_"+contents[s]);
					if (!set_i.contains(object_j)){ //make sure it is no duplicate
						set_i.add(object_j);
						break;
					}
				}
			}
			
			//only add set if it doesn't varies by the property
			if (!this.variesBy(set_i, property,false)){
				examples.add(new SetDataPoint(set_i,class_val));
			}
		}
		
		
		return examples;
	}
	
	public ArrayList<SetDataPoint> generateVarryingSet(int N, int num_per_set, String property, int seed, String class_val){
		ArrayList<SetDataPoint> examples = new ArrayList<SetDataPoint>();
		
		Random R = new Random(seed);
		
		while (examples.size() < N){
			ArrayList<String> set_i = new ArrayList<String>();
			
			for (int j = 0; j < num_per_set;j++){
				while (true){
					//pick random weight, color and contents
					int w = R.nextInt(3);
					int c = R.nextInt(3);
					int s = R.nextInt(4);
					String object_j = new String(weights[w]+"_"+colors[c]+"_"+contents[s]);
					if (!set_i.contains(object_j)){ //make sure it is no duplicate
						set_i.add(object_j);
						break;
					}
				}
			}
			
			//only add set if it actually varies by the property
			if (this.variesBy(set_i, property,true)){
				examples.add(new SetDataPoint(set_i,class_val));
			}
		}
		
		
		return examples;
	}
}

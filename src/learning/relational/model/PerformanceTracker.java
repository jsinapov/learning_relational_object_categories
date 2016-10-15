package learning.relational.model;

import java.util.ArrayList;

import weka.core.Instances;

import learning.classification.eval.EvalDelegate;

public class PerformanceTracker {

	ArrayList<String> labels;
	ArrayList<EvalDelegate> ev_list;
	int [] counts;
	
	//some hardcoded sets used to compute confusion matrices for the three different types
	String [] color_props = {"brown","green","blue"};
	String [] weight_props = {"heavy","medium","light"};
	String [] contents_props = {"glass","screws","beans","rice"};
	public double [][] CM_color;
	public double [][] CM_weight;
	public double [][] CM_contents;
	
	public PerformanceTracker(ArrayList<String> all_labels, Instances header){
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
		
		labels = new ArrayList<String>();
		labels.addAll(all_labels);
		
		counts = new int[all_labels.size()];
		
		ev_list=new ArrayList<EvalDelegate>();
		for (int i = 0; i < labels.size(); i++){
			try {
				counts[i]=0;
				EvalDelegate ev_i = new EvalDelegate(header);
				ev_list.add(ev_i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<String> getLabels(){
		return labels;
	}
	
	public int getNumEvaluations(String label){
		int ev_index = labels.indexOf(label);
		return counts[ev_index];
	}
	
	public ArrayList<EvalDelegate> getEvaluations(){
		return ev_list;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < labels.size(); i++){
			if (ev_list.get(i).numInstances() > 0)
				sb.append(new String(labels.get(i)+"\t"+ev_list.get(i).pctCorrect()+"\t"+ev_list.get(i).kappa()+"\t"+
					ev_list.get(i).falsePositiveRate(1)+"\t"+ev_list.get(i).falseNegativeRate(1))+"\n");
		}
		return sb.toString();
	}
	
	public void updateStats(ArrayList<String> estimated_labels, ArrayList<String> true_labels, ArrayList<String> applicable_labels){
		//add to confusion matrices
		
		//1.color
		for (int i = 0; i < color_props.length; i++){
			for (int j = 0; j < color_props.length; j++){
				
				if (estimated_labels.contains(color_props[i]) &&
						true_labels.contains(color_props[j])){
					CM_color[i][j]+=1.0;
				}
			}
		}
		
		for (int i = 0; i < weight_props.length; i++){
			for (int j = 0; j < weight_props.length; j++){
				
				if (estimated_labels.contains(weight_props[i]) &&
						true_labels.contains(weight_props[j])){
					CM_weight[i][j]+=1.0;
				}
			}
		}
		
		for (int i = 0; i < contents_props.length; i++){
			for (int j = 0; j < contents_props.length; j++){
				
				if (estimated_labels.contains(contents_props[i]) &&
						true_labels.contains(contents_props[j])){
					CM_contents[i][j]+=1.0;
				}
			}
		}
		
		for (int i = 0; i < estimated_labels.size();i++){
			String est_label_i = estimated_labels.get(i);
			int ev_index = labels.indexOf(est_label_i);
			if (true_labels.contains(est_label_i)){
				//true positive
				ev_list.get(ev_index).evaluationForSingleInstance(1, 1);
				counts[ev_index]++;

			}
			else {
				//false positive
				ev_list.get(ev_index).evaluationForSingleInstance(1, 0);
				counts[ev_index]++;

			}
		}
		
		for (int i = 0; i < true_labels.size(); i++){
			String true_label_i = true_labels.get(i);
			int ev_index = labels.indexOf(true_label_i);
			if (!estimated_labels.contains(true_label_i)){
				//false negative
				ev_list.get(ev_index).evaluationForSingleInstance(0,1);
				counts[ev_index]++;

			}
		}
		
		for (int i = 0; i < applicable_labels.size(); i++){
			String app_label_i = applicable_labels.get(i);
			int ev_index = labels.indexOf(app_label_i);
			if (!true_labels.contains(app_label_i) && !estimated_labels.contains(app_label_i)){
				//true negative
				ev_list.get(ev_index).evaluationForSingleInstance(0,0);
				counts[ev_index]++;
			}
		}
	}
	
}

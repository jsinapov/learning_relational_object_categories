package learning.relational.model;

import java.util.ArrayList;
import java.util.HashMap;

import data.contextmodel.ContextDataDB;
import data.contextmodel.DataPointBM;

import learning.classification.features.ContextFeatureInstancesCreator;
import learning.classification.labels.BinaryLabelFunctionMR;
import learning.memory.SensorimotorMemory;
import learning.relational.rep.RelDatapoint;

public class RelationalMultiLabelLearnerOld {

	//the set of the currenty observed unique labels so far
	ArrayList<String> known_labels;
	ArrayList<String> label_schemas;
	
	
	//map from labels to RelDatapoints that match that label
	public HashMap<String,ArrayList<RelDatapoint>> label_to_data_map;
	
	//map from label to list of objects associated with that label
	public HashMap<String,ArrayList<String>> label_to_objects_map;
	
	//for each label we know, we store the negative examples here
	public HashMap<String,ArrayList<RelDatapoint>> label_to_data_map_negative;
	
	//here we store data based on the rel. schema
	public HashMap<String,ArrayList<RelDatapoint>> schema_to_data_map;
	
	//list of classification models for each relation
	ArrayList<BinaryModelBM> r_models;
	
	//memory used to story sensorimotor observation
	SensorimotorMemory SMM;
	
	//sensorimotor database
	ContextDataDB cDB;
	
	//oracle -- used only for generating negative examples
	
	public RelationalMultiLabelLearnerOld(ContextDataDB db){
		label_to_data_map = new HashMap<String,ArrayList<RelDatapoint>>();
		label_to_data_map_negative = new HashMap<String,ArrayList<RelDatapoint>>();
		label_to_objects_map = new HashMap<String,ArrayList<String>> ();
		schema_to_data_map = new HashMap<String,ArrayList<RelDatapoint>>();
		known_labels = new ArrayList<String>();
		label_schemas = new ArrayList<String>();
		r_models = new ArrayList<BinaryModelBM>();
		SMM=new SensorimotorMemory();
		cDB=db;
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
	
	public void printDebug(){
		System.out.println("Known labels: "+known_labels);
		System.out.println("Label schemas: "+label_schemas);
		
		for (String label : known_labels){
			System.out.println("Label:"+label);
			System.out.println(label_to_data_map_negative.get(label));
			System.out.println(label_to_data_map.get(label));
		}
	}
	
	public ArrayList<String> predictLabels(RelDatapoint D, ArrayList<DataPointBM> test_data){
		 ArrayList<String> estimated_labels = new  ArrayList<String>();
		 
		 for (int i = 0; i < known_labels.size(); i++){
			 if (D.getSchema().equals(label_schemas.get(i))){
				 
				 if (r_models.get(i).isTrained){
					 double [] d = r_models.get(i).classify(test_data);
					 System.out.println(known_labels.get(i)+":\t"+d[0]+","+d[1]);
				 }
			 }
		 }
		 
		 return estimated_labels;
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
				
				//add new array list to data
				ArrayList<RelDatapoint> data = new ArrayList<RelDatapoint>();
				data.add(D);
				label_to_data_map.put(labels.get(i),data);
				
				//go through existing data and see if any should be used as negative examples
			
				//To do: get rid of BinaryModelBM and replace it with RelCatRecognitionBM model
				
				//create model
				BinaryModelBM rC = new BinaryModelBM(D.getSchema(),labels.get(i),cDB);
				
				//set labeling function and feature creation function for simple categories
				BinaryLabelFunctionMR LF = new BinaryLabelFunctionMR(labels.get(i));
				ContextFeatureInstancesCreator IC = new ContextFeatureInstancesCreator(LF);
				rC.setClassifcationParams(LF,IC );
				rC.initialize();
				rC.addPositiveExample(D);
				r_models.add(rC);
			} 
		}
		
		
		//re-train models if needed
		for (int i = 0; i < update.length;i++){
			if (update[i]==true){
				System.out.println("Updating label "+known_labels.get(i));
				r_models.get(i).train(SMM.getAllData());
			}
		}
	}
	
	
}

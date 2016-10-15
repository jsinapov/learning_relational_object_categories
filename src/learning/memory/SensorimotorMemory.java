package learning.memory;

import java.util.ArrayList;
import java.util.HashMap;

import data.contextmodel.DataPointBM;
import data.contextmodel.InteractionTrial;

import learning.relational.rep.RelDatapoint;

public class SensorimotorMemory {

	//organized by object id
	HashMap<String,ArrayList<DataPointBM>> object_to_data_map;
	HashMap<String,ArrayList<InteractionTrial>> object_to_trials_map;
	
	//organized by context
	HashMap<String,ArrayList<DataPointBM>> context_to_data_map;
	
	//all
	ArrayList<DataPointBM> observations;
	
	//organized by behavior
	//HashMap<String,ArrayList<DataPointBM>> behavior_to_data_map;
	
	public SensorimotorMemory(){
		object_to_data_map = new HashMap<String,ArrayList<DataPointBM>> ();
		object_to_trials_map = new HashMap<String,ArrayList<InteractionTrial>>();
		
		context_to_data_map = new HashMap<String,ArrayList<DataPointBM>>();
		observations = new ArrayList<DataPointBM> ();
		//behavior_to_data_map = new HashMap<String,ArrayList<DataPointBM>>();
	}
	
	
	public ArrayList<DataPointBM> getAllData(){
		return observations;
	}
	
	public ArrayList<InteractionTrial> getTrialsWithObject(String object){
		return object_to_trials_map.get(object);
	}
	
	public void addTrial(InteractionTrial T){
		if (object_to_trials_map.containsKey(T.getObject())){
			object_to_trials_map.get(T.getObject()).add(T);
		}
		else {
			ArrayList<InteractionTrial> temp = new ArrayList<InteractionTrial>();
			temp.add(T);
			object_to_trials_map.put(T.getObject(), temp);
		}
	}
	
	public void addObservation(DataPointBM data){
		observations.add(data);
		
		
		if (data.getObject() != null){
			if (object_to_data_map.containsKey(data.getObject())){
				object_to_data_map.get(data.getObject()).add(data);
			}
			else {
				ArrayList<DataPointBM> temp = new ArrayList<DataPointBM>();
				temp.add(data);
				object_to_data_map.put(data.getObject(), temp);
			}
		}
		
		String context = data.getContextName();
		if (context_to_data_map.containsKey(context)){
			context_to_data_map.get(context).add(data);
		}
		else {
			ArrayList<DataPointBM> temp = new ArrayList<DataPointBM>();
			temp.add(data);
			context_to_data_map.put(context, temp);
		}
		
	}
}

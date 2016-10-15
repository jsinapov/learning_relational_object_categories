package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DataLoaderRC {

	
	
	public ArrayList<double[]> loadTorqueData(String filename, int num_joints){
		ArrayList<double[]> data = new ArrayList<double[]>();
		
		try {
			BufferedReader BR = new BufferedReader(new FileReader(new File(filename)));
		
			while (true){
				String line = BR.readLine();
				if (line == null)
					break;
				
				
				StringTokenizer st = new StringTokenizer(line);
				double [] v = new double[num_joints];
				st.nextToken();
				for (int i = 0; i < num_joints;i++)
					v[i]=Double.parseDouble(st.nextToken());
				
				data.add(v);
			}
			
			
			
			
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
	
	public PropRecordRC loadPropRecord(String object, String behavior,int trial, String path){
		String trial_path = 
				new String(path+"/"+object+"/trial_1/exec_"+trial+"/"+behavior+"/proprioception/jtrq0.txt");
		
		ArrayList<double[]> t_data = this.loadTorqueData(trial_path, 7);
		return new PropRecordRC(trial,object,behavior,t_data);
	}
	
	public boolean trialExists(String object, String behavior, String rc_modality, int trial, String path){
		boolean exists = true;
		
		String trial_path = 
				new String(path+"/"+object+"/trial_1/exec_"+trial+"/"+behavior+"/"+rc_modality);
		
		//System.out.println(trial_path);
		
		File F = new File(trial_path);
		if (!F.exists())
			return false;
		
		
		return exists;
	}
	
	public ArrayList<PropRecordRC> loadPropRecordsForBehavior(String behavior, ArrayList<String> objects, String path, int num_trials){
		ArrayList<PropRecordRC> records = new ArrayList<PropRecordRC>();
		
		for (int o = 0; o < objects.size(); o++){
			for (int t= 1; t <= num_trials; t++){
				if (this.trialExists(objects.get(o), behavior, "proprioception", t, path))
					records.add(this.loadPropRecord(objects.get(o), behavior, t, path));
			}
		}
		
		
		return records;
	}
	
	public ArrayList<String> getObjectList(String data_path){
		ArrayList<String> objects = new ArrayList<String>();
		
		File D = new File(data_path);
		String [] dirs = D.list();
		for (int i = 0; i < dirs.length; i++){
			if (dirs[i].length()>2 && !dirs[i].equals("no_object"))
				objects.add(dirs[i]);
		}
		
		return objects;
	}
	
}

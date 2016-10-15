package data;

import java.util.ArrayList;

public class PropRecordRC {

	public int trial; //corresponds to exec_1, exec_2, etc.
	public String object;
	public String behavior;
	
	public ArrayList<double[]> torque_data;
	
	public PropRecordRC(int t, String o, String b, ArrayList<double[]> d){
		trial = t;
		object = o;
		behavior = b;
		torque_data = d;
	}
}

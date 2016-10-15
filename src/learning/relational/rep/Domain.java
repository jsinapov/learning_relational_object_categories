package learning.relational.rep;

import java.util.ArrayList;

public class Domain {
	
	boolean pSet = false;
	ArrayList<String> values;
	
	public Domain(ArrayList<String> v){
		values = new ArrayList<String>();
		values = v;
	}
	
	public Domain(ArrayList<String> v, boolean p){
		pSet = p;
		values = new ArrayList<String>();
		values = v;
	}

	public boolean isPowerSet(){
		return pSet;
	}
	public ArrayList<String> getValues(){
		return values;
	}
	
}

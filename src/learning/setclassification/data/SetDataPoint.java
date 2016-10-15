package learning.setclassification.data;

import java.util.ArrayList;

public class SetDataPoint {

	ArrayList<String> members;
	String class_value;
	
	public SetDataPoint(ArrayList<String> m, String c){
		this.members = new ArrayList<String>();
		this.members.addAll(m);
		this.class_value=c;
	}
	
	public ArrayList<String> getObjects(){
		return members;
	}
	
	public String getClassValue(){
		return class_value;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for (int i = 0; i < members.size(); i++){
			sb.append(members.get(i));
			if (i != members.size()-1)
				sb.append(",");
		}
		sb.append(" ] \t");
		sb.append(class_value);
		
		return sb.toString();
	}
}

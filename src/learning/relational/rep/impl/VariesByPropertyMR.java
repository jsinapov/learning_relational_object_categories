package learning.relational.rep.impl;

import java.util.ArrayList;

import learning.relational.rep.AtomicElement;
import learning.relational.rep.Domain;
import learning.relational.rep.RelDatapoint;
import learning.relational.rep.SetElement;
import learning.relational.rep.iface.Floor;
import learning.relational.rep.iface.Relation;

public class VariesByPropertyMR implements Relation {

	Domain D;
	
	String [] weights = {"light","medium","heavy"};
	String [] colors = {"brown","green","blue"};
	String [] contents = {"glass","rice","beans","screws"};
	String [][] properties = {weights,colors,contents};
	String target_property;
	
	
	public VariesByPropertyMR(ArrayList<String> objects, String p){
		D = new Domain(objects,true);
		target_property=p;
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

	
	
	@Override
	public int getArity() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public ArrayList<Domain> getDomains() {
		// TODO Auto-generated method stub
		ArrayList<Domain> d_list = new ArrayList<Domain>();
		d_list.add(D);
		return d_list;
	}

	@Override
	public boolean truthValue(RelDatapoint rd) {
		
		SetElement e = (SetElement)rd.getElements().get(0);
		ArrayList<AtomicElement> set = e.getSet();
		
		ArrayList<String> set_objects = new ArrayList<String>();
		for (int i = 0; i < set.size(); i++){
			set_objects.add(set.get(i).getID());
		}
		
		return this.variesBy(set_objects, target_property, false);
		

	}

	
}

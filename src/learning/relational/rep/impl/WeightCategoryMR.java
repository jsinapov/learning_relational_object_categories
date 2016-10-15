package learning.relational.rep.impl;

import java.util.ArrayList;

import learning.relational.rep.AtomicElement;
import learning.relational.rep.Domain;
import learning.relational.rep.RelDatapoint;
import learning.relational.rep.iface.Relation;

public class WeightCategoryMR implements Relation {

	Domain D;
	
	
	public WeightCategoryMR(ArrayList<String> objects){
		D = new Domain(objects,false);
		
	}
	
	public int getArity() {
		return 1;
	}

	@Override
	public ArrayList<Domain> getDomains() {
		ArrayList<Domain> d_list = new ArrayList<Domain>();
		d_list.add(D);
		return d_list;
	}

	@Override
	public boolean truthValue(RelDatapoint rd) {
		String object = ((AtomicElement)rd.getElements().get(0)).getID();
		// TODO Auto-generated method stub
		return false;
	}

}

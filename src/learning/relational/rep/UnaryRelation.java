package learning.relational.rep;

import java.util.ArrayList;

import learning.relational.rep.iface.Relation;

public class UnaryRelation implements Relation {

	Domain D; 
	
	public UnaryRelation(Domain dom){
		D = dom;
	}
	
	
	public int getArity() {
		return 1;
	}

	@Override
	public ArrayList<Domain> getDomains() {
		ArrayList<Domain> domains = new ArrayList<Domain>();
		domains.add(D);
		return domains;
	}


	@Override
	public boolean truthValue(RelDatapoint rd) {
		// TODO Auto-generated method stub
		return false;
	}

}

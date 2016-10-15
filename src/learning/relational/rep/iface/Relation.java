package learning.relational.rep.iface;

import java.util.ArrayList;

import learning.relational.rep.Domain;
import learning.relational.rep.RelDatapoint;

public interface Relation {

	//returns the number of element in the relation
	public int getArity();
	
	public ArrayList<Domain> getDomains();
	
	public boolean truthValue(RelDatapoint rd);
}

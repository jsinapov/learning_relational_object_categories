package learning.relational.rep;

import learning.relational.rep.iface.Element;

public class AtomicElement implements Element {

	String id;
	
	public AtomicElement(String i){
		id = i;
	}
	
	public String getID(){
		return id;
	}
	
	public String toString(){
		return id;
	}

	@Override
	public boolean isSet() {
		// TODO Auto-generated method stub
		return false;
	}
	
}

package learning.relational.rep;

import java.util.ArrayList;

import learning.relational.rep.iface.Element;

public class SetElement implements Element{

	ArrayList<AtomicElement> setElements;
	
	public SetElement(ArrayList<AtomicElement> set_elements){
		setElements = new ArrayList<AtomicElement>();
		setElements.addAll(set_elements);
	}
	
	public ArrayList<AtomicElement> getSet(){
		return setElements;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < setElements.size();i++){
			sb.append(setElements.get(i).toString());
			if (i <setElements.size()-1)
				sb.append(" ");
			else sb.append("]");
			
		}
		return sb.toString();
	}
	
	public ArrayList<String> getSetIDs(){
		ArrayList<String> ids = new ArrayList<String>();
		for (int i = 0; i < setElements.size(); i++)
			ids.add(setElements.get(i).getID());
		
		return ids;
	}

	@Override
	public boolean isSet() {
		return true;
	}
	
}

package learning.relational.rep;

import java.util.ArrayList;

import learning.relational.rep.iface.Element;

public class RelDatapoint {

	ArrayList<Element> rData;
	
	public RelDatapoint(String a){
		rData = new ArrayList<Element>();
		rData.add(new AtomicElement(a));
	}
	
	public RelDatapoint(SetElement x){
		rData = new ArrayList<Element>();
		rData.add(x);
	}
	
	public ArrayList<String> getAtomicElementIDs(){
		ArrayList<String> ids = new ArrayList<String>();
		
		for (int i = 0; i < rData.size();i++){
			if (rData.get(i).isSet()){
				ArrayList<String> ids_i = ((SetElement)rData.get(i)).getSetIDs();
				for (int k = 0; k < ids_i.size();k++)
					if (!ids.contains(ids_i.get(k)))
						ids.add(ids_i.get(k));
			}
			else {
				ids.add(((AtomicElement)rData.get(i)).getID());
			}
		}
		
		return ids;
	}
	
	public RelDatapoint(ArrayList<Element> d){
		rData = d;
	}
	
	public ArrayList<Element> getElements(){
		return rData;
	}
	
	public String getSchema(){
		StringBuffer sb = new StringBuffer();
		sb.append(new String(""+rData.size()+" "));
		for (int i = 0; i < rData.size(); i++){
			if (rData.get(i).isSet())
				sb.append("s");
			else sb.append("a");
		}
		
		return sb.toString();
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (int i = 0; i < rData.size(); i++){
			sb.append(rData.get(i).toString());
			if (i < rData.size()-1)
				sb.append(",");
			else sb.append("}");
		}
		
		return sb.toString();
	}
	
	public static RelDatapoint generateUnaryDatapoint(String id){
		ArrayList<Element> x = new ArrayList<Element>();
		x.add(new AtomicElement(id));
		RelDatapoint D = new RelDatapoint(x);
		return D;
	}
	
	public static RelDatapoint generateBinaryObjectDatapoint(String A, String B){
		ArrayList<Element> x = new ArrayList<Element>();
		x.add(new AtomicElement(A));
		x.add(new AtomicElement(B));
		RelDatapoint D = new RelDatapoint(x);
		return D;
	}
	
	public static RelDatapoint generateSetDatapoint(ArrayList<String> ids){
		ArrayList<AtomicElement> x = new ArrayList<AtomicElement>();
		for (int i = 0; i < ids.size(); i++){
			x.add(new AtomicElement(ids.get(i)));
		}
		SetElement s = new SetElement(x);
		return new RelDatapoint(s);
	}
}

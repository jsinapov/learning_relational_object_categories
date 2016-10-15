package learning.relational.labels;

import java.util.ArrayList;

import utils.UtilsJS;

import learning.relational.rep.RelDatapoint;
import learning.relational.rep.iface.Element;
import learning.relational.rep.AtomicElement;
import learning.relational.rep.SetElement;
import learning.setclassification.SetGeneratorMR;

public class OracleMR implements ILabelOracle {
	
	//used to compute values for the sets that vary by or are constant in something
	SetGeneratorMR SG;
	
	String [] properties = {"weight","contents","color"};
	
	
	final boolean doColor = true;
	final boolean doWeight = true;
	final boolean doContents = true;
	
	public OracleMR(){
		SG = new SetGeneratorMR();
	}


	public boolean hasLabel(RelDatapoint D, String label){
		ArrayList<String> L = this.getLabels(D);
		if (L.contains(label))
			return true;
		else return false;
	}
	
	@Override
	public ArrayList<String> getLabels(RelDatapoint D) {
		
		ArrayList<String> labels = new ArrayList<String>();
		
		ArrayList<Element> r_elements = D.getElements();
		if (r_elements.size()==1){ //unary relation
			 
			Element x = r_elements.get(0);
			if (!x.isSet()){ //relation over a single object
				String object = ((AtomicElement)x).getID();
				String [] L = object.split("_");
				
				for (int k = 0; k < L.length;k++){
					
					if (doContents)
						if (L[k].equals("glass") || L[k].equals("beans") || 
							L[k].equals("screws") || L[k].equals("rice") )
							labels.add(L[k]);
					
					if (doWeight)
						if (L[k].equals("heavy") || L[k].equals("medium") || 
								L[k].equals("light"))
								labels.add(L[k]);
					
					if (doColor)
						if (L[k].equals("brown") || L[k].equals("blue") || 
								L[k].equals("green"))
								labels.add(L[k]);
				}
				
				//labels = UtilsJS.toArrayList(L);
			}
			else {
				//unary relation over a set
				ArrayList<String> objects = ((SetElement)x).getSetIDs();

				//check if vary by context
				for (int i = 0; i < properties.length; i++){
					if (SG.variesBy(objects, properties[i], false)){
						if (doColor && properties[i].equals("color"))
							labels.add(new String("vary_by_"+properties[i]));
						if (doWeight && properties[i].equals("weight"))
							labels.add(new String("vary_by_"+properties[i]));
						if (doContents && properties[i].equals("contents"))
							labels.add(new String("vary_by_"+properties[i]));
					}
				}
			} 
		}
		else if (r_elements.size()==2){
			//System.out.println("!"+D.toString());
			Element A = r_elements.get(0);
			Element B = r_elements.get(1);
			
			if (!A.isSet() && !B.isSet()){ //binary relational category, e.g., "heavier then"
				String object_A = ((AtomicElement)A).getID();
				String object_B = ((AtomicElement)B).getID();
				
				if (doWeight){
					if (object_A.contains("heavy") && (object_B.contains("medium") || object_B.contains("light")))
						labels.add("heavier");
					else if (object_A.contains("medium") &&  object_B.contains("light"))
						labels.add("heavier");
						
					if (object_A.contains("light") && (object_B.contains("medium") || object_B.contains("heavy")))
						labels.add("lighter");
					else if (object_A.contains("medium") &&  object_B.contains("heavy"))
						labels.add("lighter");
				}
				
				ArrayList<String> objectsAB= new ArrayList<String>();
				objectsAB.add(object_A);
				objectsAB.add(object_B);
				for (int i = 0; i < properties.length; i++){
					//if (properties[i].equals("contents"))
						if (!SG.variesBy(objectsAB, properties[i], false))
							if (doColor && properties[i].equals("color"))
								labels.add(new String("same_"+properties[i]));
							if (doWeight && properties[i].equals("weight"))
								labels.add(new String("same_"+properties[i]));
							if (doContents && properties[i].equals("contents"))
								labels.add(new String("same_"+properties[i]));
							
				}
				
			}
		}
		
		// TODO Auto-generated method stub
		return labels;
	}


	@Override
	public ArrayList<String> getAllLabels() {
		ArrayList<String> labels = new ArrayList<String>();
		
		//unary
		/*labels.add("heavy");
		labels.add("medium");
		labels.add("light");
		labels.add("blue");
		labels.add("green");
		labels.add("brown");
		labels.add("glass");
		labels.add("screws");
		labels.add("beans");
		labels.add("rice");
		
		//binary
		labels.add("heavier");
		labels.add("lighter");
		labels.add("same_weight");
		labels.add("same_color");
		labels.add("same_contents");
		
		
		//unary over a set
		labels.add("vary_by_color");
		labels.add("vary_by_contents");
		labels.add("vary_by_weight");*/
		
		labels.addAll(this.getLabelsForSchema("1 a"));
		labels.addAll(this.getLabelsForSchema("2 aa"));
		labels.addAll(this.getLabelsForSchema("1 s"));
		
		return labels;
	}


	@Override
	public ArrayList<String> getLabelsForSchema(String s) {
		ArrayList<String> labels = new ArrayList<String>();
		
		if (s.equals("1 a")){
			//unary
			if (doWeight){
				labels.add("heavy");
				labels.add("medium");
				labels.add("light");
			}
			
			if (doColor){
				labels.add("blue");
				labels.add("green");
				labels.add("brown");
			}
			
			if (doContents){
				labels.add("glass");
				labels.add("screws");
				labels.add("beans");
				labels.add("rice");
			}
			
		}
		else if (s.equals("2 aa")){
			//binary
			if (doWeight){
				labels.add("heavier");
				labels.add("lighter");
				labels.add("same_weight");
			}
			
			if (doColor)
				labels.add("same_color");
			
			if (doContents)
				labels.add("same_contents");
		}
		else if (s.equals("1 s")){
			if (doColor)
				labels.add("vary_by_color");
			
			if (doContents)
				labels.add("vary_by_contents");
			
			if (doWeight)
				labels.add("vary_by_weight");
		}
		return labels;
	}

}

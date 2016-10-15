package learning.classification.labels;

import java.util.ArrayList;

public class LabelFunctionMR implements IClassLabelFunction{

	String [] weights = {"light","medium","heavy"};
	String [] colors = {"brown","green","blue"};
	String [] contents = {"glass","rice","beans","screws"};
	String [][] properties = {weights,colors,contents};
	String target_property;
	ArrayList<String> labelSet;
	
	public LabelFunctionMR(String p){
		target_property = p;
		labelSet=new ArrayList<String>();
		
		String [] l = weights;
		if (p.equals("color"))
			l = colors;
		else if (p.equals("contents"))
			l = contents;
		else l = weights;
		
		for (int i =0; i < l.length; i++)
			labelSet.add(l[i]);
		
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
	
	@Override
	public String getLabel(String object) {
		if (target_property.equals("weight")){
			return getWeight(object);
		}
		else if (target_property.equals("color")){
			return getColor(object);
		}
		else if (target_property.equals("contents")){
			return getContents(object);
		}
		else return new String("null");
	}

	@Override
	public ArrayList<String> getLabelSet() {
		// TODO Auto-generated method stub
		return labelSet;
	}

	@Override
	public IClassLabelFunction copy() {
		return new LabelFunctionMR(target_property);
	}

}

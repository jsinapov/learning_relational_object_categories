package learning.classification.labels;

import java.util.ArrayList;

public interface IClassLabelFunction {
	
	public String getLabel(String object);
	public ArrayList<String> getLabelSet();
	public IClassLabelFunction copy();
}

package learning.classification.labels;

import java.util.ArrayList;

import learning.relational.labels.OracleMR;
import learning.relational.rep.RelDatapoint;

public class BinaryLabelFunctionMR implements IClassLabelFunction {

	OracleMR O;
	String L;
	ArrayList<String> values;
	
	
	public BinaryLabelFunctionMR(String label){
		O = new OracleMR();
		L=label;
		values=new ArrayList<String>();
		values.add("+1");
		values.add("-1");
	}
	
	@Override
	public String getLabel(String object) {
		// TODO Auto-generated method stub
		if (O.hasLabel(new RelDatapoint(object), L))
			return new String("+1");
		else return new String("-1");
	}

	@Override
	public ArrayList<String> getLabelSet() {
		return values;
	}

	@Override
	public IClassLabelFunction copy() {
		return new BinaryLabelFunctionMR(L);
	}

}

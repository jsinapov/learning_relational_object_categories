package learning.relational.labels;

import java.util.ArrayList;

import learning.relational.rep.RelDatapoint;

public interface ILabelOracle {

	public ArrayList<String> getLabels(RelDatapoint D);
	public ArrayList<String> getAllLabels();
	public ArrayList<String> getLabelsForSchema(String s);
}

package learning.classification.features;

import java.util.ArrayList;

import weka.core.Instance;
import weka.core.Instances;

import data.contextmodel.DataPointBM;

public interface IContextInstancesCreator {

	public Instances generateHeader();
	public Instances computeInstances(ArrayList<DataPointBM> data);
	public Instance computeInstance(DataPointBM d);
	public void setContextInfo(String name, int d);
	public IContextInstancesCreator copy();
}

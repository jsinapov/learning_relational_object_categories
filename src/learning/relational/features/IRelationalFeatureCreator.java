package learning.relational.features;

import java.util.ArrayList;

import data.contextmodel.DataPointBM;

import learning.relational.rep.RelDatapoint;

public interface IRelationalFeatureCreator {

	public double [] generateFeatures(RelDatapoint D, ArrayList<DataPointBM> bm_data);
	public ArrayList<double[]> generateFeatures(ArrayList<RelDatapoint> D_list,ArrayList<DataPointBM> bm_data);
	
}

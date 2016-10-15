package learning.classification.features;

import java.util.ArrayList;

import learning.classification.labels.IClassLabelFunction;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import data.contextmodel.DataPointBM;

public class ContextFeatureInstancesCreator implements IContextInstancesCreator {

	String context_name;
	int dim;
	IClassLabelFunction LF;
	Instances header;
	
	public ContextFeatureInstancesCreator(IClassLabelFunction l){
		LF=l;
	}
	
	public IContextInstancesCreator copy(){
		return new ContextFeatureInstancesCreator(LF);
	}
	
	@Override
	public Instances generateHeader() {
		
		ArrayList<Attribute> attrInfo = new ArrayList<Attribute>();
		for (int i = 0; i < dim; i ++){
			Attribute a_i = new Attribute(new String("attr_"+context_name+"_"+i));
			attrInfo.add(a_i);
		}
		
		Attribute classAttribute = new Attribute("class",LF.getLabelSet());
	
		attrInfo.add(classAttribute);
		header = new Instances("data",attrInfo,0);
		header.setClassIndex(header.numAttributes()-1);
		
		return header;
		
		
	}

	@Override
	public Instances computeInstances(ArrayList<DataPointBM> data) {
		Instances weka_data = new Instances(header);
		weka_data.delete();
		
		for (int i =0; i < data.size(); i++){
			Instance x_i = this.computeInstance(data.get(i));
			x_i.setDataset(weka_data);
			weka_data.add(x_i);
		}
		
		return weka_data;
	}

	@Override
	public Instance computeInstance(DataPointBM d) {
		Instance new_inst = new DenseInstance(header.numAttributes());
		new_inst.setDataset(header);
		for (int i = 0; i < dim; i ++){
			new_inst.setValue(i, d.getFeatureAt(i));
		}
		
		String c_val = LF.getLabel(d.getObject());
		new_inst.setClassValue(c_val);
		return new_inst;
	}



	@Override
	public void setContextInfo(String name, int d) {
		context_name=name;
		dim=d;
	}

}

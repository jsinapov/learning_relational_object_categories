package exp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import utils.FeatureUtils;
import utils.UtilsJS;
import weka.attributeSelection.PrincipalComponents;
import weka.core.Instances;

import data.DataLoaderRC;
import data.PropRecordRC;
import data.contextmodel.DataUtils;
import no.ImageUtils;
import no.hiof.imagepr.Image;
import no.hiof.imagepr.RGBImage;
import no.hiof.imagepr.filters.ImageScaler;
import AudioFFT.HistogramDFT;
import AudioFFT.SoundFFT;

public class FeatureProcessing {

	public static void testBinning(){
		DataLoaderRC DL = new DataLoaderRC();
		String rc_file = "/media/FreeAgent Drive/data_backup/cyclone101/rc_data/cone_5/trial_1/exec_2/crush/proprioception/jtrq0.txt";
		ArrayList<double[]> torq_data = DL.loadTorqueData(rc_file, 7);
		double [][] M = UtilsJS.listToMatrix(torq_data);
		double [][] F = FeatureUtils.computeBinnedFeatures(torq_data, 10);
		
		RGBImage orig = ImageUtils.matrixToImageTransposed(M);
		RGBImage tranf = ImageUtils.matrixToImageTransposed(F);
		
		ImageScaler IS1 = new ImageScaler(10.0,0.5,1);
		ImageScaler IS2 = new ImageScaler(20.0,20.0,1);
		
		orig = (RGBImage)IS1.filter(orig);
		tranf = (RGBImage)IS2.filter(tranf);
		orig.show();
		tranf.show();
	}
	
	public static void computePropFeatures(String[] args){
		DataLoaderRC DL = new DataLoaderRC();
		String path = "/media/FreeAgent Drive/data_backup/cyclone101/rc_data";
		String outpath = "/home/jsinapov/research/datasets/cy101/prop_pca_features";
		ArrayList<String> objects = DL.getObjectList(path);
		System.out.println(objects);
		
		int num_trials = 5;
		
		String [] behaviors = {"grasp","lift_slow","hold","shake","low_drop","poke","tap","push","crush"};
		//String [] behaviors = {"crush"};
		
		//how many bins
		int t_bins = 10;
		
		//PCA params
		boolean doPCA = false;
		double sample_prob = 0.025;
		Random R = new Random(1);
		
		for (int b = 0; b < behaviors.length; b++){
			
			//load records
			ArrayList<PropRecordRC> records_b = DL.loadPropRecordsForBehavior(behaviors[b], objects, path, num_trials);
			System.out.println("Loaded "+records_b.size()+" prop. records for "+behaviors[b]);
		
			//sample
			if (doPCA){
				ArrayList<double[]> pca_samples = new ArrayList<double[]>();
				for (int i = 0; i < records_b.size(); i++){
					for (int s = 0; s < records_b.get(i).torque_data.size();s++){
						if (R.nextDouble()<sample_prob)
							pca_samples.add(records_b.get(i).torque_data.get(s));
					}
				}
				
				System.out.println("Extracted "+pca_samples.size()+" samples for PCA");
				
				
				//compute PCA and use it to transform data
				Instances weka_pca_data = UtilsJS.toInstances(pca_samples);
				PrincipalComponents PCA = new PrincipalComponents();
				PCA.setVarianceCovered(0.85);
				try {
					PCA.buildEvaluator(weka_pca_data);
					System.out.println(PCA.toString());
					
					//transform data for each record
					for (int i = 0; i < records_b.size(); i++){
						ArrayList<double[]> input_data_i = records_b.get(i).torque_data;
						ArrayList<double[]> transformed_data_i = new ArrayList<double[]>();
						
						Instances weka_data_input = UtilsJS.toInstances(input_data_i);
						weka_data_input = PCA.transformedData(weka_data_input);
						transformed_data_i = UtilsJS.toFeatureVectors(weka_data_input);
						
						records_b.get(i).torque_data.clear();
						records_b.get(i).torque_data = transformed_data_i;
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//compute features -- To DO
			String filename_b = new String(outpath+"/"+behaviors[b]+"_proprioception_t10_normalized.txt");
			FileWriter FW;
			try {
				FW = new FileWriter(new File(filename_b));
				for (int i = 0; i < records_b.size(); i++){
					double [][] F_i = FeatureUtils.computeBinnedFeaturesNormalized(records_b.get(i).torque_data, t_bins);
					
					//System.out.println(records_b.get(i).object);
					//ImageUtils.scaleImage(20.0,20.0, 1, ImageUtils.matrixToImageTransposed(F_i)).show();
					
					//ImageUtils.scaleImage(20.0,20.0, 1, ImageUtils.matrixToImageTransposed(F_i)).show();
					
					double [] x_i = UtilsJS.flattenMatrix(F_i);
					String object_i = records_b.get(i).object;
					int trial_i = records_b.get(i).trial;
					FW.write(new String(object_i+"_t"+trial_i+","));
					for (int j = 0; j < x_i.length; j++){
						FW.write(new String(""+x_i[j]));
						if (j < x_i.length-1)
							FW.write(new String(","));
					}
					FW.write("\n");
				}
				
				FW.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void main(String[] args){
		//testBinning();
		computePropFeatures(args);
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void mr_audio_process(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//String filename ="/home/jsinapov/matrix_reasoning_data/heavy_blue_beans/trial_1/exec_1/high_velocity_shake/hearing/1364434836153423.wav";
		//double [][] sg = SoundFFT.getMatrixFFT(filename, 129, true);
		//ImageUtils.matrixToImageTransposed(sg).show();
		
		ArrayList<String> objects = DataUtils.getObjectsMR();
		int num_trials = 10;
		String [] behaviors = {"grasp","lift_slow","hold","shake","high_velocity_shake","low_drop","tap","poke","push","crush"};
		String datapath = "/home/jsinapov/matrix_reasoning_data/";
		String output_path = "/home/jsinapov/research/datasets/matrix_reasoning/features2/";
		for (int b = 0; b < behaviors.length; b++){
			
			String out_filename = new String(output_path+"audio2_"+behaviors[b]+".txt");
			FileWriter FW = new FileWriter(new File(out_filename));
			
			
			for (int o = 0; o < objects.size(); o++){
				for (int t = 1; t <= num_trials; t++){
					
					String wav_directory = new String(datapath+""+objects.get(o)+"/trial_1/exec_"+t+"/"+behaviors[b]+"/hearing/");
					//System.out.println(wav_directory);
					File P = new File(wav_directory);
					File [] files = P.listFiles();
					String filename = new String("");
					
					if (files != null){
						for (int f=0; f < files.length; f++){
							if (files[f].getName().substring(files[f].getName().length()-4,files[f].getName().length()).equals(".wav")){
								filename = new String(wav_directory+""+files[f].getName());
								break;
							}
						}
					}
					
					if (filename.equals("")){
						System.out.println(wav_directory);
						
					}
					
					/*double [][] sg = SoundFFT.getMatrixFFT(filename, 129, true);
					double [][] hist = HistogramDFT.computeHistogram(sg, 10, 10);
					//UtilsJS.printMatrix(hist);
					double [] hist_vector = HistogramDFT.computeHistogramVector(sg, 10, 10);
					
					StringBuffer line = new StringBuffer(objects.get(o)+","+(t-1)+",");
					for (int k = 0; k < hist_vector.length; k++){
						line.append(new String(""+hist_vector[k]));
						if (k < hist_vector.length-1)
							line.append(",");
						else line.append("\n");
					}
					
					FW.write(line.toString());
					
					//UtilsJS.printArray(hist_vector);
					//ImageUtils.matrixToImageTransposed(sg).show();*/
				}
			}
			
			FW.close();
		}
		
	}

}

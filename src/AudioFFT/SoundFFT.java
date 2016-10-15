package AudioFFT;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import AudioFFT.Data;
import AudioFFT.DataEndSignal;
import AudioFFT.DiscreteFourierTransform;
import AudioFFT.DoubleData;
import AudioFFT.FFTFactory;
import AudioFFT.Preemphasizer;
import AudioFFT.RaisedCosineWindower;
import AudioFFT.StreamDataSource;

public class SoundFFT {

	public static double [][] getMatrixFFT(String soundFileName, int fK, boolean stereo){

		if (stereo)
			fK = 2*(fK-1)+1;
		
		
		DiscreteFourierTransform fft = SoundFFT.getFFT(soundFileName, fK);
		ArrayList<double[]> columns = new ArrayList<double[]>();
		
		int counter = 0;

		double maxIntensity = Double.MIN_VALUE;
		try {
			Data spectrum = fft.getData();
	
			while (!(spectrum instanceof DataEndSignal)) {
				if (spectrum instanceof DoubleData) {
					double[] spectrumData = ((DoubleData) spectrum).getValues();
					//System.out.println(((DoubleData)spectrum).getFirstSampleNumber());
					double[] intensities = new double[spectrumData.length];
					for (int i = 0; i < intensities.length; i++) {
						/*
						 * A very small intensity is, for all intents and
						 * purposes, the same as 0.
						 */
						intensities[i] = Math.max(Math.log(spectrumData[i]),
								0.0);
						if (intensities[i] > maxIntensity) {
							maxIntensity = intensities[i];
						}
						//fileOut.print(intensities[i] + " ");
					}
					
					columns.add(intensities);
					
					//fftList.add(intensities);
					counter++;
					//fileOut.println("" + counter);
				}
				
				spectrum = fft.getData();
				
			}
		} catch (Exception e) {
			System.exit(-1);
		}
		
		if (stereo)
			fK = (fK-1)/2+1;
		
		double [][] matrixFFT = new double[columns.size()][fK];
		
		for (int i = 0; i < matrixFFT.length; i ++){
			double [] c = columns.get(i);
			
			for (int j = 0; j < fK; j ++){
				matrixFFT[i][j] = c[j];
			}
		}
		return matrixFFT;
	}
	
	public static DiscreteFourierTransform getFFT(String filename, int numFftPoints){
		System.out.println(filename);
		File inputFile = new File(filename);
		AudioInputStream inStream = null;
		try{
			inStream = AudioSystem.getAudioInputStream(inputFile);
			
		}
		catch(Exception e){
			System.out.println(e.toString());
			System.out.println("Error initializing audio stream");
			System.exit(-1);
		}
		DiscreteFourierTransform fft = new DiscreteFourierTransform();
		fft.initialize(numFftPoints);
		
		StreamDataSource file = new StreamDataSource();
		file.setInputStream(inStream, "sound");
		file.initialize();
		Preemphasizer pre = new Preemphasizer();
		pre.initialize();
		pre.setPredecessor(file);
		RaisedCosineWindower windower = new RaisedCosineWindower();
	
		windower.initialize();
		windower.setPredecessor(file);
		fft.setPredecessor(windower);
		return fft;
	}
	
}

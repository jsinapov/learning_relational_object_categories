package AudioFFT;

public class HistogramDFT {


	public static double[] computeHistogramVector(double [][] data, int timeBins, int freqBins){
		double [][] histData = new double[timeBins][freqBins];
		
		int tBinLength = (int)Math.floor((double)data.length/(double)timeBins);
		int fBinLength = (int)Math.floor((double)data[0].length/(double)freqBins);
		
		//System.out.println("FWidth=" + fBinLength);
		//System.out.println("TWidth=" + tBinLength);
		
		for (int fIndex = 0; fIndex < freqBins; fIndex++){
			for (int tIndex = 0; tIndex < timeBins; tIndex++){
				int tStart = tIndex*tBinLength;
				int tEnd = Math.min((tIndex+1)*tBinLength, data.length);
				
				int fStart = fIndex*fBinLength;
				int fEnd = Math.min((fIndex+1)*(fBinLength), data[0].length);
				
				
				int c =0;
				double value = 0;
				for (int i = tStart; i < tEnd; i ++){
					for (int j = fStart; j < fEnd; j ++){
						value += data[i][j];
						c++;
					}
				}
				value = value/(double)c;
				
				//System.out.println(fIndex + "\t" + fStart + "\t" + fEnd);
				//System.out.println(tIndex + "\t" + tStart + "\t" + tEnd);
				
				
				histData[tIndex][fIndex] = value;
			}
		}
		
		double [] vector = new double[timeBins*freqBins];
		int c = 0;
		for (int i = 0; i <histData.length; i ++)
			for (int j = 0; j <histData[i].length; j++){
				vector[c]=histData[i][j];
				c++;
			}
		
		return vector;
	}
	
	public static double[][] computeHistogram(double [][] data, int timeBins, int freqBins){
		double [][] histData = new double[timeBins][freqBins];
		
		int tBinLength = (int)Math.floor((double)data.length/(double)timeBins);
		int fBinLength = (int)Math.floor((double)data[0].length/(double)freqBins);
		
		//System.out.println("FWidth=" + fBinLength);
		//System.out.println("TWidth=" + tBinLength);
		
		for (int fIndex = 0; fIndex < freqBins; fIndex++){
			for (int tIndex = 0; tIndex < timeBins; tIndex++){
				int tStart = tIndex*tBinLength;
				int tEnd = Math.min((tIndex+1)*tBinLength, data.length);
				
				int fStart = fIndex*fBinLength;
				int fEnd = Math.min((fIndex+1)*(fBinLength), data[0].length);
				
				
				int c =0;
				double value = 0;
				for (int i = tStart; i < tEnd; i ++){
					for (int j = fStart; j < fEnd; j ++){
						value += data[i][j];
						c++;
					}
				}
				value = value/(double)c;
				
				//System.out.println(fIndex + "\t" + fStart + "\t" + fEnd);
				//System.out.println(tIndex + "\t" + tStart + "\t" + tEnd);
				
				
				histData[tIndex][fIndex] = value;
			}
		}
		
		return histData;
		
		
	}
	

}

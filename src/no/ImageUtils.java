package no;

import java.util.ArrayList;

import no.hiof.imagepr.RGBImage;
import no.hiof.imagepr.filters.ImageScaler;

public class ImageUtils {

	public static RGBImage scaleImage(double s_x, double s_y, int method, RGBImage img){
		ImageScaler IS1 = new ImageScaler(s_x,s_y,method);
		img = (RGBImage)IS1.filter(img);
		return img;
	}
	
	public static RGBImage matrixToImageGray(double [][] data){
		RGBImage img = new RGBImage(data.length,data[0].length);
		
		//finding the min and max
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[i].length; j++){
				if (data[i][j] < min)
					min = data[i][j];
				
				if (data[i][j] > max)
					max = data[i][j];
			}
		}
		
		//set pixesl
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[i].length; j++){
				short value = (short) ((short)255-(short)Math.floor(255.0*(data[i][j]-min)/(max-min)));
				img.setPixelAt(i,j,value,value,value);
				
			}
		}
		
		
		return img;
	}
	
	public static RGBImage matrixToImageTransposed(double [][] data){
		double [][] new_data = new double[data[0].length][data.length];
		for (int i = 0; i < new_data.length; i++){
			for (int j = 0; j < new_data[i].length; j++){
				new_data[i][j]=data[j][i];
			}
		}
		
		data = new_data;
		
		//params for colormap
		double [] weights = {0.0,0.12,0.4,0.6,0.88,1.0};  
		//ArrayList<double[]> colors = new ArrayList<double[]>();
		double [][] colors = {   {0,0,143},{0,0,255},{0,255,255},{255,255,0},{255,0,0},{0,100,50}};
		
		
		RGBImage img = new RGBImage(data.length,data[0].length);
		
		//finding the min and max
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[i].length; j++){
				if (data[i][j] < min)
					min = data[i][j];
				
				if (data[i][j] > max)
					max = data[i][j];
			}
		}
		
		//set pixesl
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[i].length; j++){
				double scaled_value = (data[i][j]-min)/(max-min);
				double [] w = new double[weights.length];
				double sum = 0;
				for (int k = 0; k < w.length; k ++){
					w[k] = Math.pow(1.0-Math.abs(scaled_value-weights[k]),5.0);
					sum+=w[k];
				}
				
				for (int k = 0; k < w.length; k ++){
					w[k]=w[k]/sum;
				}
				
				double [] rgb = {0.0,0.0,0.0};
				for (int k = 0; k < w.length; k ++){
					
					for (int c=0; c < 3; c++){
						
						rgb[c]+=w[k]*colors[k][c];	
					}
				}
				
				//short value = (short) ((short)255-(short)Math.floor(255.0*(data[i][j]-min)/(max-min)));
				
				
				
				
				img.setPixelAt(data.length-1-i,j,(short)rgb[0],(short)rgb[1],(short)rgb[2]);
				
			}
		}
		
		
		return img;
	}
	
	public static RGBImage matrixToImage(double [][] data){
		//params for colormap
		double [] weights = {0.0,0.12,0.4,0.6,0.88,1.0};  
		//ArrayList<double[]> colors = new ArrayList<double[]>();
		double [][] colors = {   {0,0,143},{0,0,255},{0,255,255},{255,255,0},{255,0,0},{0,100,50}};
		
		
		RGBImage img = new RGBImage(data.length,data[0].length);
		
		//finding the min and max
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[i].length; j++){
				if (data[i][j] < min)
					min = data[i][j];
				
				if (data[i][j] > max)
					max = data[i][j];
			}
		}
		
		//set pixesl
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[i].length; j++){
				double scaled_value = (data[i][j]-min)/(max-min);
				double [] w = new double[weights.length];
				double sum = 0;
				for (int k = 0; k < w.length; k ++){
					w[k] = Math.pow(1.0-Math.abs(scaled_value-weights[k]),5.0);
					sum+=w[k];
				}
				
				for (int k = 0; k < w.length; k ++){
					w[k]=w[k]/sum;
				}
				
				double [] rgb = {0.0,0.0,0.0};
				for (int k = 0; k < w.length; k ++){
					
					for (int c=0; c < 3; c++){
						
						rgb[c]+=w[k]*colors[k][c];	
					}
				}
				
				//short value = (short) ((short)255-(short)Math.floor(255.0*(data[i][j]-min)/(max-min)));
				
				
				
				
				img.setPixelAt(data.length-1-i,j,(short)rgb[0],(short)rgb[1],(short)rgb[2]);
				
			}
		}
		
		
		return img;
	}
	
}

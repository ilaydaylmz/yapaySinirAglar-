package YSA2;
import java.io.*;
import java.util.Scanner;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;


public class Ysa 
{
	private static File egitimDosya =  new File(Ysa.class.getResource("egitim.txt").getPath());

	private static File testDosya =  new File(Ysa.class.getResource("test.txt").getPath());
	
	private double[] minumumlar;
	private double[] maksimumlar;
	
	private DataSet egitimVeriSeti;
	private DataSet testVeriSeti;
	
	private int araKatmanNoronSayisi;
	private MomentumBackpropagation mbp;
	
	public Ysa(int araKatmanNoronSayisi,double momentum,double ogrenmeKatsayisi,double maxHata,int epoch) throws FileNotFoundException
	{
		this.araKatmanNoronSayisi = araKatmanNoronSayisi;
		minumumlar = new double[8];
		maksimumlar = new double[8];
		for(int i=0;i<8;i++)
		{
			minumumlar[i] = Double.MAX_VALUE;
			maksimumlar[i] = Double.MIN_VALUE;
		}
		minimumVeMaksimumlarBul(egitimDosya);

		minimumVeMaksimumlarBul(testDosya);
		
		
		egitimVeriSeti = veriSetiOku(egitimDosya);

		testVeriSeti = veriSetiOku(testDosya);
		mbp = new MomentumBackpropagation();
		mbp.setLearningRate(ogrenmeKatsayisi);
		mbp.setMaxError(maxHata);
		mbp.setMaxIterations(epoch);
		mbp.setMomentum(momentum);
	}
	public void egit()
	{
		MultiLayerPerceptron sinirselAg = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,8,araKatmanNoronSayisi,3);
		sinirselAg.setLearningRule(mbp);
		sinirselAg.learn(egitimVeriSeti);
		System.out.println("egitim yamamlandi.");
		sinirselAg.save("model.nnet");
	}
	public double test()
	{
		NeuralNetwork sinirselAg  =   NeuralNetwork.createFromFile("model.nnet");
		double toplamHata =0;
		var satirlar =testVeriSeti.getRows();
		for(var satir : satirlar)
		{
			sinirselAg.setInput(satir.getInput());
			sinirselAg.calculate();
			toplamHata += mse(satir.getDesiredOutput(),sinirselAg.getOutput());
		}
		return toplamHata/testVeriSeti.size();
	}
	public double egitimHata()
	{
		return mbp.getTotalNetworkError();
	}
	public String tekSatirTest(double[] inputs)
	{
		double[] inputD = new double[inputs.length];
		for(int i=0;i<inputs.length;i++)
		{
			inputD[i] = minMax(inputs[i],minumumlar[i],maksimumlar[i]);
		}
		NeuralNetwork sinirselAg =  NeuralNetwork.createFromFile("model.nnet");
		sinirselAg.setInput(inputD);
		sinirselAg.calculate();
		return gercekCikti(sinirselAg.getOutput());
	}
	private String gercekCikti(double[] output)
	{
		 double max = Double.MIN_VALUE;
		 int maxIndex=0;
		 for(int i=0;i<output.length;i++)
		 {
			 if(output[i]>max)
			 {
				 max = output[i];
				 maxIndex = i;
			 }
		 }
		 switch(maxIndex)
		 {
		 	case 0:
		 		return "kotu";
		 	case 1:
		 		return "normal";
		 	case 2:
		 		return "iyi";
		 		default:
		 			return "hata";
		 }
		 
	}
	
	private double mse(double[] beklenen,double[] cikti)
	{
		double birSatirHata =0;
		for(int i=0;i<beklenen.length;i++)
		{
			birSatirHata +=  Math.pow(beklenen[i]- cikti[i],2);
		}
		return birSatirHata/3;
	}
	private DataSet veriSetiOku(File dosya) throws FileNotFoundException
	{
		Scanner in = new Scanner(dosya);
		DataSet ds = new DataSet(8,3);
		while(in.hasNextDouble())
		{
			double[] input = new double[8];
			for(int i=0;i<8;i++)
			{
				double d = in.nextDouble();
				input[i]= minMax(d,minumumlar[i],maksimumlar[i]);
			}
			double[] output = new double[3];
			for(int i=0;i<3;i++)
			{
				output[i]=in.nextDouble();
			}
			DataSetRow satir = new DataSetRow(input,output);
			ds.add(satir);
			
		}
		return ds;
	}
	private double minMax(double d,double min,double max)
	{
		return (d-min)/(max-min);
	}
	
	private void minimumVeMaksimumlarBul(File dosya) throws FileNotFoundException
	{
		Scanner in = new Scanner(dosya);
		while(in.hasNextDouble())
		{
			for(int i=0;i<8;i++)
			{
				double d = in.nextDouble();
				if(d<minumumlar[i]) minumumlar[i]= d;
				if(d>maksimumlar[i]) maksimumlar[i] = d;
				
			}
			in.nextDouble();in.nextDouble();in.nextDouble();
		}
	}
	
}

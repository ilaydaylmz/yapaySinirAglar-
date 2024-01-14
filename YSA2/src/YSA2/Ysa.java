package YSA2;
import java.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

public class Ysa {

	private static File veriDosya = new File("dosya.txt");

    private double[] minumumlar;
    private double[] maksimumlar;

    private DataSet egitimVeriSeti;
    private DataSet testVeriSeti;

    private int araKatmanNoronSayisi;
    private MomentumBackpropagation mbp;
    
    NeuralNetwork<BackPropagation> ann = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,2,5,1);
    BackPropagation bp = new BackPropagation();

    public Ysa(int araKatmanNoronSayisi, double momentum, double ogrenmeKatsayisi, double maxHata, int epoch)
            throws FileNotFoundException {
        this.araKatmanNoronSayisi = araKatmanNoronSayisi;
        minumumlar = new double[2];
        maksimumlar = new double[2];
        for (int i = 0; i < 2; i++) {
            minumumlar[i] = Double.MAX_VALUE;
            maksimumlar[i] = Double.MIN_VALUE;
        }
        
        minimumVeMaksimumlarBul(veriDosya);

        egitimVeriSeti = new DataSet(2, 1);
        testVeriSeti = new DataSet(2, 1);
        veriSetiniDuzenle(veriDosya);
        
        mbp = new MomentumBackpropagation();
        mbp.setLearningRate(ogrenmeKatsayisi);
        mbp.setMaxError(maxHata);
        mbp.setMaxIterations(epoch);
        mbp.setMomentum(momentum);
    }

    public void egit() {
        MultiLayerPerceptron sinirselAg = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 2,
                araKatmanNoronSayisi, 1);
        sinirselAg.setLearningRule(mbp);
        sinirselAg.learn(egitimVeriSeti);
        System.out.println("Eğitim tamamlandı.");
        sinirselAg.save("model.nnet");
    }

    public double test() {
    	NeuralNetwork<MomentumBackpropagation> sinirselAg  =   NeuralNetwork.createFromFile("model.nnet");
       // MultiLayerPerceptron sinirselAg = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile("model.nnet");
        double toplamHata = 0;
        var satirlar = testVeriSeti.getRows();
        for (var satir : satirlar) {
            sinirselAg.setInput(satir.getInput());
            sinirselAg.calculate();
            toplamHata += mse(satir.getDesiredOutput(), sinirselAg.getOutput());
        }
        return toplamHata / testVeriSeti.size();
    }

    public double egitimHata() {
        return mbp.getTotalNetworkError();
    }

    public String tekSatirTest(double[] inputs) {
        double[] inputD = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            inputD[i] = minMax(inputs[i], minumumlar[i], maksimumlar[i]);
        }
       // MultiLayerPerceptron sinirselAg = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile("model.nnet");
        NeuralNetwork<MomentumBackpropagation> sinirselAg  =   NeuralNetwork.createFromFile("model.nnet");
        sinirselAg.setInput(inputD);
        sinirselAg.calculate();
        return gercekCikti(sinirselAg.getOutput());
    }

    private String gercekCikti(double[] output) {
        double max = Double.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < output.length; i++) {
            if (output[i] > max) {
                max = output[i];
                maxIndex = i;
            }
        }
        switch (maxIndex) {
            case 0:
                return "bukulebilir";
            case 1:
                return "hafifsertlik";
            case 2:
                return "sert";
            case 3:
                return "ortasertlik";
            case 4:
                return "coksert";
            case 5:
                return "asirisert";
            default:
                return "hata";
        }
    }

    private double mse(double[] beklenen, double[] cikti) {
        double birSatirHata = 0;
        for (int i = 0; i < beklenen.length; i++) {
            birSatirHata += Math.pow(beklenen[i] - cikti[i], 2);
        }
        return birSatirHata / 3;
    }

    private void veriSetiniDuzenle(File dosya) throws FileNotFoundException {
        List<String> veriSatirlari = dosyadanVeriOku(dosya);
        Collections.shuffle(veriSatirlari);
     
        int egitimVeriSayisi = (int) (veriSatirlari.size() * 0.75);
      
        for (int i = 0; i < veriSatirlari.size(); i++) {
            double[] input = new double[2];
          
            double[] output = new double[1];
           

            String[] satirParcalari = veriSatirlari.get(i).split(" ");
           

           if (satirParcalari.length < 3) {
                // Satırda yeterli sayıda değer yoksa atla
            	
            	continue;
            }
            
            for (int j = 0; j < 2; j++) {
            	
                try {
                    double d = Double.parseDouble(satirParcalari[j]);
               
                    input[j] = minMax(d, minumumlar[j], maksimumlar[j]);
                } catch (NumberFormatException e) {
                    // Geçersiz bir sayı varsa, bu değeri 0 olarak ayarla veya başka bir değere karar ver
                
                    input[j] = 0.0;
                }
            }

            for (int j = 1; j < 2; j++) {
                try {
                    output[j - 1] = Double.parseDouble(satirParcalari[j]);
                } catch (NumberFormatException e) {
                    // Geçersiz bir sayı varsa, bu değeri 0 olarak ayarla veya başka bir değere karar ver
                    output[j - 1] = 0.0;
                }
            }

            DataSetRow satir = new DataSetRow(input, output);

            if (i < egitimVeriSayisi) {
                egitimVeriSeti.add(satir);
            } else {
                testVeriSeti.add(satir);
            }
        }
    }


    private List<String> dosyadanVeriOku(File dosya) throws FileNotFoundException {
        List<String> veriSeti = new ArrayList<>();

        Scanner in = new Scanner(dosya);
        while (in.hasNextLine()) {
        	 String satir = in.nextLine();
             veriSeti.add(satir);
        }

        return veriSeti;
    }

    private double minMax(double d, double min, double max) {
        return (d - min) / (max - min);
    }

    private void minimumVeMaksimumlarBul(File dosya) throws FileNotFoundException {
        Scanner in = new Scanner(dosya);
        while (in.hasNextDouble()) {
            for (int i = 0; i < 2; i++) {
                double d = in.nextDouble();
                if (d < minumumlar[i])
                    minumumlar[i] = d;
                if (d > maksimumlar[i])
                    maksimumlar[i] = d;
            }
            in.nextDouble();
            in.nextDouble();
            in.nextDouble();
        }
    }
    public void YsaWithoutMomentum(int araKatmanNoronSayisi, double ogrenmeKatsayisi, double maxHata, int epoch)
            throws FileNotFoundException {
        this.araKatmanNoronSayisi = araKatmanNoronSayisi;
        minumumlar = new double[8];

        maksimumlar = new double[8];
        for (int i = 0; i < 8; i++) {
            minumumlar[i] = Double.MAX_VALUE;
            maksimumlar[i] = Double.MIN_VALUE;
        }

        minimumVeMaksimumlarBul(veriDosya);

        egitimVeriSeti = new DataSet(2, 1);
        testVeriSeti = new DataSet(2, 1);
        veriSetiniDuzenle(veriDosya);

        bp = new BackPropagation();
        bp.setLearningRate(ogrenmeKatsayisi);
        bp.setMaxError(maxHata);
        bp.setMaxIterations(epoch);
    }

    public void newegit() {
        MultiLayerPerceptron sinirselAg = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 2,
                araKatmanNoronSayisi, 1);
        sinirselAg.setLearningRule(bp);
        sinirselAg.learn(egitimVeriSeti);

        System.out.println("Momentumsuz Eğitim tamamlandı.");
        sinirselAg.save("yeni_without_momentum.nnet");
    }

    public double newtest() {
        MultiLayerPerceptron sinirselAg = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile("yeni_without_momentum.nnet");
        double toplamHata = 0;
        var satirlar = testVeriSeti.getRows();

        for (var satir : satirlar) {
            sinirselAg.setInput(satir.getInput());
            sinirselAg.calculate();

            toplamHata += mse(satir.getDesiredOutput(), sinirselAg.getOutput());
        }

        return toplamHata / testVeriSeti.size();
    }

    public double newegitimHata() {
        return bp.getTotalNetworkError();
    }

    public String newtekSatirTest(double[] inputs) {
        double[] inputD = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            inputD[i] = minMax(inputs[i], minumumlar[i], maksimumlar[i]);
        }
        MultiLayerPerceptron sinirselAg = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile("yeni_without_momentum.nnet");
        sinirselAg.setInput(inputD);
        sinirselAg.calculate();
        return gercekCikti(sinirselAg.getOutput());
    }
}


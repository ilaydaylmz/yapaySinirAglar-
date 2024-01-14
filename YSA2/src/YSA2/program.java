package YSA2;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class program {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Ysa ysa = null;
		Scanner in = new Scanner(System.in);
		int araKatmanNoronSayisi;
		double momentum,ogrenmeKatsayisi,maxError;
		int epoch,sec;
		do{
			System.out.println("1.Egitim ve test");
			System.out.println("2. Tek satir test");
			System.out.println("3. cikis");
			sec = in.nextInt();
			switch(sec)
			{
				case 1:
					ysa = new Ysa(10,0.1,0.1,0.0001,1000);
					HataGrafik grafik = new HataGrafik("Eğitim ve Test Hata Grafiği", ysa);
					ysa.egit();
					System.out.println("Momentumlu eğitimdeki hata:"+ysa.egitimHata());
					System.out.println("Momentumlu test hata:"+ysa.test());
					ysa.YsaWithoutMomentum(10,0.4,0.0001,1000);
					ysa.newegit();
					System.out.println("Momentumsuz eğitimdeki hata:"+ysa.newegitimHata());
					System.out.println("Momentumsuz test hata:"+ysa.newtest());
					for(epoch=1; epoch<=100;epoch++)
					{
						double egitimHata = ysa.egitimHata();
		                double testHata = ysa.test();
		                grafik.ekleHata(egitimHata, testHata);
		                grafik.repaint();
		                try {
		                    Thread.sleep(100);
		                } catch (InterruptedException e) {
		                    e.printStackTrace();
		                }
					}
					grafik.grafikGoster();		
					break;
				case 2 :
					if(ysa!=null)
					{
						double[] inputs = new double[2];
						System.out.println("soguma hizi"); 
						inputs[0] = in.nextDouble();

						System.out.println("suda bekletilme");
						inputs[1] = in.nextDouble();

						String cikti = ysa.tekSatirTest(inputs);
						System.out.println("Cikti: "+cikti);
					}
					break;
			}
		}while(sec!=3);
	}
}

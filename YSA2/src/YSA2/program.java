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
					/*System.out.println("arakatman noron sayisi");
					araKatmanNoronSayisi = in.nextInt();

					System.out.println("momentum");
					momentum = in.nextDouble();

					System.out.println("ogrenme katsaiyisi");
					ogrenmeKatsayisi = in.nextDouble();
					
					System.out.println("max hata");
					maxError = in.nextDouble();
					
					System.out.println("epoch");
					epoch = in.nextInt();*/
					
					//ysa = new Ysa(araKatmanNoronSayisi,momentum,ogrenmeKatsayisi,maxError,epoch);
					ysa = new Ysa(20,0.4,0.1,0.0001,10000);
					
					ysa.egit();
					System.out.println("egitimdeki hata: "+ysa.egitimHata());
					System.out.println("test hata: "+ysa.test());
					
					break;
				case 2 :
					if(ysa!=null)
					{
						double[] inputs = new double[8];
						System.out.println("Silindir"); 
						
						inputs[0] = in.nextDouble();

						System.out.println("surtunme");

						inputs[1] = in.nextDouble();

						System.out.println("beygir gucu");

						inputs[2] = in.nextDouble();
						
						
						System.out.println("agirlik");
						inputs[3] = in.nextDouble();

						System.out.println("hizlanma");
						inputs[4] = in.nextDouble();

						System.out.println("model");
						inputs[5] = in.nextDouble();
						System.out.println("kıta");
						String kita = in.next();
						double[]d = ulkeSayisal(kita);
						inputs[6] = d[0];
						inputs[7] = d[1];
						String cikti = ysa.tekSatirTest(inputs);
						System.out.println("Cikti: "+cikti);
					}
					break;
			
			}
			
		}while(sec!=3);
	}
	public static double[] ulkeSayisal(String ulke)
	{
		double[] sayisal = new double[2];
		sayisal[0]=0;sayisal[1]=0;
		if(ulke.equals("Asia")) sayisal[1] = 1;
		if(ulke.equals("Europe")) sayisal[0] = 1;
		return sayisal;
	}
}

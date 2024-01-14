package YSA2;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

public class HataGrafik extends JFrame{
	
	private XYSeries egitimHataSerisi;
    private XYSeries testHataSerisi;
    private Ysa ysa;

    public HataGrafik(String title, Ysa ysa) {
        super(title);
        this.ysa = ysa;

        egitimHataSerisi = new XYSeries("Eğitim Hatası");
        testHataSerisi = new XYSeries("Test Hatası");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(egitimHataSerisi);
        dataset.addSeries(testHataSerisi);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Eğitim ve Test Hata Grafiği",
                "Epoch",
                "Hata",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(560, 370));
        setContentPane(chartPanel);
    }

    public void ekleHata(double egitimHata, double testHata) {
        int epoch = egitimHataSerisi.getItemCount() + 1;
        egitimHataSerisi.add(epoch, egitimHata);
        testHataSerisi.add(epoch, testHata);
    }

    public void grafikGoster() {
        SwingUtilities.invokeLater(() -> {
            this.setSize(800, 600);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.setVisible(true);
        });
    }

}

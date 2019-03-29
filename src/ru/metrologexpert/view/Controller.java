package ru.metrologexpert.view;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import ru.metrologexpert.DDS;
import ru.metrologexpert.Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Controller {

    @FXML
    private Button generate;

    public LineChart getLineChart() {
        return lineChart;
    }

    @FXML
    private LineChart lineChart;

    private Main mainApp;


    static String file = "error.txt";                       // File name
    static double F0 = 4000;
    static double N = F0/3;                                 // Number of phaseMax experiments
    static int M = 3;                                       // Number of amp experiments
    static double[][] errorArray = new double[(int)F0][M];

    public void setMainApp(Main main) {
        this.mainApp = main;

    }

    public void drawSineChart() {

        evalErrorArray();

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Синтезируемая частота");

        lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Зависимость СКО от синтезируемой частоты");
        lineChart.setCreateSymbols(false);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("DAC 8bit");

        for (int i = 0; i < N; i++) {
            series1.getData().add(new XYChart.Data(i, errorArray[i][0]));
        }
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("DAC 12bit");

        for (int i = 0; i < N; i++) {
            series2.getData().add(new XYChart.Data(i, errorArray[i][1]));
        }
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("DAC 16bit");

        for (int i = 0; i < N; i++) {
            series3.getData().add(new XYChart.Data(i, errorArray[i][2]));
        }
        lineChart.getData().addAll(series1, series2, series3);


    }

    void evalErrorArray() {
        int nPhase = 12;
        int nAmp;
        double fClk = 4000;
        for (int j = 0; j < M; j++) {
            nAmp = 8 + 4 * j;                                   //  ampMax on each step
            for (int i = 0; i < N; i++) {
                DDS dds = new DDS(nPhase, nAmp, fClk, i);
                dds.evalU();
                errorArray[i][j] = dds.getError();
            }
        }
    }

    void writeErrorFile() {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(file)));
            for (int i = 0; i < N; i++) {
                out.println(errorArray[i][0] + ", " + errorArray[i][1] + ", " + errorArray[i][2]);
            }
            out.close();
        } catch (IOException e) {
            System.err.print("File not created");
        }
    }
}

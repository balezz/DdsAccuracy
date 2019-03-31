package ru.metrologexpert.view;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import ru.metrologexpert.DDS;
import ru.metrologexpert.Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Controller {

    @FXML
    private LineChart lineChart0;

    @FXML
    private LineChart lineChart1;

    @FXML
    private LineChart lineChart2;

    @FXML
    private NumberAxis xAxis0;

    @FXML
    private NumberAxis yAxis0;

    @FXML
    private NumberAxis xAxis1;

    @FXML
    private NumberAxis yAxis1;

    @FXML
    private NumberAxis xAxis2;

    @FXML
    private NumberAxis yAxis2;

    private Main mainApp;

    private DDS dds;

    private static final String FILE_NAME = "error.txt";
    static final double F0 = 4000;
    private int N = (int) F0 / 3;                                 // Number of phaseMax experiments

    private double[] errorArray = new double[N];
    private int[] U = new int[N];

    /********************************* Constructors ************************/

    public Controller() { }


    /********************************** Methods ****************************/

    public void setMainApp(Main main) {
        this.mainApp = main;
    }

    @FXML
    private void initialize() {
    }

    public void drawLineChart0() {

        lineChart2.setCreateSymbols(false);

        evalErrorFrequency();

        XYChart.Series series1 = new XYChart.Series();
//        series1.setName("DAC 8bit");

        for (int i = 0; i < N; i++) {
            series1.getData().add(new XYChart.Data(i, errorArray[i]));
        }

        lineChart2.getData().addAll(series1);

    }

    public void drawLineChart1() {

        initialize();

        evalErrorFrequency();

        XYChart.Series series1 = new XYChart.Series();
//        series1.setName("DAC 8bit");

        for (int i = 0; i < N; i++) {
            series1.getData().add(new XYChart.Data(i, errorArray[i]));
        }

        lineChart2.getData().addAll(series1);

    }

    public void drawLineChart2() {

        lineChart2.setCreateSymbols(false);

        evalErrorFrequency();

        XYChart.Series series1 = new XYChart.Series();
//        series1.setName("DAC 8bit");

        for (int i = 0; i < N; i++) {
            series1.getData().add(new XYChart.Data(i, errorArray[i]));
        }

        lineChart2.getData().addAll(series1);

    }

    void evalErrorFrequency() {
        int nPhase = 12;
        int nAmp;
        double fClk = 4000;
        nAmp = 8;                                   //  ampMax on each step
        for (int i = 0; i < N; i++) {
            DDS dds = new DDS(nPhase, nAmp, fClk, i);
            dds.evalU();
            errorArray[i] = dds.getMse();
        }
    }

//    todo
    void evalSine() {
        dds = new DDS(8, 8, 4000, N);
        dds.evalU();
        U = dds.getU();
    }

//    todo
    void evalErrorCapacity() {

    }

//    todo: bind to button
    void writeFile() {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(FILE_NAME)));
            for (int i = 0; i < N; i++) {
                out.println(errorArray[i]);
            }
            out.close();
        } catch (IOException e) {
            System.err.print("File not created");
        }
    }

}

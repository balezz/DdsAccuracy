package ru.metrologexpert;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ErrorChartFx extends Application {

    static String file = "error.txt";                       // File name
    static int F0 = 1000;
    static int N = F0/3;                                      // Number of phaseMax experiments
    static int M = 3;                                       // Number of amp experiments
    static double[][] errorArray = new double[F0][M];

    public static void main(String[] args) {

        evalErrorArray();

        writeErrorFile();

        launch(args);                                       // Visualize window

    }

    /**
     * Visualize error
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Error Sine Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Синтезируемая частота");
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Зависимость СКО от синтезируемой частоты");
        lineChart.setCreateSymbols(false);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("АЦП 8 бит");

        for (int i = 0; i < N; i++) {
            series1.getData().add(new XYChart.Data(i, errorArray[i][0]));
        }
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("АЦП 12 бит");

        for (int i = 0; i < N; i++) {
            series2.getData().add(new XYChart.Data(i, errorArray[i][1]));
        }
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("АЦП 16 бит");

        for (int i = 0; i < N; i++) {
            series3.getData().add(new XYChart.Data(i, errorArray[i][2]));
        }

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().addAll(series1, series2, series3);

        stage.setScene(scene);
        stage.show();
    }

    static int powInt (int number, int power) {
        int result = 1;
        if(power < 0) return 0;
        else {
            for (int i = 0; i < power; i++) {
                result *= number;
            }
            return result;
        }
    }

    static void writeErrorFile() {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(file)));
            for (int i = 0; i < N; i++) {
                out.println(errorArray[i][0] + "; " + errorArray[i][1] + "; " + errorArray[i][2]);
            }
            out.close();
        } catch (IOException e) {
            System.err.print("File not created");
        }
    }

    static void evalErrorArray() {
        int nPhase = powInt(2, 12);      // delta phaseMax on each step
        for (int j = 0; j < M; j++) {
            int nAmp = powInt(2, 8 + 4*j);       // delta ampMax on each step
            for (int i = 1; i < F0; i++) {
                DDS dds = new DDS(nPhase, nAmp, N, i);
                errorArray[i][j] = dds.getError();
            }
        }
    }
}

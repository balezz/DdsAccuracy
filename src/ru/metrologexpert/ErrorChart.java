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

/**
 * Class intended for plotting dependence between standard error and
 * digit capacity of phaseMax register
 */
public class ErrorChart extends Application {

    static String file = "out/error.txt";                       // File name
    static int N = 23;                                      // Number of phaseMax experiments
    static int M = 3;                                       // Number of amp experiments
    static double[][] errorArray = new double[N][M];

    public static void main(String[] args) {
        evalErrorArray();
        //writeErrorFile();
        // System.exit(0);
        launch(args); // Visualize window
    }

    /**
     * Visualize error with JavaFX
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Error Sine Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Разрядность регистра фазы i + 8");
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Зависимость СКО от разрядности регистра фазы");
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
        int nPhase;
        int nAmp;
        for (int j = 0; j < M; j++) {
            nAmp = 8 + 4 * j;                                   //  ampMax on each step
            for (int i = 8; i < N; i++) {
                DDS dds = new DDS(i, nAmp, 4000, 350);
                dds.evalU();
                errorArray[i][j] = dds.getError();
            }
        }
    }

    /**
     * @param power - degree of 2
     * @return 2 in power degree
     */
    static int twoPow (int power) {
        int result = 1;
        if(power < 0) return 0;
        else {
            for (int i = 0; i < power; i++) {
                result *= 2;
            }
            return result;
        }
    }
}

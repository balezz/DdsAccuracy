package ru.metrologexpert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends Application {
    static String file = "error.txt";                       // File name
    static double F0 = 4000;
    static double N = F0/3;                                      // Number of phaseMax experiments
    static int M = 3;                                       // Number of amp experiments
    static double[][] errorArray = new double[(int)F0][M];


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainApp.fxml"));
        primaryStage.setTitle("Direct Digital Synthesis");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        primaryStage.setTitle("Error Sine Chart Sample");
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

        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
    static void evalErrorArray() {
        int nPhase;
        int nAmp;
        for (int j = 0; j < M; j++) {
            nAmp = 8 + 4 * j;                                   //  ampMax on each step
            for (int i = 0; i < N; i++) {
                DDS dds = new DDS(12, nAmp, 4000, i);
                dds.evalU();
                errorArray[i][j] = dds.getError();
            }
        }
    }
    static void writeErrorFile() {
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

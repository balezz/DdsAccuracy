package ru.metrologexpert;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/**
 * Chart sine reference and LookUpTable value
 */
public class SineChart extends Application {

    DDS dds = new DDS(15, 12, 1000, 12);
    int N = (int)(dds.fClk /dds.fOut);

    public static void main(String[] args) {
        launch(args); // Visualize window
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Sine Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Значение фазы");
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Табличное и точное значение синуса");
        lineChart.setCreateSymbols(false);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Табличное значение синуса");
        for (int i = 0; i < N; i++) {
            series1.getData().add(new XYChart.Data(i, dds.U[i]));
        }

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Точное значение синуса");
        for (int i = 0; i < N; i++) {
            series2.getData().add(new XYChart.Data(i, dds.Uref[i]));
        }

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().addAll(series1, series2);

        stage.setScene(scene);
        stage.show();
    }
}

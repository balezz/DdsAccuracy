package ru.metrologexpert.view;


import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import ru.metrologexpert.DDS;
import ru.metrologexpert.Main;
import ru.metrologexpert.lut.LutType;

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
    private Spinner spinnerF0;

    @FXML
    private Spinner spinnerFx;

    @FXML
    private Slider sliderAmp;

    @FXML
    private Slider sliderPhase;

    @FXML
    private Button buttonRefresh;

    @FXML
    private Button buttonSave;

    @FXML
    private ChoiceBox<LutType> choiceBox;

    @FXML
    private Label ampLabel;

    @FXML
    private Label phaseLabel;


    private Main mainApp;

    private DDS dds;

    private int nPhase = 12;
    private int nAmp = 12;

    private static final String FILE_NAME = "error.txt";
    static int F0 = 4000;
    static int kF = 100;
    static int Fx = F0 / kF;
    private int N = Fx;


    /********************************* Constructors ************************/

    public Controller() { }


    /********************************** Methods ****************************/

    public void setMainApp(Main main) {
        this.mainApp = main;
    }

    @FXML
    private void initialize() {

        SpinnerValueFactory<Integer> f0valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5000, F0);
        spinnerF0.setValueFactory(f0valueFactory);


        SpinnerValueFactory<Integer> fxvalueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, F0 / 3, Fx);
        spinnerFx.setValueFactory(fxvalueFactory);

        choiceBox.getItems().setAll(LutType.values());
        choiceBox.setValue(LutType.SIMPLE);

        sliderAmp.valueProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val)
                        -> {nAmp = new_val.intValue();
                            ampLabel.setText(String.valueOf(nAmp));}
        );

        sliderPhase.valueProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val)
                        -> {nPhase = new_val.intValue();
                    phaseLabel.setText(String.valueOf(nPhase));}
        );

        buttonRefresh.setOnAction(
                (ActionEvent e) -> {
                    F0 = (int) spinnerF0.getValue();
                    Fx = (int) spinnerFx.getValue();
                    drawLineChart0();
                    drawLineChart1();
                    drawLineChart2();
                }
        );
    }

    /**
     * Draws MSE depends onto phase digit capacity
     * */
    public void drawLineChart0() {

        lineChart0.setCreateSymbols(false);
        lineChart0.setLegendVisible(false);
        lineChart0.getData().removeAll(lineChart0.getData());

        XYChart.Series series0 = new XYChart.Series();

        for (int i = 8; i < 24; i++) {
            dds = new DDS(i, nAmp, F0, Fx, choiceBox.getValue());
            dds.evalU();
            double mse = dds.getMse();
            series0.getData().add(new XYChart.Data(i, mse));
        }

        lineChart0.getData().addAll(series0);

    }

    /**
     * Draws one period of generated signal
     * */
    public void drawLineChart1() {

        lineChart1.setCreateSymbols(false);
        lineChart1.setLegendVisible(false);
        lineChart1.getData().removeAll(lineChart1.getData());
        XYChart.Series series1 = new XYChart.Series();

        dds = new DDS(nPhase, nAmp, F0, N, choiceBox.getValue());
        dds.evalU();
        int[] U = dds.getU();
        for (int i = 0; i <= kF; i++) {
            series1.getData().add(new XYChart.Data(i, U[i]));
        }

        lineChart1.getData().addAll(series1);

    }

    /**
     * Draws MSE depends onto signal frequency
     * */
    public void drawLineChart2() {

        lineChart2.setCreateSymbols(false);
        lineChart2.setLegendVisible(false);
        lineChart2.getData().removeAll(lineChart2.getData());

        XYChart.Series series2 = new XYChart.Series();

        for (int i = 0; i < N; i++) {
            dds = new DDS(nPhase, nAmp, F0, i, choiceBox.getValue());
            dds.evalU();
            double mse = dds.getMse();
            series2.getData().add(new XYChart.Data(i, mse));
        }

        lineChart2.getData().addAll(series2);

    }


    void evalErrorCapacity() {

    }

//    todo: bind to button
    void writeFile() {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(FILE_NAME)));
            for (int i = 0; i < N; i++) {
                out.println(i);
            }
            out.close();
        } catch (IOException e) {
            System.err.print("File not created");
        }
    }


}

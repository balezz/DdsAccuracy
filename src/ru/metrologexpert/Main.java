package ru.metrologexpert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.metrologexpert.view.Controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Direct Digital Synthesis");

        initRootLayout();

    }

    public static void main(String[] args) {
        launch(args);
    }

    void initRootLayout() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/rootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            showSineChart();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSineChart() {
        Controller controller = new Controller();
        controller.setMainApp(this);
        controller.drawSineChart();
        LineChart lineChart = controller.getLineChart();
        rootLayout.setCenter(lineChart);
    }

}

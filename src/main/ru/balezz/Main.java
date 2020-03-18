package ru.balezz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.balezz.view.Controller;

import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private Controller controller;


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Модель цифрового синтезатора частоты");

        initRootLayout();

    }

    public static void main(String[] args) {
        launch(args);
    }

    void initRootLayout() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/res/rootLayout.fxml"));
            rootLayout = (AnchorPane) loader.load();

            controller = loader.getController();
            controller.setMainApp(this);

            controller.drawLineChart0();
            controller.drawLineChart1();
            controller.drawLineChart2();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

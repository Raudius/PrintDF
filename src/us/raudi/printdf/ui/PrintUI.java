package us.raudi.printdf.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PrintUI extends Application {

	UiController controller;
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintUI.fxml"));
		Scene scene = new Scene(loader.load());

		controller = loader.getController();
		controller.setStage(primaryStage);
		

		Image img = new Image(getClass().getResource("PrintDF.png").toExternalForm());

		primaryStage.setTitle("PrintDF");
		primaryStage.getIcons().add(img);
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	@Override
	public void stop(){
	    if(controller != null) {
			try {
				controller.saveSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}

	public static void main(String[] args) {
		launch(args);
	}
}

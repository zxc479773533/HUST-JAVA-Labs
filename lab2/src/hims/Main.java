package hims;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Connect to database
		try {
			DBConnector.getInstance().connectDataBase("localhost", 3306, "java_lab2", "zxcpyp", "123456");
		} catch (SQLException e) {
			System.err.println("failed to connect to sql database");
			System.exit(0);
		}
		// Start JFX
		Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
		primaryStage.setTitle("医院简易挂号管理系统");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

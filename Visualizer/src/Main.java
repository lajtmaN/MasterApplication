import Helpers.ConnectedGraphGenerator;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        Parent root = FXMLLoader.load(getClass().getResource("View/TopologyViewer/TopologyViewer.fxml"));
        primaryStage.setTitle("UPPAAL Visualization Application");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();

        //Get matrix for connected graph square topology. Input nr of nodes
        //ConnectedGraphGenerator.matrix(16);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

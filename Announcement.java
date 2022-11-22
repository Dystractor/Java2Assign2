package application.pane;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataOutputStream;
import java.io.IOException;

public class Announcement {
    private Stage announcement = new Stage();
    private Scene scene =  new Scene(new Pane(),280,200);
    private Label text= new Label();
    private Button bottomYes = new Button("OK");
    public Announcement() {
        announcement.setResizable(false);
        announcement.initStyle(StageStyle.UNDECORATED);
        announcement.initModality(Modality.APPLICATION_MODAL);
        announcement.setX(Player.x);announcement.setY(Player.y);
        announcement.setScene(scene);
    }
    public void display(String s) {
        bottomYes.setOnAction(event -> announcement.close());text.setText(s);
        scene.setRoot(new StackPane(new VBox(new StackPane(text),new StackPane(bottomYes))));
        announcement.showAndWait();
    }
}

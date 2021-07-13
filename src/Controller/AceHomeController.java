package Controller;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class AceHomeController {


    public AnchorPane root;
    public ImageView membership;
    public ImageView equipment;
    public ImageView eqp_borrowed;
    public ImageView eqp_returned;
    public ImageView eqp_search;

    //navigate windows
    public void navigate(MouseEvent event) throws IOException {

        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            Parent root= null;

            switch (icon.getId()) {

                case "membership":
                    root = FXMLLoader.load(this.getClass().getResource("/View/MembershipView.fxml"));
                    break;
                case "equipment":
                    root = FXMLLoader.load(this.getClass().getResource("/View/EquipmentView.fxml"));
                    break;
                case "eqp_borrowed":
                    root = FXMLLoader.load(this.getClass().getResource("/View/EquipmentBorrowedView.fxml"));
                    break;
                case "eqp_returned":
                    root = FXMLLoader.load(this.getClass().getResource("/View/EquipmentReturnedView.fxml"));
                    break;
                case "eqp_search":
                    root = FXMLLoader.load(this.getClass().getResource("/View/EquipmentSearchView.fxml"));
                    break;
            }

            if (root != null) {
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();

            }
        }
    }

    //mouse hover animations
    public void playMouseEnterAnimation(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();
            icon.setEffect(null);

            DropShadow glow = new DropShadow();
            glow.setColor(Color.RED);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }

    public void playMouseExitAnimation(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.YELLOW);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }

    public void logout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You are logging Out");
        alert.setHeaderText(null);
        alert.setContentText("Come back Soon!");
        alert.showAndWait();
        Parent root;
        try
        {
            root = FXMLLoader.load(getClass().getResource("/View/LoginScreenView1.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
            tt.setFromX(-scene.getWidth());
            tt.setToX(0);
            tt.play();
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        } catch (IOException ioe)
        {
            ioe.printStackTrace(System.out);
        }
    }
    }


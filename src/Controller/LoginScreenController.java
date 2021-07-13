package Controller;


import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class LoginScreenController{
        @FXML
        private TextField user;
        @FXML private TextField password;
        @FXML private Button loginButton;
    @FXML
    public Button exitButton;

    @FXML
    public void exitAction(ActionEvent closeEvent) throws SQLException, ClassNotFoundException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You are Exiting");
        alert.setHeaderText(null);
        alert.setContentText("See you Soon!");
        Optional<ButtonType> result = alert.showAndWait();
        Stage closeStage = (Stage) exitButton.getScene().getWindow();
        closeStage.close();
               }
            public void loginAction(javafx.event.ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con= DriverManager.getConnection("jdbc:mysql://localhost:3307/aceacademy", "root", "");
            ps= con.prepareStatement("SELECT * FROM userdata WHERE username=? and password=?");
            ps.setString(1,user.getText());
            ps.setString(2,password.getText());
            rs=ps.executeQuery();
            if (rs.next()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Approved");
                alert.setHeaderText(null);
                alert.setContentText("Welcome to Ace Academy!");
                alert.showAndWait();
             Parent root;
                try
                   {
                       root = FXMLLoader.load(getClass().getResource("/View/AceHomeView.fxml"));
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
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Please Try Again!");
                alert.showAndWait();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }


}

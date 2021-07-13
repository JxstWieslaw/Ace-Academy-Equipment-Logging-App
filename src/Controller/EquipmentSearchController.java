package Controller;

import Model.EquipmentModel;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class EquipmentSearchController{
    public JFXTextField eqp_search;
    public TableView<EquipmentModel> eqp_table;
    public AnchorPane search_root;
    private Connection connection;

    public void initialize() {

        eqp_table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        eqp_table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        eqp_table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("branding"));
        eqp_table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/aceacademy", "root", "");
            ObservableList<EquipmentModel> equipment = eqp_table.getItems();

            String sql = "SELECT * from equipmentdetail";
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();
            while (rst.next()) {
                System.out.println("Initializer");
                equipment.add(new EquipmentModel(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
            }
            eqp_table.setItems(equipment);
            //connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        eqp_search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String searchText = eqp_search.getText();

                try {
                    eqp_table.getItems().clear();


                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/aceacademy", "root", "");
                        String sql = "Select * FROM equipmentdetail where id like ? OR description like ? OR branding like ?";
                        PreparedStatement pstm = connection.prepareStatement(sql);
                        String like = "%" + searchText + "%";
                        pstm.setString(1, like);
                        pstm.setString(2, like);
                        pstm.setString(3, like);

                        ResultSet rst = pstm.executeQuery();

                        ObservableList tbl = eqp_table.getItems();
                        tbl.clear();

                        while (rst.next()) {
                            tbl.add(new EquipmentModel(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
                        }

                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                } catch (NullPointerException n) {
                    return;
                }

            }
        });


    }

    public void img_bk(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/AceHomeView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.search_root.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.GREEN);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }
}


package Controller;

import Model.EquipmentReturnedModel;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class EquipmentReturnedController {
    public AnchorPane Returnroot;
    public JFXTextField txt_brw_date;
    public JFXTextField txt_fine;
    public JFXDatePicker txt_rtn_date;
    public TableView<EquipmentReturnedModel> rt_tbl;
    public JFXComboBox eqp_brwd_id;
    private Connection connection;

    public void initialize() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");

        rt_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        rt_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        rt_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("returnedDate"));
        rt_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("fine"));

        try {
            connection = DBConnection.getInstance().getConnection();
            ObservableList<EquipmentReturnedModel> returns = rt_tbl.getItems();

            String sql = "SELECT * from equipmentreturn";
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();
            while (rst.next()) {
                float fine = Float.parseFloat(rst.getString(4));
                returns.add(new EquipmentReturnedModel(rst.getString(1), rst.getString(2), rst.getString(3), fine));
            }
            rt_tbl.setItems(returns);

            eqp_brwd_id.getItems().clear();
            ObservableList cmbIssue = eqp_brwd_id.getItems();
            String sql2 = "select borrowedId from equipmentborrowed";
            PreparedStatement pstm1 = connection.prepareStatement(sql2);
            ResultSet rst1 = pstm1.executeQuery();

            while (rst1.next()) {
                cmbIssue.add(rst1.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        eqp_brwd_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (eqp_brwd_id.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                try {
                    String sql = "select date from equipmentborrowed where borrowedId =?";
                    PreparedStatement pstm = connection.prepareStatement(sql);
                    pstm.setString(1, (String) eqp_brwd_id.getSelectionModel().getSelectedItem());
                    ResultSet rst = pstm.executeQuery();
                    if (rst.next()) {
                        txt_brw_date.setText(rst.getString(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        txt_rtn_date.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {

                if (txt_rtn_date.getValue() == null) {
                    return;
                }

                LocalDate returned = txt_rtn_date.getValue();
                LocalDate issued = LocalDate.parse(txt_brw_date.getText());

                Date date1 = Date.valueOf(issued);
                Date date2 = Date.valueOf(returned);

                long diff = date2.getTime() - date1.getTime();

                System.out.println(TimeUnit.MILLISECONDS.toDays(diff));
                int dateCount = (int) TimeUnit.MILLISECONDS.toDays(diff);
                float fine = 0;

                if (dateCount > 14) {
                    fine = dateCount * 15;
                }
                txt_fine.setText(Float.toString(fine));
            }
        });
    }

    //btn new action
    public void btn_new(ActionEvent actionEvent) {
        txt_fine.clear();
        txt_brw_date.setPromptText("Date Borrowed");
        eqp_brwd_id.getSelectionModel().clearSelection();
        txt_rtn_date.setPromptText("Date Returned");
    }

    //btn add action
    public void btn_add_inveb(ActionEvent actionEvent) throws SQLException {
        if (eqp_brwd_id.getSelectionModel().isEmpty() ||
                txt_brw_date.getText().isEmpty() ||
                txt_rtn_date.getValue() == null ||
                txt_fine.getText().isEmpty()
        ) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please fill details.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            System.out.println("Fill Your empty fields!");
            return;
        }

        String issueID = (String) eqp_brwd_id.getSelectionModel().getSelectedItem();
        String sql = "INSERT INTO equipmentreturn VALUES (?,?,?,?)";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, (String) eqp_brwd_id.getSelectionModel().getSelectedItem());
        pstm.setString(2, txt_brw_date.getText());
        pstm.setString(3, txt_rtn_date.getValue().toString());
        pstm.setString(4, txt_fine.getText());
        int affectedRows = pstm.executeUpdate();

        if (affectedRows > 0) {
            System.out.println("successful");
            String sql4 = "Update equipmentdetail SET status=? where id=?";
            PreparedStatement pstm2 = connection.prepareStatement(sql4);

            String sql3 = "select equipmentid from equipmentborrowed where borrowedId=?";
            PreparedStatement pstm3 = connection.prepareStatement(sql3);
            pstm3.setString(1, (String) eqp_brwd_id.getSelectionModel().getSelectedItem());
            ResultSet rst3 = pstm3.executeQuery();
            String id = null;

            if (rst3.next()) {
                id = rst3.getString(1);
            }
            pstm2.setString(1, "Available");
            pstm2.setString(2, id);
            int affected = pstm2.executeUpdate();
            if (affected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Status updated.",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
            }
        } else {
            System.out.println("Something went wrong");
        }
        try {
            rt_tbl.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection.close();
        eqp_brwd_id.getItems().remove(issueID);
        eqp_brwd_id.getSelectionModel().clearSelection();
        txt_brw_date.clear();
        txt_fine.clear();
        txt_rtn_date.getEditor().clear();
    }

    public void img_back(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/AceHomeView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.Returnroot.getScene().getWindow();
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
            glow.setColor(Color.YELLOW);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }
}

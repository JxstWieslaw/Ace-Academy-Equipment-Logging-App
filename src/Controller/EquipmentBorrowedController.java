package Controller;

import Model.EquipmentBorrowedModel;
import Model.EquipmentModel;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import db.DB;
import db.DBConnection;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class EquipmentBorrowedController {
    public JFXTextField txt_brw_id;
    public JFXDatePicker txt_brw_date;
    public JFXTextField txt_name;
    public JFXTextField txt_descpn;
    public JFXComboBox member_brw_id;
    public JFXComboBox equipment_id;
    public TableView<EquipmentBorrowedModel> eqp_brw_tbl;
    public AnchorPane eqp_brw;
    private Connection connection;

    //JDBC
    private PreparedStatement selectALl;
    private PreparedStatement selectmemID;
    private PreparedStatement selecteqdtl;
    private PreparedStatement table;
    private PreparedStatement delete;

    public void initialize() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");

        eqp_brw_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("brwId"));
        eqp_brw_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("date"));
        eqp_brw_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("memberId"));
        eqp_brw_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("eqpId"));


        try {
            connection = DBConnection.getInstance().getConnection();
            ObservableList<EquipmentBorrowedModel> issued = eqp_brw_tbl.getItems();

            selectALl = connection.prepareStatement("SELECT * FROM equipmentborrowed");
            selectmemID = connection.prepareStatement("select name from membershipdetail where id=?");
            selecteqdtl = connection.prepareStatement("select description,status from equipmentdetail where id=?");
            table = connection.prepareStatement("INSERT INTO equipmentborrowed values(?,?,?,?)");
            delete = connection.prepareStatement("DELETE FROM equipmentborrowed WHERE borrowedId=?");
            ResultSet rst = selectALl.executeQuery();

            while (rst.next()) {
                System.out.println("load");
                issued.add(new EquipmentBorrowedModel(rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)));
            }

            eqp_brw_tbl.setItems(issued);
            member_brw_id.getItems().clear();
            ObservableList cmbmembers = member_brw_id.getItems();
            String sql2 = "select id from membershipdetail";
            PreparedStatement pstm1 = connection.prepareStatement(sql2);
            ResultSet rst1 = pstm1.executeQuery();

            while (rst1.next()) {
                cmbmembers.add(rst1.getString(1));
            }

            equipment_id.getItems().clear();
            ObservableList cmbequipment = equipment_id.getItems();
            String sql3 = "select id from equipmentdetail";
            PreparedStatement pstm2 = connection.prepareStatement(sql3);
            ResultSet rst2 = pstm2.executeQuery();
            while (rst2.next()) {
                cmbequipment.add(rst2.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        member_brw_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                if (member_brw_id.getSelectionModel().getSelectedItem() != null) {
                    Object selectedItem = member_brw_id.getSelectionModel().getSelectedItem();
                    if (selectedItem.equals(null) || member_brw_id.getSelectionModel().isEmpty()) {
                        return;
                    }
                    try {
                        selectmemID.setString(1, selectedItem.toString());
                        ResultSet rst = selectmemID.executeQuery();

                        if (rst.next()) {
                            txt_name.setText(rst.getString(1));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        equipment_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (equipment_id.getSelectionModel().getSelectedItem() != null) {
                    Object selectedItem = equipment_id.getSelectionModel().getSelectedItem();

                    try {
                        txt_descpn.clear();
                        selecteqdtl.setString(1, selectedItem.toString());
                        ResultSet rst = selecteqdtl.executeQuery();

                        if (rst.next()) {
                            if (rst.getString(2).equals("Available")) {
                                txt_descpn.setText(rst.getString(1));
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR,
                                        "The equipment isn't available!",
                                        ButtonType.OK);
                                Optional<ButtonType> buttonType = alert.showAndWait();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //button new action
    public void new_action(ActionEvent actionEvent) throws SQLException {
        txt_descpn.clear();
        txt_name.clear();
        member_brw_id.getSelectionModel().clearSelection();
        equipment_id.getSelectionModel().clearSelection();
        txt_brw_date.setPromptText("Date Borrowing");

        String sql = "Select borrowedId from equipmentborrowed";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();

        String ids = null;
        int maxId = 0;

        while (rst.next()) {
            ids = rst.getString(1);

            int id = Integer.parseInt(ids.replace("I", ""));
            if (id > maxId) {
                maxId = id;
            }
        }
        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "I00" + maxId;
        } else if (maxId < 100) {
            id = "I0" + maxId;
        } else {
            id = "I" + maxId;
        }
        txt_brw_id.setText(id);
    }

    //button add action
    public void add_Action(ActionEvent actionEvent) throws SQLException {

        ObservableList<EquipmentBorrowedModel> issued = FXCollections.observableList(DB.borrowed);
        ObservableList<EquipmentModel> books = FXCollections.observableList(DB.equipment);

        if (txt_brw_id.getText().isEmpty() ||
                equipment_id.getSelectionModel().getSelectedItem().equals(null) ||
                member_brw_id.getSelectionModel().getSelectedItem().equals(null)
                || txt_brw_date.getValue().toString().equals(null)) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please fill your details.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        } else {
            String memberId = (String) member_brw_id.getSelectionModel().getSelectedItem();
            String eqpId = (String) equipment_id.getSelectionModel().getSelectedItem();
            issued.add(new EquipmentBorrowedModel(txt_brw_id.getText(), txt_brw_date.getValue().toString(), memberId, eqpId));

            try {
                table.setString(1, txt_brw_id.getText());
                table.setString(2, txt_brw_date.getValue().toString());
                table.setString(3, (String) member_brw_id.getSelectionModel().getSelectedItem());
                table.setString(4, (String) equipment_id.getSelectionModel().getSelectedItem());
                int affectedRows = table.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Data insertion successfull");
                    String sql2 = "Update equipmentdetail SET status=? where id=?";
                    PreparedStatement pstm2 = connection.prepareStatement(sql2);
                    String id = (String) equipment_id.getSelectionModel().getSelectedItem();
                    pstm2.setString(1, "Unavailable");
                    pstm2.setString(2, id);
                    int affected = pstm2.executeUpdate();

                    if (affected > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "Status updated.",
                                ButtonType.OK);
                        Optional<ButtonType> buttonType = alert.showAndWait();
                    } else {
                        System.out.println("ERROR");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            eqp_brw_tbl.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //button delete action
    public void delete_Action(ActionEvent actionEvent) throws SQLException {
        //BookIssueTM selectedItem = (BookIssueTM) FXCollections.observableList(DB.issued);
        EquipmentBorrowedModel selectedItem = eqp_brw_tbl.getSelectionModel().getSelectedItem();
        if (eqp_brw_tbl.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please select a row.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        } else {
            try {
                delete.setString(1, selectedItem.getBrwId());
                delete.executeUpdate();

                String sql2 = "Update equipmentdetail SET status=? where id=?";
                PreparedStatement pstm2 = connection.prepareStatement(sql2);
                String id = (String) equipment_id.getSelectionModel().getSelectedItem();
                pstm2.setString(1, "Available");
                pstm2.setString(2, id);
                pstm2.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Record deleted!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        try {
            eqp_brw_tbl.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void back_click(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/AceHomeView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.eqp_brw.getScene().getWindow();
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

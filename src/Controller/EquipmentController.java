package Controller;

import Model.EquipmentModel;
import com.jfoenix.controls.JFXButton;
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

public class EquipmentController {
    public JFXTextField eqp_id;
    public JFXTextField eqp_dscpn;
    public JFXTextField eqp_brand;
    public JFXTextField eqp_st;
    public TableView<EquipmentModel> eqp_table;
    public AnchorPane eqp_root;
    public JFXButton btn_add;
    private Connection connection;

    //JDBC
    private PreparedStatement selectall;
    private PreparedStatement selectID;
    private PreparedStatement newIdQuery;
    private PreparedStatement addToTable;
    private PreparedStatement updateQuarary;
    private PreparedStatement deleteQuarary;

    public void initialize() throws ClassNotFoundException {
        //disable id field
        eqp_id.setDisable(true);

        //load table
        eqp_table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        eqp_table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        eqp_table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("branding"));
        eqp_table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));

        Class.forName("com.mysql.jdbc.Driver");

        try {
            connection = DBConnection.getInstance().getConnection();
            selectall = connection.prepareStatement("SELECT * from equipmentdetail");
            updateQuarary = connection.prepareStatement("UPDATE equipmentdetail SET description=? , branding=? , status=? where id=?");
            selectID = connection.prepareStatement("select * from equipmentdetail where id=?");
            addToTable = connection.prepareStatement("INSERT INTO equipmentdetail values(?,?,?,?)");
            newIdQuery = connection.prepareStatement("SELECT id from equipmentdetail");
            deleteQuarary = connection.prepareStatement("DELETE from equipmentdetail where id=?");
            ObservableList<EquipmentModel> members = eqp_table.getItems();
            ResultSet rst = selectall.executeQuery();
            while (rst.next()) {
                System.out.println("load");
                members.add(new EquipmentModel(
                        rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)
                ));
            }
            eqp_table.setItems(members);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        eqp_table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EquipmentModel>() {
            @Override
            public void changed(ObservableValue<? extends EquipmentModel> observable, EquipmentModel oldValue, EquipmentModel newValue) {
                EquipmentModel selectedItem = eqp_table.getSelectionModel().getSelectedItem();
                try {
                    connection = null;
                    try {
                        selectID.setString(1, selectedItem.getId());
                        ResultSet rst = selectID.executeQuery();

                        if (rst.next()) {
                            eqp_id.setText(rst.getString(1));
                            eqp_dscpn.setText(rst.getString(2));
                            eqp_brand.setText(rst.getString(3));
                            eqp_st.setText(rst.getString(4));
                            eqp_id.setDisable(true);
                            btn_add.setText("Update");
                        }
                        btn_add.setText("Update");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (NullPointerException n) {
                    return;
                }
            }
        });
    }

    //button new action
    public void btn_new(ActionEvent actionEvent) throws SQLException {
        btn_add.setText("Add");
        eqp_st.setText("Available");
        eqp_st.setDisable(true);
        eqp_id.setDisable(false);
        eqp_brand.clear();
        eqp_dscpn.clear();
        eqp_dscpn.requestFocus();

        ResultSet rst = newIdQuery.executeQuery();

        String ids = null;
        int maxId = 0;

        while (rst.next()) {
            ids = rst.getString(1);

            int id = Integer.parseInt(ids.replace("E", ""));
            if (id > maxId) {
                maxId = id;
            }
        }
        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "E00" + maxId;
        } else if (maxId < 100) {
            id = "E0" + maxId;
        } else {
            id = "E" + maxId;
        }
        eqp_id.setText(id);
    }

    //button add action
    public void btn_Add(ActionEvent actionEvent) throws SQLException {
        ObservableList<EquipmentModel> books = FXCollections.observableList(DB.equipment);

        if (eqp_id.getText().isEmpty() || eqp_dscpn.getText().isEmpty() || eqp_brand.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please fill your details.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        }

        //reg ex
        if (!(eqp_dscpn.getText().matches("^\\b([A-Za-z.]+\\s?)+$") && eqp_brand.getText().matches("^\\b([A-Za-z.]+\\s?)+$"))) {
            new Alert(Alert.AlertType.ERROR, "Enter Valid Name").show();
            return;
        }
        if (btn_add.getText().equals("Add")) {
            addToTable.setString(1, eqp_id.getText());
            addToTable.setString(2, eqp_dscpn.getText());
            addToTable.setString(3, eqp_brand.getText());
            addToTable.setString(4, eqp_st.getText());

            int affectedRows = addToTable.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Data insertion successfull");
            } else {
                System.out.println("ERROR");
            }
        } else {
            if (btn_add.getText().equals("Update")) {
                for (int i = 0; i < books.size(); i++) {
                    if (eqp_id.getText().equals(books.get(i).getId())) {
                        updateQuarary.setString(1, eqp_dscpn.getText());
                        updateQuarary.setString(2, eqp_brand.getText());
                        updateQuarary.setString(3, eqp_st.getText());
                        updateQuarary.setString(4, eqp_id.getText());
                        int affected = updateQuarary.executeUpdate();

                        if (affected > 0) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                    "Record updated!",
                                    ButtonType.OK);
                            Optional<ButtonType> buttonType = alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "Update error!",
                                    ButtonType.OK);
                            Optional<ButtonType> buttonType = alert.showAndWait();
                        }
                    }
                }
                eqp_table.setItems(books);
            }
        }
        try {
            eqp_table.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //button delete action
    public void btn_dlt(ActionEvent actionEvent) throws SQLException {
        EquipmentModel selectedItem = eqp_table.getSelectionModel().getSelectedItem();
        if (eqp_table.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please select a member.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        } else {
            deleteQuarary.setString(1, selectedItem.getId());
            int affected = deleteQuarary.executeUpdate();

            if (affected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Record deleted!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
            }
        }
        try {
            eqp_table.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void img_back(MouseEvent event) throws IOException {

        URL resource = this.getClass().getResource("/View/AceHomeView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.eqp_root.getScene().getWindow();
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

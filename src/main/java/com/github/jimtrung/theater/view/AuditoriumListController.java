package com.github.jimtrung.theater.view;

import com.github.jimtrung.theater.model.Auditorium;
import com.github.jimtrung.theater.model.Movie;
import com.github.jimtrung.theater.service.AuditoriumService;
import com.github.jimtrung.theater.service.MovieService;
import com.github.jimtrung.theater.util.AuthTokenUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;
import java.util.UUID;

public class AuditoriumListController {

    private ScreenController screenController;
    private AuditoriumService auditoriumService;
    private ObservableList<Auditorium> auditoriumList;
    private UUID uuid;

    public void setAuditoriumService(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
        refreshData();
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    public TableView<Auditorium> getAuditoriumTable() {
        return auditoriumTable;
    }

    @FXML
    private TableView auditoriumTable;

    @FXML
    private TableColumn<Auditorium, String> nameColumn;

    @FXML
    private TableColumn<Auditorium, String> typeColumn;

    @FXML
    private TableColumn<Auditorium, Integer> capacityColumn;

    @FXML
    private TableColumn<Auditorium, String> noteColumn;

    @FXML
    public void handleOnOpen() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        auditoriumList = FXCollections.observableArrayList();
        auditoriumTable.setItems(auditoriumList);

        auditoriumTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Auditorium auditorium = (Auditorium) newSelection;
                uuid = auditorium.getId();
            }
            handleClickItem(uuid);
        });
        
        refreshData();
    }

    @FXML
    public void handleAddAuditoriumButton() {
        screenController.activate("addAuditorium");
    }

    @FXML
    public void handleClickItem(UUID id) {
        try {
            AuditoriumInformationController controller = (AuditoriumInformationController) screenController.getController("auditoriumInformation");
            controller.setUuid(id);
            controller.setAuditoriumListController(this);
            screenController.activate("auditoriumInformation");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteAllButton() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete all movie ?");

        Optional<ButtonType> result = alert.showAndWait();

        auditoriumService.deleteAllAuditoriums();
        refreshData();
    }

    public void refreshData() {
        if(auditoriumService != null && auditoriumList != null) {
            try {
                auditoriumList.setAll(auditoriumService.getAllAuditoriums());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateAuditorium(Auditorium updatedAuditorium) {
        for (int i = 0; i < auditoriumList.size(); i++) {
            Auditorium auditorium = auditoriumList.get(i);
            if (auditorium.getId().equals(updatedAuditorium.getId())) {
                auditoriumList.set(i, updatedAuditorium);
                break;
            }
        }
    }
    
    @FXML
    public void handleCloseButton() { 
        screenController.activate("homePageManager");
    }
}

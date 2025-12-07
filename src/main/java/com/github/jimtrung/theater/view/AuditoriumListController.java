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
    private AuthTokenUtil authTokenUtil;
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

    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    public TableView<Auditorium> getAuditoriumTable() {
        return movieTable;
    }

    @FXML
    private TableView movieTable;

    @FXML
    private TableColumn<Auditorium, String> nameColumn;

    @FXML
    private TableColumn<Auditorium, String> typeColumn;

    @FXML
    private TableColumn<Auditorium, Integer> capacityColumn;

    @FXML
    private TableColumn<Auditorium, String> noteColumn;

//    @FXML TableColumn<Movie, UUID> idColumn;

    @FXML
    private Button closeBtn;

    @FXML
    private Button addAuditoriumBtn;

    @FXML
    private Button deleteAllBtn;

    @FXML
    public void handleOnOpen() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        auditoriumList = FXCollections.observableArrayList();
        movieTable.setItems(auditoriumList);

        movieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Auditorium auditorium = (Auditorium) newSelection;
                System.out.println("Auditorium id was clicked: " + auditorium.getId());
                System.out.println("Auditorium name was clicked: " + auditorium.getName());
                uuid = auditorium.getId();
            }
            handleClickItem(uuid);
        });
    }

    @FXML
    public void handleAddAuditoriumButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_auditorium_dialog.fxml"));
            screenController.addScreen("addAuditorium", loader);

            AddAuditoriumController addAuditoriumController = loader.getController();
            addAuditoriumController.setScreenController(screenController);
            addAuditoriumController.setAuditoriumService(auditoriumService);
            addAuditoriumController.setAuthTokenUtil(authTokenUtil);
            addAuditoriumController.setAuditoriumListController(this);

            screenController.activate("addAuditorium");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleClickItem(UUID id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auditorium_information.fxml"));
            screenController.addScreen("auditoriumInformation", loader);

            AuditoriumInformationController auditoriumInformationController = loader.getController();
            auditoriumInformationController.setScreenController(screenController);
            auditoriumInformationController.setAuditoriumService(auditoriumService);
            auditoriumInformationController.setAuthTokenUtil(authTokenUtil);
            auditoriumInformationController.setAuditoriumListController(this);
            auditoriumInformationController.setUuid(uuid);

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

        if (result.isPresent() && result.get() == ButtonType.OK) {
            auditoriumService.deleteAllAuditoriums();
            refreshData();
        }
        else {
            System.out.println("Delete all operation cancelled !");
        }
    }

//    @FXML
//    public void handleOnOpen() {
//        refreshData();
//    }

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

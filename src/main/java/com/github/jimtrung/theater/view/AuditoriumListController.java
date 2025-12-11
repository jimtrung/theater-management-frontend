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

import java.util.List;
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

    @FXML private TextField searchField;
    
    // FilteredList
    private javafx.collections.transformation.FilteredList<Auditorium> filteredData;

    @FXML
    public void handleOnOpen() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        auditoriumList = FXCollections.observableArrayList();
        
        // Wrap
        filteredData = new javafx.collections.transformation.FilteredList<>(auditoriumList, p -> true);
        javafx.collections.transformation.SortedList<Auditorium> sortedData = new javafx.collections.transformation.SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(auditoriumTable.comparatorProperty());
        
        auditoriumTable.setItems(sortedData);

        auditoriumTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Auditorium auditorium = (Auditorium) newSelection;
                uuid = auditorium.getId();
            }
            handleClickItem(uuid);
        });
        
        // Listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        refreshData();
    }
    
    private void updateFilter() {
        filteredData.setPredicate(auditorium -> {
            String lowerCaseFilter = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
            if (lowerCaseFilter.isEmpty()) return true;
            
            boolean matchName = auditorium.getName() != null && auditorium.getName().toLowerCase().contains(lowerCaseFilter);
            boolean matchType = auditorium.getType() != null && auditorium.getType().toLowerCase().contains(lowerCaseFilter);
            boolean matchNote = auditorium.getNote() != null && auditorium.getNote().toLowerCase().contains(lowerCaseFilter);
            
            return matchName || matchType || matchNote;
        });
    }

    @FXML
    public void handleClearFilters() {
        searchField.setText("");
    }
    
    @FXML
    public void handleDeleteFilteredButton() {
        if (filteredData == null || filteredData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Không có phòng chiếu nào trong danh sách lọc để xóa.");
            alert.showAndWait();
            return;
        }

        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc chắn muốn xóa " + filteredData.size() + " phòng chiếu đang hiển thị không?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                List<Auditorium> toDelete = new java.util.ArrayList<>(filteredData);
                for (Auditorium a : toDelete) {
                    auditoriumService.deleteAuditoriumById(a.getId());
                }
                refreshData();
            }
        } catch (Exception e) {
            e.printStackTrace();
             Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Lỗi khi xóa: " + e.getMessage());
            error.showAndWait();
        }
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
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa tất cả phòng chiếu không?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            auditoriumService.deleteAllAuditoriums();
            refreshData();
        }
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

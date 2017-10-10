package us.raudi.printdf.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import us.raudi.printdf.BookletSettings;
import us.raudi.printdf.BookletSettings.DirSetting;
import us.raudi.printdf.PrintDF;
import us.raudi.printdf.StandardSize;

public class UiController {
	private FileChooser fileChooser;
	private DirectoryChooser dirChooser = new DirectoryChooser();;
	private Stage stage;
	
	public Button btn_browse;
	public CheckBox check_rotate;
	public Slider slide_quality;
	public ComboBox<StandardSize> combo_sizes;
	public TextField txt_ignore, txt_target;
	public ListView<File> list_files;
	public VBox vbox_container;
	public RadioButton radio_custom, radio_overwrite, radio_override;
	
	@FXML
    public void initialize() {
		fileChooser = new FileChooser();
		ExtensionFilter filter = new ExtensionFilter("PDF documents (*.pdf)", "*.pdf");
		fileChooser.getExtensionFilters().add(filter);
		
		setDragDrop();
		setPageSizes();
		setDefaultTarget();
		
		this.loadSettings();
    }
	
	private void setDefaultTarget() {
		String dir = System.getProperty("user.dir");
		txt_target.setText(dir);
	}

	private void setDragDrop() {
		vbox_container.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != vbox_container
                        && event.getDragboard().hasFiles()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });
		
		vbox_container.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    addFiles(db.getFiles());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                event.consume();
			}
		});
	}

	public void createBooklets(){
		List<File> items = list_files.getItems();
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setHeaderText("Please wait...");
		alert.setContentText("Creating booklets..." );
		alert.getButtonTypes().clear();
		
		BookletSettings settings = new BookletSettings()
				.quality((int) slide_quality.getValue())
				.rotateEvens(check_rotate.isSelected())
				.size(combo_sizes.getValue())
				.dirSetting(getDirSetting())
				.path(getTargetDirectory());
		
		Task<Void> task = new Task<Void>() {
		    @Override public Void call() {
		    	for(File f : items) PrintDF.createBooklet(f, settings);
		    	return null;
		    }
		};

		task.setOnRunning((e) -> alert.show());
		task.setOnSucceeded((e) -> {
			alert.getButtonTypes().add(ButtonType.CANCEL);
			alert.hide();
			alert.getButtonTypes().remove(ButtonType.CANCEL);	
		});
		task.setOnFailed((e) -> {});
		new Thread(task).start();
	}
	
	

	public void selectFiles() {
		List<File> list = fileChooser.showOpenMultipleDialog(stage);
		addFiles(list);
	}
	
	public void selectDirectory() {
		dirChooser.setInitialDirectory(new File(getTargetDirectory()));
		File dir = dirChooser.showDialog(stage);
		
		if(dir == null) return;
		
		txt_target.setText(dir.getPath());
	}
	
	private void addFiles(List<File> list) {
		List<File> items = list_files.getItems();
		
		if(list == null) return;
		
		for(File f : list)
			if(f.getName().contains(".pdf"))  
				if(!items.contains(f)) items.add(f);
	}
	
	public void removeFiles() {
		File f = list_files.getSelectionModel().getSelectedItem();
		list_files.getItems().remove(f);
	}
	
	public void clearList() {
		list_files.getItems().clear();
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	private void setPageSizes() {
		combo_sizes.getItems().addAll(StandardSize.values());
		combo_sizes.getSelectionModel().select(StandardSize.A4);
	}
	
	public String getTargetDirectory() {
		return txt_target.getText();
	}
	
	
	private void setDirSetting(DirSetting ds) {
		if(ds == DirSetting.CUSTOM)			radio_custom.setSelected(true);
		else if(ds == DirSetting.OVERWRITE)	radio_overwrite.setSelected(true);
		else								radio_override.setSelected(true);
	}
	
	
	
	public DirSetting getDirSetting() {
		if(radio_custom.isSelected()) 
			return BookletSettings.DirSetting.CUSTOM;
		if(radio_override.isSelected())
			return BookletSettings.DirSetting.OVERRIDE;
		
		return BookletSettings.DirSetting.OVERWRITE;
	}
	
	public void saveSettings() throws IOException {
		UiPreferences.fromController(this).save();
	}
	
	public void loadSettings() {
		UiPreferences prefs = UiPreferences.loadSettings();
		
		if(prefs == null) return;
		
		combo_sizes.getSelectionModel().select(prefs.size);
		check_rotate.setSelected(prefs.rotate);
		setDirSetting(prefs.dirSetting);
		txt_target.setText(prefs.targetPath);
		slide_quality.setValue(prefs.quality);
	}	
}
package us.raudi.printdf.ui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import us.raudi.printdf.BookletSettings.DirSetting;
import us.raudi.printdf.StandardSize;

public class UiPreferences implements Serializable{
	private static final long serialVersionUID = -9132794209389422645L;
	
	public boolean rotate;
	public StandardSize size;
	public DirSetting dirSetting;
	public String targetPath;
	public double quality;
	
	public static UiPreferences fromController(UiController c) {
		UiPreferences prefs = new UiPreferences();
		
		prefs.rotate = c.check_rotate.isSelected();
		prefs.size = c.combo_sizes.getValue();
		prefs.quality = c.slide_quality.getValue();
		prefs.dirSetting = c.getDirSetting();
		prefs.targetPath = c.getTargetDirectory();
		
		return prefs;
	}
	
	
	public void save() throws IOException {
		FileOutputStream fout = new FileOutputStream("printdf.settings");
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(this);
		oos.close();
	}


	public static UiPreferences loadSettings() {
		try {
			FileInputStream fis = new FileInputStream("printdf.settings");
			ObjectInputStream ois = new ObjectInputStream(fis);
		    UiPreferences p = (UiPreferences) ois.readObject();
		    ois.close();
		    return p;
		} catch (IOException e) {
		    System.err.println("Could not find settings file: 'printdf.settings'");
		    return null;
		} catch (ClassNotFoundException e) {
		    System.err.println("'printdf.settings' is corrupted.");
			return null;
		}
	}
}

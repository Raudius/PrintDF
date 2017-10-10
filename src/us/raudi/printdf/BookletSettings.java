package us.raudi.printdf;

import java.io.File;

public class BookletSettings {
	public enum DirSetting{
		CUSTOM, OVERRIDE, OVERWRITE;
	}
	
	private DirSetting dirSetting;
	private String path;
	private boolean rotateEvens;
	private int scale;
	private StandardSize size;
	
	
	public BookletSettings() {
		rotateEvens = false;
		scale = 2;
		size = StandardSize.A4;
		dirSetting = DirSetting.OVERRIDE;
	}
	
	public BookletSettings rotateEvens(boolean bool) {
		this.rotateEvens = bool;
		return this;
	}
	
	public BookletSettings quality(int scale) {
		this.scale = scale;
		return this;
	}
	
	public BookletSettings size(StandardSize size) {
		this.size = size;
		return this;
	}

	public BookletSettings dirSetting(DirSetting dirSetting) {
		this.dirSetting = dirSetting;
		return this;
	}
	
	public BookletSettings path(String directory) {
		this.path = directory;
		return this;
	}
	
	
	// getters
	public StandardSize getSize() {
		return size;
	}
	public int getScale() {
		return scale;
	}
	public boolean isRotateEvens() {
		return rotateEvens;
	}
	public String getPath() {
		return path;
	}
	public DirSetting getDirSetting() {
		return dirSetting;
	}
	public String getDirectory(File f) {
		if(dirSetting == DirSetting.CUSTOM)
			return path + "/" + f.getName().replaceAll(".pdf", "_booklet.pdf");
		
		if(dirSetting == DirSetting.OVERWRITE) return f.getPath();
		
		return f.getPath().replaceAll(".pdf", "_booklet.pdf");
	}
}

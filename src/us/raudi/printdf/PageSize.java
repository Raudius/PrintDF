package us.raudi.printdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.printing.Orientation;

public class PageSize {
	
	
	private float width, height;
	
	public PageSize(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	
	public void setWidth(float width) {
		this.width = width;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	
	@Override
	public String toString() {
		return String.format("%f x %f", width, height);
	}


	public PDRectangle asPDRectangle() {
		return new PDRectangle(width, height);
	}


	public void setOrientation(Orientation orientation) {
		if(orientation == Orientation.LANDSCAPE) {
			if(width < height) rotate();
		}
		if(orientation == Orientation.PORTRAIT) {
			if(height < width) rotate();
		}
	}


	private void rotate() {
		float temp = height;
		height = width;
		width = temp;
	}
}

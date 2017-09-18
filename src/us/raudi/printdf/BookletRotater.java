package us.raudi.printdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class BookletRotater {
	public static void rotate(PDDocument doc, PageSelector pages, int degrees) {
		for(PDPage p : pages)
			p.setRotation(degrees);		
	}
	
	public static void flip(PDDocument doc, PageSelector pages) {
		rotate(doc, pages, 180);
	}
}

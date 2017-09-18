package us.raudi.printdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;


public class PrintDF {

	public static void main(String[] args) {
		
		String file = args[0];
		
		try {
			PDDocument document = PDDocument.load(new File(file));
			
			if (document.isEncrypted()) {
				System.err.println("Error: Encrypted documents are not supported!");
				System.exit(1);
			}
			
			BookletMaker.setScale(2);
			PDDocument booklet = BookletMaker.make(document);
			BookletRotater.flip(booklet, PageSelector.pagesEven(booklet));
			
			booklet.save("Output.pdf");
			System.out.println("Done!");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		

	}
	
	
	
	public static PDImageXObject pageToImage(PDDocument doc, int page, int scale) throws IOException {
		// return empty pages if page number excedes number of pages
		if(page >= doc.getNumberOfPages()) 
			return JPEGFactory.createFromImage(doc, new BufferedImage(1,1,1));
		
		PDFRenderer pdfRenderer = new PDFRenderer(doc);
		BufferedImage bim = pdfRenderer.renderImage(page, scale);
		PDImageXObject img = JPEGFactory.createFromImage(doc, bim);
		
		return img;
	}

}
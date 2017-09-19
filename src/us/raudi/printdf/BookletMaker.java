package us.raudi.printdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class BookletMaker {
	private static int scale = 1;
	private static PageSize sizeOverride = null;
	
	/**
	 * Given a pdf document, creates a printable booklet.
	 * @param doc original document to be converted
	 * @return booklet form of the original document.
	 */
	public static PDDocument make(PDDocument doc) {
		PDDocument booklet = new PDDocument();
		
		// "effective pages" defines the effective number of pages in the booklet (including blanks)
		int effectivePages = doc.getNumberOfPages() + doc.getNumberOfPages() % 4;
		// "pages" defines the actual number of sides of paper the booklet will use
		int pages = effectivePages / 2;
		
		
		boolean left = false; // smaller page number goes on the left?
		
		for(int i=0; i<pages; i++) {
			int page1 = i;
			int page2 = effectivePages - i - 1;
			
			PDPage page;
			if(left) page = createBookPage(doc, page1, page2);
			else page = createBookPage(doc, page2, page1);
			
			booklet.addPage(page);
			
			left = !left;
		}
		
		return booklet;
	}
	
	/**
	 * Creates a pdf page from two pages from another 'original' pdf document
	 * @param doc original pdf from which the pages will be taken
	 * @param leftPage page number of the page to go on the left side 
	 * @param rightPage page number of the page to go on the right side
	 * @return generated page containing the left and right pages from the original document side-by-side.
	 */
	private static PDPage createBookPage(PDDocument doc, int leftPage, int rightPage) {
		// double the width of a normal page to create the booklet
		PDRectangle baseSize = doc.getPage(0).getMediaBox();		
		PDRectangle box = new PDRectangle(baseSize.getWidth()*2, baseSize.getHeight());
		

		if(sizeOverride != null) {
			box = sizeOverride.asPDRectangle();
		}
		
		PDPage page = new PDPage(box);
		
		try {
			PDImageXObject leftImg = PrintDF.pageToImage(doc, leftPage, scale);
			PDImageXObject rightImg = PrintDF.pageToImage(doc, rightPage, scale);
			
			
			
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			if(leftImg != null)
				contentStream.drawImage(leftImg, 0, 0, box.getWidth()/2, box.getHeight());
			if(rightImg != null)
				contentStream.drawImage(rightImg, box.getWidth()/2, 0, box.getWidth()/2, box.getHeight());
			contentStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return page;
	}
	
	
	public static void setScale(int scale) {
		BookletMaker.scale = scale;
	}
	
	public static void setSizeOverride(PageSize ps) {
		BookletMaker.sizeOverride = ps;
	}
}

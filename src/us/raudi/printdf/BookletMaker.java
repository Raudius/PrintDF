package us.raudi.printdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class BookletMaker {
	private static int scale = 1;
	
	public static PDDocument make(PDDocument doc) {
		PDDocument booklet = new PDDocument();
		
		int pages = (int) Math.ceil(doc.getNumberOfPages() / 2f);
		
		boolean left = false; // smaller page number goes on the left?
		
		for(int i=0; i<pages; i++) {
			int page1 = i;
			int page2 = doc.getNumberOfPages() - i;
			
			PDPage page = null;
			if(left) page = createBookPage(doc, page1, page2);
			else page = createBookPage(doc, page2, page1);
			
			booklet.addPage(page);
			
			left = !left;
		}
		
		return booklet;
	}
	
	private static PDPage createBookPage(PDDocument doc, int leftPage, int rightPage) {
		PDRectangle baseSize = doc.getPage(0).getMediaBox();
		PDRectangle box = new PDRectangle(baseSize.getWidth()*2, baseSize.getHeight());
		
		PDPage page = new PDPage(box);
		
		try {
			PDImageXObject leftImg = PrintDF.pageToImage(doc, leftPage, scale);
			PDImageXObject rightImg = PrintDF.pageToImage(doc, rightPage, scale);
			
			
			
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			contentStream.drawImage(leftImg, 0, 0, leftImg.getWidth()/scale, leftImg.getHeight()/scale);
			contentStream.drawImage(rightImg, baseSize.getWidth(), 0, rightImg.getWidth()/scale, rightImg.getHeight()/scale);
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
	
}

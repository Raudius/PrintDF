package us.raudi.printdf;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class BookletMaker {
	private static int scale = 1;
	private static PageSize sizeOverride = null;
	
	// This class represents a "sub page" in the output document
	// n: the page number
	// right: whether it is the right side of the page
	private static class Page {
		public int n;
		public boolean right;
		
		private boolean up = true;
		
		public Page(int n, boolean right) {
			this.n = n;
			this.right = right;
		}

		public void next(int max) {
			right = !right;
			
			if(n == max-1 && up) {
				up = false;
				return;
			}
			
			if(up) n++;
			else n--;
		}
		
		@Override
		public String toString() {
			return n+1 + (right ? "R" : "L");
		}
	}
	
	// this class describes the contents of an output pdf page
	// the right and left values correspond to a page of the input pdf
	private static class Contents {
		public int right= Integer.MAX_VALUE, left=Integer.MAX_VALUE;
		
		public void setContent(int n, boolean r) {
			if(r) this.right = n;
			else this.left = n;
		}
	}
	
	/**
	 * Given a pdf document, creates a printable booklet.
	 * @param doc original document to be converted
	 * @return booklet form of the original document.
	 */
	public static PDDocument make(PDDocument doc) {
		PDDocument booklet = new PDDocument();
		
		// out_pages defines the number of pages of the output pdf.
		int out_pages = (int) Math.ceil(doc.getNumberOfPages() / 2.0);
		if(out_pages % 2 != 0)
			out_pages++;
		
		// the page defines a position of the booklet pages in output pdf (the first page will always be on the 1st page on the right side)
		Page page = new Page(0, true);
		
		// for only two pages in input we want both pages to appear side by side.
		if(doc.getNumberOfPages() == 2) {
			page.n = 1;
			page.right = false;
		}
		
		Contents[] contents = new Contents[out_pages];
		for(int i=0; i<out_pages; i++)
			contents[i] = new Contents();

		// construct output page contents
		for(int i=0; i<doc.getNumberOfPages(); i++) {
			contents[page.n].setContent(i, page.right);
			
			System.out.println("Page "+i+" in " + page);
			
			page.next(out_pages);
		}
		
		// build pdf fromt content
		for(Contents c : contents) {
			booklet.addPage( createBookPage(doc, c.left, c.right) );
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

package us.raudi.printdf;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PageSelector implements Iterable<PDPage> {
	private ArrayList<PDPage> pages = new ArrayList<>();
	
	public static PageSelector pagesInterval(PDDocument doc, int from, int to) {
		PageSelector br = new PageSelector();
		
		// bound parameters
		from = Math.min(from, 0);
		to = Math.min(to,  doc.getNumberOfPages()-1);
		
		for(int i=from; i<=to; i++)
			br.addPage(doc, i);
		
		return br;
	}
	
	public static PageSelector pagesOdd(PDDocument doc) {
		return pagesSequence(doc, 0, 2);
	}
	public static PageSelector pagesEven(PDDocument doc) {
		return pagesSequence(doc, 1, 2);
	}
	
	public static PageSelector pagesSequence(PDDocument doc, int start, int period) {
		PageSelector br = new PageSelector();
		
		for(int i=start; i<doc.getNumberOfPages(); i+=period)
			br.addPage(doc, i);
		
		return br;
	}
	
	
	@Override
	public Iterator<PDPage> iterator() {
		return pages.iterator();
	}
	
	public void addPage(PDPage p) {
		pages.add(p);
	}
	
	public void addPage(PDDocument doc, int page) {
		addPage(doc.getPage(page));
	}
}
package us.raudi.printdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.Orientation;
import org.apache.pdfbox.rendering.PDFRenderer;


public class PrintDF {
	
	public static void main(String[] args) {
		String dir = ".";
		
		if(args.length > 0)
			dir = args[0];
		
		ArrayList<File> files = getAllPdfs(dir);
		
		for(File f : files) {
			createBooklet(f, new BookletSettings());
		}
		
		System.out.println("Done!");
	}
	
	public static void createBooklet(File f, BookletSettings setts) {
		try {
			PDDocument document = PDDocument.load(f);
			
			if (document.isEncrypted()) {
				System.err.println("Error: Encrypted documents are not supported!");
				System.exit(1);
			}
			
			PageSize size = setts.getSize().getPageSize();
			size.setOrientation(Orientation.LANDSCAPE);
			
			BookletMaker.setSizeOverride(size);			
			BookletMaker.setScale(setts.getScale());
			
			PDDocument booklet = BookletMaker.make(document);
			
			if(setts.isRotateEvens())
				BookletRotater.flip(booklet, PageSelector.pagesEven(booklet));
			
			String name = setts.getDirectory(f);
			System.out.println(name);

			booklet.save(name);
			document.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static ArrayList<File> getAllPdfs(String path){
		ArrayList<File> files = new ArrayList<>();
		
		File dir = new File(path);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
		    if (file.isFile()) {
		        String name = file.getName();
		        if(name.contains(".pdf") && !name.contains("_booklet")) {
		        	files.add(file);
		        }
		    }
		}
		return files;
	}
	
	
	public static PDImageXObject pageToImage(PDDocument doc, int page, int scale) throws IOException {
		// return empty pages if page number excedes number of pages
		if(page >= doc.getNumberOfPages()) 
			return null;
		
		PDFRenderer pdfRenderer = new PDFRenderer(doc);
		BufferedImage bim = pdfRenderer.renderImage(page, scale);
		PDImageXObject img = JPEGFactory.createFromImage(doc, bim);
		
		return img;
	}


}
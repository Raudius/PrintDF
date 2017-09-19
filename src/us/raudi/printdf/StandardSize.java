package us.raudi.printdf;

public enum StandardSize{
	A0(0), A1(1), A2(2), A3(3), A4(4), A5(5),
	A6(6), A7(7), A8(8), A9(9), A10(10);
	
	private PageSize size;
	
	private StandardSize(int s) {
		size = sizeAx(s);
	}
	
	public PageSize sizeAx(int x) {
		// base size of an A0 paper in points (1pt = 1/72 inches)
		float w=2384, h= 3370;
		
		for(int i=0; i<x; i++) {
			float temp = h / 2f;
			h = w;
			w = temp;
		}			

		PageSize ps = new PageSize(w, h);
		
		return ps;
	}
	
	public PageSize getPageSize() {
		return size;
	}
}
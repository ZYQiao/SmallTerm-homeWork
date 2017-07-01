package a2;

public class Line {
	private Point p1;
	private Point p2;

	public Line() {
		super();
	}

	public Line(Point p1, Point p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

	public Point getP1() {
		return p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}
	
	public String getLine(Point p1, Point p2) {
		if (p1 == p2)
			return null;
		String f = new String();
		double a, b, c;
		a = p2.getY() - p1.getY();
		b = p1.getX() - p2.getX();
		c = p2.getX() * p1.getY() - p1.getX() * p2.getY();
		f = "(" + String.valueOf(a) + ")x + (" + String.valueOf(b) + ")y + (" + String.valueOf(c) + ") = 0";
		return f;
	}
	public static void main(String[] args) {
		Point p1 = new Point(0, 1);
		Point p2 = new Point(0,0);
		Line l = new Line();
		l.getLine(p1, p2);
		System.out.println(l.getLine(p1, p2));
	}
}

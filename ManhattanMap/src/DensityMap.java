import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class DensityMap extends Canvas implements MouseMotionListener {
	private CoordinateParser cp;
	int xMin = 100;
	int xMax = 0;
	int yMin = 100;
	int yMax = 0;

	int mouseX = 0;
	int mouseY = 0;

	public DensityMap(int width, int height) {
		setSize(width, height);
		setBackground(Color.BLACK);
		cp = new CoordinateParser(width, height, this);
		cp.print();
		System.out.println(getWidth() + " " + getHeight() + " " + width + " " + height);
		addMouseMotionListener(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// for (int i = 0; i < p.size(); i++) {
		// if (p.get(i).x < 0 || p.get(i).y < 0) {
		// g.drawLine(0, 0, getWidth(), getHeight());
		// }
		// if (p.get(i).x < xMin) {
		// xMin = p.get(i).x;
		// } else if (p.get(i).x > xMax){
		// xMax = p.get(i).x;
		// }
		//
		// if (p.get(i).y < yMin) {
		// yMin = p.get(i).y;
		// } else if (p.get(i).y > yMax){
		// yMax = p.get(i).y;
		// }

		// g.drawRect((int)(p.get(i).x), (int)(p.get(i).y), 0, 0);
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(cp.getImage(), 5, 5, getWidth() - 10, getHeight() - 10, null);

		 g.setColor(Color.WHITE);
		 g.drawString("--Crash Density Scale--", 8, 22);
		 
		 g.drawString(cp.getResponseTime() + " minutes", getWidth() - 175, getHeight() - 20);
		// }
		// g.drawString(mouseX + ", " + mouseY, 20,20);
		// System.out.println(xMin + " " + xMax + " " + yMin + " " + yMax);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseX = e.getX();
		mouseY = e.getY();
	}

}

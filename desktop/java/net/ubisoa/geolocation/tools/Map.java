/*
 * Copyright (c) 2010, Edgardo Avilés-López <edgardo@ubisoa.net>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * – Redistributions of source code must retain the above copyright notice, this list of
 *   conditions and the following disclaimer.
 * – Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * – Neither the name of the CICESE Research Center nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ubisoa.geolocation.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.ubisoa.geolocation.data.Location;

public class Map extends JPanel {
	private static final Font INSIDE_POINT_FONT = new Font("Helvetica", Font.BOLD, 16);
	private static final Font STATUS_FONT = new Font("Helvetica", Font.BOLD, 18);
	private static final long serialVersionUID = 5410197355947740016L;
	private static final int SCROLLBAR_HEIGHT = 15;
	private static final int STATUS_MARGIN_BOTTOM = -25;
	private static final Color POINT_COLOR = new Color(0xe6bc3e);
	private static final int POINT_RADIUS = 25;

	private BufferedImage bufImg = null;
	private Graphics2D g2d = null;
	private Image image = null;
	private Location[] borderCoords;
	private String noImageText = "to start using this client, please load a map";
	private String statusText = "";
	private File filename;
	private TreeMap<String, Point> points = new TreeMap<String, Point>();
	private TreeMap<String, String> pointLabels = new TreeMap<String, String>();
	private boolean mustRepaint;
	
	public Map() {
		super();
		this.setBackground(new Color(0x9a9da0));
		this.setAutoscrolls(true);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	public void paint(Graphics g) {
		Graphics2D graphics2D = (Graphics2D)g;
		
		if (bufImg == null || bufImg.getWidth() != getWidth() || bufImg.getHeight() != getHeight()) {
			bufImg = graphics2D.getDeviceConfiguration().createCompatibleImage(
					getWidth(), getHeight(), Transparency.TRANSLUCENT);
			g2d = bufImg.createGraphics();
			g2d.setComposite(AlphaComposite.Src);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			drawInBuffer();
		} else if (statusText != null) {
			drawInBuffer();
		} else if (mustRepaint) {
			drawInBuffer();
			mustRepaint = false;
		}
		
		graphics2D.drawImage(bufImg, 0, 0, null);
	}
	
	public void update(Graphics g) {
		paint(g);
	}

	private void drawInBuffer() {
		drawBackground();
		if (image != null) {
			drawImage();
			drawPoints();
			drawText(statusText);
		} else drawText(noImageText);
	}

	private void drawBackground() {
		g2d.setBackground(new Color(0x9a9da0));
		g2d.clearRect(0, 0, bufImg.getWidth(), bufImg.getHeight());
	}
		
	private void drawPoints() {
		g2d.setFont(STATUS_FONT);
		
		for (String key: points.keySet()) {
			
			// The point to draw.
			Point point = points.get(key);
			
			// The circle background.
			double x = point.getX() - POINT_RADIUS / 2.0;
			double y = point.getY() - POINT_RADIUS / 2.0;
			g2d.setColor(POINT_COLOR);
			g2d.fillOval((int)Math.round(x), (int)Math.round(y), POINT_RADIUS, POINT_RADIUS);
			
			// The text inside.
			g2d.setColor(Color.WHITE);
			FontMetrics fontMetrics = g2d.getFontMetrics();
			x = point.getX() - fontMetrics.stringWidth(key) / 2.0;
			y = point.getY() + fontMetrics.getAscent() / 2.0;
			g2d.drawString(key, Math.round(x), Math.round(y));
			
		}
	}
	
	private void drawText(String string) {
		if (string == null) return;
		g2d.setFont(INSIDE_POINT_FONT);
		g2d.setColor(Color.WHITE);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int width = fontMetrics.stringWidth(string);
		int fromBottom = (string.compareTo(noImageText) == 0)?
				STATUS_MARGIN_BOTTOM: STATUS_MARGIN_BOTTOM - SCROLLBAR_HEIGHT;
		
		g2d.drawString(string,
				Math.round((getParent().getParent().getWidth() - width) / 2.0f) + getX() * -1,
				getParent().getParent().getHeight() + fromBottom + getY() * -1);
	}
	
	private void drawImage() {
		g2d.drawImage(image, 0, 0, null);
	}

	public boolean setImage(File filename) {
		if (filename == null) return false;
		Image newImage = new ImageIcon(filename.getAbsolutePath()).getImage();
		if (newImage.getWidth(null) == -1) {
			JOptionPane.showMessageDialog(null, "Selected image cannot be loaded.", "Loading error",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		image = newImage;
		this.filename = filename;
		
		Dimension dimension = new Dimension(image.getWidth(null), image.getHeight(null));
		setSize(dimension); setPreferredSize(dimension);
		
		drawInBuffer(); statusText = ""; repaint();
		return true;
	}
	
	public File getFilename() {
		return filename;
	}
	
	public void setStatus(String string) {
		statusText = string;
		mustRepaint = true;
		repaint();
	}
	
	public void addPoint(String key, Point point, String label) {
		points.put(key, point);
		pointLabels.put(key, label);
		mustRepaint = true;
	}
	
	public Location[] getBorderCoords() {
		return borderCoords;
	}
	
	public void setBorderCoords(Location[] borderCoords) {
		this.borderCoords = borderCoords;
	}

	/**
	 * Formule is:
	 * 		lat = x1 + x*(x2-x1) + y*(x3-x1) + x*y*(x1-x2-x3+x4);
	 *		lon = y1 + x*(y2-y1) + y*(y3-y1) + x*y*(y1-y2-y3+y4);
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Location normToLatLong(Location location) {
		/*		
		x1 = 31.871229; y1 = -116.667744;	// NW	3	
		x2 = 31.871434; y2 = -116.666870;	// NE	0
		x3 = 31.870950; y3 = -116.667654;	// SW	2
		x4 = 31.871155; y4 = -116.666779;	// SE	1
		
		lat = x1 + x*(x2-x1) + y*(x3-x1) + x*y*(x1-x2-x3+x4);
		lon = y1 + x*(y2-y1) + y*(y3-y1) + x*y*(y1-y2-y3+y4);
		 */
		return new Location(
				borderCoords[3].getLatitude() + location.getLatitude() * (borderCoords[0].getLatitude() -
						borderCoords[3].getLatitude()) + location.getLongitude() *
						(borderCoords[2].getLatitude() - borderCoords[3].getLatitude()) +
						location.getLatitude() * location.getLongitude() *
						(borderCoords[3].getLatitude() - borderCoords[0].getLatitude() -
								borderCoords[2].getLatitude() + borderCoords[1].getLatitude()),
				borderCoords[3].getLongitude() + location.getLatitude() *
						(borderCoords[0].getLongitude() - borderCoords[3].getLongitude()) +
						location.getLongitude() * (borderCoords[2].getLongitude() -
						borderCoords[3].getLongitude()) + location.getLatitude() *
						location.getLongitude() * (borderCoords[3].getLongitude() -
						borderCoords[0].getLongitude() - borderCoords[2].getLongitude() +
						borderCoords[1].getLongitude())
		);
	}
	
	public Location latLongToNorm(Location location) {
		return binarySearch(0.0, 0.0, 1.0, 1.0, location, 0, true);
	}
	
	private Location binarySearch(double x1, double y1, double x2, double y2, Location latLong,
			int i, boolean horiz) {
		double xmed, ymed, diff, diffLeft, diffRight, diffTop, diffBottom;
		double error = 1E-64;
		
		try {
			if (i > 1E10) return new Location(x1 + (x2 - x1) / 2.0, y1 + (y2 - y1) / 2.0);
		
			xmed = x1 + (x2 - x1) / 2.0;
			ymed = y1 + (y2 - y1) / 2.0;
		
			Location val = normToLatLong(new Location(xmed, ymed));			
			diff = Location.distance(latLong, val);
					
			if (diff < error) return new Location(xmed, ymed);
			
			if (horiz) {
				diffLeft = Location.distance(latLong, normToLatLong(new Location(xmed * 0.9, ymed)));
				diffRight = Location.distance(latLong, normToLatLong(new Location(xmed * 1.1, ymed)));			
				if (diffRight < diffLeft)
					return binarySearch(xmed, y1, x2, y2, latLong, i + 1, false);
				else return binarySearch(x1, y1, xmed, y2, latLong, i + 1, false);
			} else {
				diffTop = Location.distance(latLong, normToLatLong(new Location(xmed, ymed * 0.9)));
				diffBottom = Location.distance(latLong, normToLatLong(new Location(xmed, ymed * 1.1)));
				if (diffBottom < diffTop)
					return binarySearch(x1, ymed, x2, y2, latLong, i + 1, true);
				else return binarySearch(x1, y1, x2, ymed, latLong, i + 1, true);
			}
		} catch (StackOverflowError e) {
			return new Location(x1 + (x2 - x1) / 2.0, y1 + (y2 - y1) / 2.0);
		}
	}

}

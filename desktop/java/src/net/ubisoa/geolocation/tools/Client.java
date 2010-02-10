package net.ubisoa.geolocation.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;

import net.ubisoa.geolocation.data.Location;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

@SuppressWarnings("restriction")
public class Client extends JFrame implements ActionListener, AdjustmentListener, MouseListener,
	MouseMotionListener, BrowseListener, ResolveListener {
	private static final long serialVersionUID = 1343780925922938227L;
	private static final int MODE_NO_SERVICE = 0;
	private static final int MODE_STUMBLE = 1;
	private static final int MODE_TRACK = 2;
	private String filename = "dat/geolocation.properties";
	private Map map;
	private JScrollPane scrollPane;
	private int mode = MODE_NO_SERVICE;
	private Point lastMouseDragPoint;
	private boolean beingDragged, busy;
	private WiFiSpotter spotter = new WiFiSpotter(true);
	private HashMap<String, String> serviceHostNames = new HashMap<String, String>();
	private HashMap<String, Integer> servicePorts = new HashMap<String, Integer>();
	private JMenu servicesMenu;
	private String selectedService;
	
	public Client() {
		super("UbiSOA Geolocation Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationByPlatform(true);
		this.setJMenuBar(createMenuBar());
		this.setLayout(new BorderLayout());
		this.add(createMapScroller(), BorderLayout.CENTER);
		this.setSize(700, 500);
		this.setVisible(true);
		loadPreferences();
		searchForServices();
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu; JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		
		menu = new JMenu("Map");
		
		menuItem = new JMenuItem("Load map…", KeyEvent.VK_L);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Set border coordinates…", KeyEvent.VK_B);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuBar.add(menu);
		
		menu = new JMenu("Mode");
		ButtonGroup group = new ButtonGroup();
		
		rbMenuItem = new JRadioButtonMenuItem("Stumbler");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_S);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("Tracker");
		rbMenuItem.setEnabled(false);
		rbMenuItem.setMnemonic(KeyEvent.VK_T);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);
		
		menuBar.add(menu);
		
		servicesMenu = new JMenu("Service");
		menuItem = new JMenuItem("No geolocation services detected.");
		menuItem.setEnabled(false);
		servicesMenu.add(menuItem);
		menuBar.add(servicesMenu);
		
		return menuBar;
	}
	
	private JScrollPane createMapScroller() {
		map = new Map();
		map.addMouseListener(this);
		map.addMouseMotionListener(this);
		scrollPane = new JScrollPane(map);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		scrollPane.setWheelScrollingEnabled(false);
		return scrollPane;
	}
	
	private void loadPreferences() {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(filename));
			
			String image = properties.getProperty("client.map.filename");
			if (image != null) map.setImage(new File(image));
			
			String coordsNW = properties.getProperty("client.map.coords.ne.lat");
			if (coordsNW != null) {
				Location[] locations = new Location[4];
				locations[0] = new Location(
						Double.parseDouble(properties.getProperty("client.map.coords.ne.lat")),
						Double.parseDouble(properties.getProperty("client.map.coords.ne.lon")));
				locations[1] = new Location(
						Double.parseDouble(properties.getProperty("client.map.coords.se.lat")),
						Double.parseDouble(properties.getProperty("client.map.coords.se.lon")));
				locations[2] = new Location(
						Double.parseDouble(properties.getProperty("client.map.coords.sw.lat")),
						Double.parseDouble(properties.getProperty("client.map.coords.sw.lon")));
				locations[3] = new Location(
						Double.parseDouble(properties.getProperty("client.map.coords.nw.lat")),
						Double.parseDouble(properties.getProperty("client.map.coords.nw.lon")));
				map.setBorderCoords(locations);
			} else {
				CoordsDialog coordsDialog = new CoordsDialog(this);
				coordsDialog.setVisible(true);
				map.setBorderCoords(coordsDialog.getBorderCoords());
				storePreferences();
			}
			
			waitingService();
		} catch (IOException e) {}
	}
	
	private void storePreferences() {
		try {
			Properties properties = new Properties();
			properties.setProperty("client.map.filename", map.getFilename().getAbsolutePath());
			
			Location[] locations = map.getBorderCoords();
			if (locations != null) {
				properties.setProperty("client.map.coords.ne.lat", locations[0].getLatitude() + "");
				properties.setProperty("client.map.coords.ne.lon", locations[0].getLongitude() + "");
				properties.setProperty("client.map.coords.se.lat", locations[1].getLatitude() + "");
				properties.setProperty("client.map.coords.se.lon", locations[1].getLongitude() + "");
				properties.setProperty("client.map.coords.sw.lat", locations[2].getLatitude() + "");
				properties.setProperty("client.map.coords.sw.lon", locations[2].getLongitude() + "");
				properties.setProperty("client.map.coords.nw.lat", locations[3].getLatitude() + "");
				properties.setProperty("client.map.coords.nw.lon", locations[3].getLongitude() + "");
			}
			
			properties.store(new FileOutputStream(filename), null);
		} catch (IOException e) {}
	}
	
	private void stumbling() {
		mode = MODE_STUMBLE;
		map.setStatus("double click on the map to stumble a placemark");
	}
	
	private void tracking() {
		mode = MODE_TRACK;
		map.setStatus("tracking your position…");
	}
	
	private void waitingService() {
		mode = MODE_NO_SERVICE;
		map.setStatus("no geolocation services detected");
	}
	
	private void searchForServices() {
		try {
			DNSSD.browse("_ubisoa._tcp", this);
		} catch (DNSSDException e) {
			e.printStackTrace();
		}
	}
	
	private void updateServicesList() {
		servicesMenu.removeAll();
		
		if (serviceHostNames.size() == 0) {
			waitingService();
			JMenuItem menuItem = new JMenuItem("No geolocation services detected.");
			menuItem.setEnabled(false);
			servicesMenu.add(menuItem);
			return;
		}
		
		ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButtonMenuItem rbMenuItem; boolean first = true;
		for (String key: serviceHostNames.keySet()) {
			rbMenuItem = new JRadioButtonMenuItem(key);
			if (first) {
				rbMenuItem.setSelected(true);
				selectedService = key;
				stumbling();
				first = !first;
			}
			rbMenuItem.addActionListener(this);
			rbMenuItem.setActionCommand("Service Selected");
			buttonGroup.add(rbMenuItem);
			servicesMenu.add(rbMenuItem);
		}
	}
	
	private Location xyToNorm(Point point) {
		return new Location(point.x / (map.getSize().getWidth() - 1.0),
				point.y / (map.getSize().getHeight() - 1.0));
	}
	
	@SuppressWarnings("unused")
	private Point normToXY(Location location) {
		return new Point((int)Math.round(location.getLatitude() * (map.getSize().getWidth() - 1)),
				(int)Math.round(location.getLongitude() * (map.getSize().getHeight() - 1)));
	}
	
	public static void main(String[] args) {
		new Client();
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.compareTo("Load map…") == 0) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.showOpenDialog(this);
			if (map.setImage(fileChooser.getSelectedFile())) {
				storePreferences();
				if (mode == MODE_STUMBLE) stumbling();
				if (mode == MODE_TRACK) tracking();				
				CoordsDialog coordsDialog = new CoordsDialog(this, false);
				coordsDialog.setBorderCoords(map.getBorderCoords());
				coordsDialog.setVisible(true);
				map.setBorderCoords(coordsDialog.getBorderCoords());
				storePreferences();
			}
		} else if (command.compareTo("Set border coordinates…") == 0) {
			CoordsDialog coordsDialog = new CoordsDialog(this, false);
			coordsDialog.setBorderCoords(map.getBorderCoords());
			coordsDialog.setVisible(true);
			map.setBorderCoords(coordsDialog.getBorderCoords());
			storePreferences();
		} else System.out.println("Unimplemented event \"" + command + "\".");
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		map.repaint();
	}
	
	public void mouseDragged(MouseEvent e) {
		if (!beingDragged) {
			beingDragged = true;
			lastMouseDragPoint = e.getPoint();
		}
 
		handleMouseDragInBrowserPanel(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		beingDragged = false;
	}
	
	/***************************************************************************************************
	 * Code for this was extracted from:
	 *   http://forums.sun.com/thread.jspa?threadID=707817&messageID=4099799
	 * @param e
	 ***************************************************************************************************/
	private void handleMouseDragInBrowserPanel (MouseEvent e) {		
		JViewport viewPort = scrollPane.getViewport();
		Point scrollPosition = viewPort.getViewPosition();
		
		int dx = e.getX() - lastMouseDragPoint.x;
		int dy = e.getY() - lastMouseDragPoint.y;

		scrollPosition.x -= dx;
		scrollPosition.y -= dy;
		
		int limitWidth = map.getWidth() - getWidth() + getInsets().left + getInsets().right +
			scrollPane.getVerticalScrollBar().getWidth();
		int limitHeight = map.getHeight() - getHeight() + getInsets().top + getInsets().bottom +
			scrollPane.getHorizontalScrollBar().getHeight();
		
		if (scrollPosition.x > limitWidth) scrollPosition.x = limitWidth;
		if (scrollPosition.y > limitHeight) scrollPosition.y = limitHeight;
		
		if (scrollPosition.x < 0) scrollPosition.x = 0;
		if (scrollPosition.y < 0) scrollPosition.y = 0;
		
		viewPort.setViewPosition(scrollPosition);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() != 2) return;
		if (busy) return;
		if (mode != MODE_STUMBLE) return;
		
		map.addPoint("@", new Point(e.getX(), e.getY()), "Train");		
		map.setStatus(null);
		map.repaint();
		map.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		busy = true;
		
		Location location = map.normToLatLong(xyToNorm(e.getPoint()));
		
		Request request = new Request();
		request.setResourceRef("http://" + serviceHostNames.get(selectedService) + ":" +
				servicePorts.get(selectedService));
		request.setMethod(Method.POST);
		
		String os = System.getProperty("os.name");
		String osWords[] = os.split(" ");
		String platform = osWords[0];
		
		Form form = new Form();
		form.add("signalData", spotter.getSignalData());
		form.add("latitude", location.getLatitude() + "");
		form.add("longitude", location.getLongitude() + "");
		form.add("platform", platform);
		request.setEntity(form.getWebRepresentation());
		
		org.restlet.Client client = new org.restlet.Client(Protocol.HTTP);
		Response response = client.handle(request);
		
		if (response.getStatus().isSuccess()) {
			try {
				response.getEntity().write(System.out);
			} catch (IOException err) {
				err.printStackTrace();
			}
			System.out.println("\nLocation successfuly stumbled.");
		} else {
			System.out.println("Failure!");
			System.out.println(response.getStatus().getDescription());
		}
		
		map.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		busy = false;
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {} 
	public void mousePressed(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	
	public void serviceFound(DNSSDService browser, int flags, int IfIndex, String name, String regType,
			String domain) {
		try {
			DNSSD.resolve(0, DNSSD.ALL_INTERFACES, name, regType, domain, this);
		} catch (DNSSDException e) {
			e.printStackTrace();
		}
	}

	public void serviceLost(DNSSDService browser, int flags, int IfIndex, String name, String regType,
			String domain) {
		serviceHostNames.remove(name);
		servicePorts.remove(name);
		updateServicesList();
	}
	
	public void operationFailed(DNSSDService browser, int errorCode) {
		System.err.println("DNSSD operation failed " + errorCode);
	}

	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		String service = txtRecord.getValueAsString("implements");
		if (service.compareTo("geolocation.resolver") == 0 && fullName.contains("local.")) {
			
			System.out.println(fullName);
		
			
			String name = fullName.substring(0, fullName.indexOf(".")).replaceAll("\\\\032", " ");
			serviceHostNames.put(name, hostName);
			servicePorts.put(name, port);
			updateServicesList();
			System.out.println("HOST: " + hostName + ", PORT:" + port);
		}
	}
	
	private class CoordsDialog extends JDialog {
		private static final long serialVersionUID = -369094238050302571L;
		private JFormattedTextField tfNELat, tfNELon, tfSELat, tfSELon, tfSWLat, tfSWLon,
			tfNWLat, tfNWLon;
		private NumberFormat numberFormat;
		
		public CoordsDialog(JFrame owner) {
			this(owner, true);
		}

		public CoordsDialog(JFrame owner, boolean exitIfCancelled) {
			super(owner, true);
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			setLocationByPlatform(true);
			setResizable(false);
			
			JLabel label = new JLabel("<html>Please enter the corresponding coordinates for each<br>" +
					"of the four corners of the map.</html>");
			label.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
			getContentPane().add(label, BorderLayout.NORTH);
			
			JPanel container = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			c.anchor = GridBagConstraints.CENTER;
						
			label = new JLabel("Latitude");
			label.setForeground(Color.DARK_GRAY);
			c.gridx = 1; c.gridy = 0; container.add(label, c);
			
			c.insets = new Insets(0, 0, 0, 32);
			label = new JLabel("Longitude");
			label.setForeground(Color.DARK_GRAY);
			c.gridx = 2; c.gridy = 0; container.add(label, c);
			
			c.anchor = GridBagConstraints.EAST;
			c.insets = new Insets(0, 32, 0, 4);
			label = new JLabel("Northeast:"); c.gridx = 0; c.gridy = 1; container.add(label, c);
			label = new JLabel("Southeast:"); c.gridx = 0; c.gridy = 2; container.add(label, c);
			label = new JLabel("Southwest:"); c.gridx = 0; c.gridy = 3; container.add(label, c);
			label = new JLabel("Northwest:"); c.gridx = 0; c.gridy = 4; container.add(label, c);
			
			if (numberFormat == null) {
				DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				dfs.setDecimalSeparator('.');
				numberFormat = new DecimalFormat("0.0000000", dfs);
			}
			
			c.weightx = 1.5;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0, 0, 0, 0);
			
			tfNELat = new JFormattedTextField(numberFormat);
			c.gridx = 1; c.gridy = 1; container.add(tfNELat, c);
			tfSELat = new JFormattedTextField(numberFormat);
			c.gridx = 1; c.gridy = 2; container.add(tfSELat, c);
			tfSWLat = new JFormattedTextField(numberFormat);
			c.gridx = 1; c.gridy = 3; container.add(tfSWLat, c);
			tfNWLat = new JFormattedTextField(numberFormat);
			c.gridx = 1; c.gridy = 4; container.add(tfNWLat, c);
			
			c.insets = new Insets(0, 0, 0, 32);
			
			tfNELon = new JFormattedTextField(numberFormat);
			c.gridx = 2; c.gridy = 1; container.add(tfNELon, c);
			tfSELon = new JFormattedTextField(numberFormat);
			c.gridx = 2; c.gridy = 2; container.add(tfSELon, c);
			tfSWLon = new JFormattedTextField(numberFormat);
			c.gridx = 2; c.gridy = 3; container.add(tfSWLon, c);
			tfNWLon = new JFormattedTextField(numberFormat);
			c.gridx = 2; c.gridy = 4; container.add(tfNWLon, c);
			
			this.getContentPane().add(container, BorderLayout.CENTER);
			
			container = new JPanel(new GridBagLayout());
			
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.EAST;
			c.insets = new Insets(12, 0, 12, 0);
			c.gridx = 0; c.gridy = 0;
			JButton button = new JButton("Cancelar");
			if (exitIfCancelled)
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);					
					}				
				});
			else button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			container.add(button, c);
			
			c.anchor = GridBagConstraints.WEST;
			c.gridx = 1;
			button = new JButton("OK");
			button.setFocusable(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tfNELat.getText().length() == 0 || tfNELon.getText().length() == 0 ||
							tfSELat.getText().length() == 0 || tfSELon.getText().length() == 0 ||
							tfSWLat.getText().length() == 0 || tfSWLon.getText().length() == 0 ||
							tfNWLat.getText().length() == 0 || tfNWLon.getText().length() == 0) {
						JOptionPane.showMessageDialog(null, "There is at least one location with " +
								"an incorrect format.\nPlease check all are correctly set and try " +
								"again.", "Wrong Format", JOptionPane.WARNING_MESSAGE);
					} else setVisible(false);
				}});
			container.add(button, c);
			
			this.getContentPane().add(container, BorderLayout.SOUTH);
			
			this.pack();
		}
		
		public Location[] getBorderCoords() {
			if (tfNELat.getText().length() == 0 || tfNELon.getText().length() == 0 ||
					tfSELat.getText().length() == 0 || tfSELon.getText().length() == 0 ||
					tfSWLat.getText().length() == 0 || tfSWLon.getText().length() == 0 ||
					tfNWLat.getText().length() == 0 || tfNWLon.getText().length() == 0)
				return null;
			Location[] locations = new Location[4];
			locations[0] = new Location(
					Double.parseDouble(tfNELat.getText()), Double.parseDouble(tfNELon.getText()));
			locations[1] = new Location(
					Double.parseDouble(tfSELat.getText()), Double.parseDouble(tfSELon.getText()));
			locations[2] = new Location(
					Double.parseDouble(tfSWLat.getText()), Double.parseDouble(tfSWLon.getText()));
			locations[3] = new Location(
					Double.parseDouble(tfNWLat.getText()), Double.parseDouble(tfNWLon.getText()));
			return locations;
		}
		
		public void setBorderCoords(Location[] borderCoords) {
			if (borderCoords == null) return;
			tfNELat.setText(numberFormat.format(borderCoords[0].getLatitude()));
			tfNELon.setText(numberFormat.format(borderCoords[0].getLongitude()));
			tfSELat.setText(numberFormat.format(borderCoords[1].getLatitude()));
			tfSELon.setText(numberFormat.format(borderCoords[1].getLongitude()));
			tfSWLat.setText(numberFormat.format(borderCoords[2].getLatitude()));
			tfSWLon.setText(numberFormat.format(borderCoords[2].getLongitude()));
			tfNWLat.setText(numberFormat.format(borderCoords[3].getLatitude()));
			tfNWLon.setText(numberFormat.format(borderCoords[3].getLongitude()));
		}
		
	}

}

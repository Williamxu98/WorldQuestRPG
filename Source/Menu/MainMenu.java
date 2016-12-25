package Menu;

import java.awt.Color; 
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import Client.Client;
import Client.ClientCloud;
import Client.ClientFrame;
import Client.ClientInventory;
import Client.ClientLobby;
import Client.ClientWorld;
import ClientUDP.ClientAccountWindow;
import ClientUDP.ClientServerSelection;
import Imports.Audio;
import Imports.Images;
import Imports.Maps;
import START.StartGame;
import Server.Server;
import Server.ServerFrame;
import Server.ServerGUI;
import Server.ServerManager;
import WorldCreator.CreatorItems;
import WorldCreator.CreatorWorld;

/**
 * The main menu for the game
 * 
 * @author Alex Raita & William Xu
 *
 */
public class MainMenu implements KeyListener {
	static MainMenu main;

	/**
	 * The IP address for the dedicated server
	 */
	public final static String DEDICATED_IP = "159.203.60.120";

	/**
	 * Default port number
	 */
	public final static int DEF_PORT = 9988;
	public final static int DEF_UDP_PORT = 9989;

	// All the panels
	public static ClientFrame mainFrame;
	private static MainPanel mainMenu;
	private static CreatorPanel creatorPanel;
	private static GamePanel gamePanel;
	private static InstructionPanel instructionPanel;

	// Cloud variables
	private static ArrayList<ClientCloud> clouds;
	public final static int CLOUD_DISTANCE = Client.SCREEN_WIDTH * 3;
	public static int cloudDirection = 0;

	private static Client client;
	private static ClientLobby lobby;

	private static String playerName;

	private boolean imagesAudioLoaded = false;
	private boolean mapsLoaded = false;

	private static ClientServerSelection serverList = null;
	private static ClientAccountWindow newLogin = null;

	/**
	 * Whether or not image loading has failed
	 */
	public static boolean imageLoadFailed = false;

	private static boolean canConnect = false;

	private static JButton login;
	/**
	 * Create the initial clouds for the main menu screen
	 */
	public void generateClouds() {
		// Generate clouds
		if ((int) (Math.random() * 2) == 0) {
			cloudDirection = 1;
		} else {
			cloudDirection = -1;
		}

		clouds = new ArrayList<ClientCloud>();
		for (int no = 0; no < 24; no++) {
			double x = Client.SCREEN_WIDTH / 2 + Math.random() * CLOUD_DISTANCE
					- (CLOUD_DISTANCE / 2);
			double y = Math.random() * (Client.SCREEN_HEIGHT * 1.5)
					- (2 * Client.SCREEN_HEIGHT / 3);

			double hSpeed = 0;

			hSpeed = (Math.random() * 0.9 + 0.1) * cloudDirection;

			int imageNo = no;

			while (imageNo >= 6) {
				imageNo -= 6;
			}

			String image = "CLOUD_" + imageNo + "";

			clouds.add(new ClientCloud(x, y, hSpeed, 0, image));
		}
	}

	/**
	 * Constructor
	 */
	public MainMenu() {
		main = this;
		Thread loadImages = new Thread(new LoadImagesAudio());
		loadImages.start();

		// Set up the dimensions of the screen
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		DisplayMode dm = gs.getDisplayMode();

		ClientInventory.INVENTORY_WIDTH = (int) (300 * (Math.min(dm.getWidth(),1920) / 1920.0));

		Client.SCREEN_WIDTH = dm.getWidth() - ClientInventory.INVENTORY_WIDTH;

		boolean tooLarge = false;

		if (Client.SCREEN_WIDTH > 1920 - ClientInventory.INVENTORY_WIDTH) {
			Client.SCREEN_WIDTH = 1920 - ClientInventory.INVENTORY_WIDTH;
			tooLarge = true;
		}
		Client.SCREEN_HEIGHT = dm.getHeight();
		if (Client.SCREEN_HEIGHT > 1080) {
			Client.SCREEN_HEIGHT = 1080;
			tooLarge = true;
		}

		// Display results
		System.out.println(dm.getWidth());
		System.out.println(dm.getHeight());

		if (tooLarge)
		{
			JOptionPane.showMessageDialog(null, "Please set your monitor to 1920x1080 or smaller for an optimized experience");
		}

		mainFrame = new ClientFrame(tooLarge);
		mainFrame.addKeyListener(this);
		mainFrame.requestFocus();

		int enableCloudsAndStars = JOptionPane
				.showConfirmDialog(
						null,
						"Is your computer terrible? (Reduce graphical quality ---> Increase performance)",
						"Select Game Quality",
						JOptionPane.YES_NO_OPTION);
		if (enableCloudsAndStars == JOptionPane.YES_OPTION) {
			ClientWorld.NO_OF_CLOUDS = 0;
		}

		while (!(imagesAudioLoaded)) {
			try {
				Thread.sleep(10);
				if (imageLoadFailed) {
					JOptionPane
					.showMessageDialog(
							null,
							"Failed to load images. Perhaps you are running the jar directly from Eclipse? Or perhaps you need to extract the zip first",
							"Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mainFrame.setLayout(null);
		mainMenu = new MainPanel();
		mainFrame.add(mainMenu);
		mainMenu.revalidate();
		mainFrame.setVisible(true);
		mainMenu.repaint();
		generateClouds();
	}

	private class LoadImagesAudio implements Runnable {

		@Override
		public void run() {
			Images.importImages();
			Audio.importAudio();
			imagesAudioLoaded = true;
		}
	}

	/**
	 * The main JPanel
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class MainPanel extends JPanel implements ActionListener,
	MouseListener {

		int middle = (Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH) / 2;
		Image titleImage = Images.getImage("WorldQuestOnline");
		Image background = Images.getImage("BACKGROUND");
		JButton playGame;
		JButton createServer;
		JButton createMap;
		JButton instructions;

		JButton online;

		Image buttonTrayImage = Images.getImage("ButtonTray");

		Image createMapImage = Images.getImage("CreateAMap");
		Image createMapOver = Images.getImage("CreateAMapClicked");

		Image instructionsImage = Images.getImage("Instructions");
		Image instructionsOver = Images.getImage("InstructionsClicked");

		Image playGameImage = Images.getImage("FindAGame");
		Image playGameOver = Images.getImage("FindAGameClicked");

		Image createServerImage = Images.getImage("CreateAServer");
		Image createServerOver = Images.getImage("CreateAServerClicked");

		private Timer repaintTimer = new Timer(15, this);

		/**
		 * Constructor
		 */
		public MainPanel() {
			// Set the Icon
			mainFrame.setIconImage(Images.getImage("WorldQuestIcon"));

			setDoubleBuffered(true);
			// setBackground(Color.white);

			// setBackground(Color.BLACK);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT);

			mainFrame.requestFocus();

			repaintTimer.start();

			playGame = new JButton(new ImageIcon(playGameImage));
			playGame.setSize(playGameImage.getWidth(null),
					playGameImage.getHeight(null));
			playGame.setLocation(middle - playGameImage.getWidth(null) / 2,
					(int) (630 * (Client.SCREEN_HEIGHT / 1080.0)));
			playGame.setBorder(BorderFactory.createEmptyBorder());
			playGame.setContentAreaFilled(false);
			playGame.setOpaque(false);
			playGame.addActionListener(new GameStart());
			playGame.addMouseListener(this);
			add(playGame);

			createServer = new JButton(new ImageIcon(createServerImage));
			createServer.setSize(createServerImage.getWidth(null),
					createServerImage.getHeight(null));
			createServer.setLocation(middle - createServerImage.getWidth(null)
					/ 2, (int) (720 * (Client.SCREEN_HEIGHT / 1080.0)));
			createServer.setBorder(BorderFactory.createEmptyBorder());
			createServer.setContentAreaFilled(false);
			createServer.setOpaque(false);
			createServer.addActionListener(new StartServer());
			createServer.addMouseListener(this);
			add(createServer);

			createMap = new JButton(new ImageIcon(createMapImage));
			createMap.setSize(createMapImage.getWidth(null),
					createMapImage.getHeight(null));
			createMap.setLocation(middle - createMapImage.getWidth(null) / 2,
					(int) (810 * (Client.SCREEN_HEIGHT / 1080.0)));
			createMap.setBorder(BorderFactory.createEmptyBorder());
			createMap.setContentAreaFilled(false);
			createMap.setOpaque(false);
			createMap.addActionListener(new StartCreator());
			createMap.addMouseListener(this);
			add(createMap);

			instructions = new JButton(new ImageIcon(instructionsImage));
			instructions.setSize(instructionsImage.getWidth(null),
					instructionsImage.getHeight(null));
			instructions.setLocation(middle - instructionsImage.getWidth(null)
					/ 2, (int) (900 * (Client.SCREEN_HEIGHT / 1080.0)));
			instructions.setBorder(BorderFactory.createEmptyBorder());
			instructions.setContentAreaFilled(false);
			instructions.setOpaque(false);
			instructions.addActionListener(new OpenInstructions());
			instructions.addMouseListener(this);
			add(instructions);

			online = new JButton("Play Online");
			online.setSize(instructionsImage.getWidth(null),
					instructionsImage.getHeight(null));
			online.setLocation(middle - instructionsImage.getWidth(null)
					/ 2, (int) (990 * (Client.SCREEN_HEIGHT / 1080.0)));
			//online.setBorder(BorderFactory.createEmptyBorder());
			//online.setContentAreaFilled(false);
			//online.setOpaque(false);
			online.addActionListener(new OnlineButton());
			online.addMouseListener(this);
			add(online);

			ClientAccountWindow.checkLogin();
			if(ClientAccountWindow.loggedIn)
			{
				login = new JButton("Logout");
			}
			else login = new JButton("Login");
			login.setSize(100, 50);
			login.setLocation(Client.SCREEN_WIDTH - 200,50);
			login.setBorder(BorderFactory.createEmptyBorder());
			login.setContentAreaFilled(false);
			login.setOpaque(false);
			login.addActionListener(new LoginButton());
			login.addMouseListener(this);
			add(login);

			setVisible(true);
			repaint();
		}

		/**
		 * Draw the clouds
		 */
		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			graphics.drawImage(background, 0, 0, Client.SCREEN_WIDTH
					+ ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT,
					null);

			// Draw and move the clouds
			for (ClientCloud cloud : clouds) {
				if (cloud.getX() <= Client.SCREEN_WIDTH
						+ ClientInventory.INVENTORY_WIDTH
						&& cloud.getX() + cloud.getWidth() >= -64
						&& cloud.getY() <= Client.SCREEN_HEIGHT
						&& cloud.getY() + cloud.getHeight() >= 0) {
					graphics.drawImage(cloud.getImage(), (int) cloud.getX(),
							(int) cloud.getY(), null);
				}

				if (cloud.getX() < middle - CLOUD_DISTANCE / 2) {
					cloud.setX(middle + CLOUD_DISTANCE / 2);
					cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
							- (2 * Client.SCREEN_HEIGHT / 3));
					cloud.sethSpeed((Math.random() * 0.8 + 0.2)
							* cloudDirection);

				} else if (cloud.getX() > middle + CLOUD_DISTANCE / 2) {
					cloud.setX(middle - CLOUD_DISTANCE / 2);
					cloud.setY(Math.random() * (Client.SCREEN_HEIGHT)
							- (2 * Client.SCREEN_HEIGHT / 3));
					cloud.sethSpeed((Math.random() * 0.8 + 0.2)
							* cloudDirection);
				}
				cloud.setX(cloud.getX() + cloud.gethSpeed());

			}

			// Draw the title image
			graphics.drawImage(titleImage, middle - titleImage.getWidth(null)
					/ 2 - 20, (int) (75 * (Client.SCREEN_HEIGHT / 1080.0)),
					null);

			graphics.drawString("William Xu and Alex Raita", 15, 20);

			graphics.drawString("Press 'ESC' to quit",
					ClientFrame.getScaledWidth(1920) - 120, 20);
			graphics.drawImage(buttonTrayImage,
					middle - buttonTrayImage.getWidth(null) / 2,
					(int) (605 * (Client.SCREEN_HEIGHT / 1080.0)), null);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();

		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		/**
		 * When mouse hovers over a button, change the colour
		 */
		public void mouseEntered(MouseEvent e) {
			if (e.getSource() == createMap) {
				createMap.setIcon(new ImageIcon(createMapOver));
			} else if (e.getSource() == instructions) {
				instructions.setIcon(new ImageIcon(instructionsOver));
			} else if (e.getSource() == playGame) {
				playGame.setIcon(new ImageIcon(playGameOver));
			} else if (e.getSource() == createServer) {
				createServer.setIcon(new ImageIcon(createServerOver));
			}

		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (e.getSource() == createMap) {
				createMap.setIcon(new ImageIcon(createMapImage));
			} else if (e.getSource() == instructions) {
				instructions.setIcon(new ImageIcon(instructionsImage));
			} else if (e.getSource() == playGame) {
				playGame.setIcon(new ImageIcon(playGameImage));
			} else if (e.getSource() == createServer) {
				createServer.setIcon(new ImageIcon(createServerImage));
			}
		}

	}

	/**
	 * The Panel that displays the map creator
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class CreatorPanel extends JPanel {
		/**
		 * Constructor
		 * 
		 * @param fileName
		 *            the file to be used
		 */
		public CreatorPanel(String fileName) {
			setDoubleBuffered(true);
			setBackground(Color.black);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT);

			StringBuilder newFileName = new StringBuilder(fileName);
			while (newFileName.indexOf(" ") >= 0)
				newFileName.setCharAt(newFileName.indexOf(" "), '_');

			CreatorWorld world = null;
			try {
				world = new CreatorWorld(newFileName.toString());
			} catch (NumberFormatException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			JButton menu = new JButton("Main Menu");
			menu.addActionListener(new CreatorMenuButton(world));
			CreatorItems items = new CreatorItems(world, menu);
			world.setLocation(0, 0);
			items.setLocation(Client.SCREEN_WIDTH, 0);

			add(world);
			add(items);
			world.revalidate();
			items.invalidate();
			items.revalidate();
			setVisible(true);
			items.repaint();
		}
	}

	private static class ConnectionTimer implements Runnable
	{
		DatagramSocket socket;
		DatagramPacket receive;
		DatagramPacket send;

		byte[] receiveData;
		byte[] sendData;

		private int port;
		private String IP;

		public ConnectionTimer(String IP, int port)
		{
			this.IP = IP;
			this.port = port;
			canConnect = false;
		}

		public void close()
		{
			if(socket != null)
				socket.close();
		}
		@Override
		public void run() {
			while(true)
			{
				try {
					socket = new DatagramSocket(DEF_UDP_PORT+1);
					receiveData = new byte[1024];
					sendData = "C".getBytes();
					send = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(IP), port);
					socket.send(send);


					receiveData = new byte[1024];
					receive = new DatagramPacket(receiveData, receiveData.length);

					socket.receive(receive);

					//Get input
					String input = new String(receive.getData()).trim();
					if (input.length() == 1 && input.charAt(0) == 'C')
					{
						canConnect = true;
						return;
					}
				} catch (Exception e) {
					return;
				}
			}

		}

	}
	/**
	 * The panel to run the actual game
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	public static class GamePanel extends JPanel {
		static ClientInventory inventory;
		static Socket mySocket = null;

		String serverIP;
		int port;
		BufferedReader input = null;
		PrintWriter output = null;

		/**
		 * Constructor
		 * 
		 * @param serverIP
		 *            the server IP
		 * @param port
		 *            the port number
		 * @param playerName
		 *            the player's name
		 * @throws IOException
		 * @throws NumberFormatException
		 */
		public GamePanel(String serverIPIn, int portIn)
				throws NumberFormatException, IOException {
			serverIP = serverIPIn;
			this.port = portIn;
			boolean connected = false;
			boolean exit = false;
			boolean full = false;

			while (!connected) {
				ConnectionTimer con = new ConnectionTimer(serverIP, port);
				Thread conThread = new Thread(con);
				conThread.start();
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (!canConnect)
				{
					serverIP = JOptionPane
							.showInputDialog("Connection Failed. Please re-enter the IP.");
					if (serverIP == null)
						exit = true;
					else 
					{
						con.close();
						continue;
					}
				}
				con.close();
				if (exit)
					break;
				try {
					mySocket = new Socket(serverIP, port);

					// Set up the output
					output = new PrintWriter(mySocket.getOutputStream());
					output.println("Na " + ClientAccountWindow.savedKey + " "+ClientAccountWindow.savedUser);
					output.flush();

					input = new BufferedReader(new InputStreamReader(
							mySocket.getInputStream()));

					String line = input.readLine();

					if (line.equals("FULL")) {
						System.out.println("ROOMS ARE FULL MESSAGE RECEIVED");
						exit = true;
						full = true;
						input.close();
						output.close();
						mySocket.close();
					}
					else if(line.equals("DOUBLEACC"))
					{
						JOptionPane.showMessageDialog(this, "You are already playing a game on this account!");
						exit = true;
						input.close();
						output.close();
						mySocket.close();
					}
					else if(line.equals("INVALID"))
					{
						JOptionPane.showMessageDialog(this, "Invalid login credentials. Try to logout and login again.");
						exit = true;
						input.close();
						output.close();
						mySocket.close();
					}
					else if(line.equals("ERROR"))
					{
						JOptionPane.showMessageDialog(this, "Error Connecting");
						exit = true;
						input.close();
						output.close();
						mySocket.close();
					}

					connected = true;
				} catch (IOException e) {
					serverIP = JOptionPane
							.showInputDialog("Connection Failed. Please re-enter the IP.");
					if (serverIP == null)
						exit = true;
				}
				if (exit)
					break;
			}

			if (exit) {
				if (full)
					JOptionPane.showMessageDialog(null,
							"All Rooms are full. Please try again later");

				setVisible(false);
				mainFrame.remove(this);
				mainFrame.invalidate();
				mainFrame.validate();

				mainMenu = new MainPanel();
				mainMenu.setVisible(true);
				mainFrame.add(mainMenu);

				mainFrame.setVisible(true);
				mainMenu.revalidate();
			} else {
				JButton menu = new JButton("Main Menu");
				menu.addActionListener(new LobbyMenuButton());

				lobby = new ClientLobby(mySocket, input, output, this,
						clouds, menu);
				lobby.setLocation(0, 0);
				lobby.setLayout(null);
				lobby.setSize(Client.SCREEN_WIDTH
						+ ClientInventory.INVENTORY_WIDTH, Client.SCREEN_HEIGHT);
				lobby.setDoubleBuffered(true);
				mainFrame.add(lobby);
				lobby.repaint();

			}
		}

		public void startGame(ClientLobby lobby) throws UnknownHostException,
		IOException {
			lobby.setVisible(false);
			mainFrame.remove(lobby);
			mainFrame.invalidate();
			mainFrame.validate();
			lobby = null;

			JLayeredPane pane = new JLayeredPane();
			pane.setLocation(0, 0);
			pane.setLayout(null);
			pane.setSize(Client.SCREEN_WIDTH, Client.SCREEN_HEIGHT);
			pane.setDoubleBuffered(true);
			mainFrame.add(pane);
			pane.setVisible(true);

			JButton menu = new JButton("Main Menu");
			menu.addActionListener(new GameMenuButton());
			inventory = new ClientInventory(menu);
			mySocket.close();
			mySocket = new Socket(serverIP, port);
			
			try {
				mySocket.setReceiveBufferSize(1024*16);
				mySocket.setSendBufferSize(1024);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Set up the output
			output = new PrintWriter(mySocket.getOutputStream());
			output.println("Na " + ClientAccountWindow.savedKey + " "+ClientAccountWindow.savedUser);
			output.flush();

			client = new Client(mySocket, output, inventory, pane);
			inventory.setClient(client);
			inventory.setBackground(Color.BLACK);

			client.setLocation(0, 0);
			inventory.setLocation(Client.SCREEN_WIDTH, 0);

			pane.add(client);
			mainFrame.add(inventory);
			client.initialize();
			client.revalidate();
			inventory.revalidate();
			pane.revalidate();
			pane.setVisible(true);
			mainFrame.setVisible(true);

			inventory.repaint();
		}
	}

	/**
	 * Opens the menu to select a server
	 */
	private static class OnlineButton implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			if(!ClientAccountWindow.loggedIn)
			{
				JOptionPane.showMessageDialog(MainMenu.mainFrame, "You are not logged in!");
				return;
			}
			if(ClientAccountWindow.open)
			{
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}

			if(ClientServerSelection.open)
			{
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			serverList = null;
			try {
				serverList = new ClientServerSelection(DEF_UDP_PORT);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread listThread = new Thread(serverList);
			listThread.start();


		}

	}

	/**
	 * Reacts to login/logout
	 */
	private static class LoginButton implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(ClientServerSelection.open)
			{
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if(ClientAccountWindow.loggedIn)
			{
				ClientAccountWindow.logout();
				login.setText("Login");
				JOptionPane.showMessageDialog(mainFrame, "Successfully Logged Out!");
				return;
			}
			newLogin = null;
			try {
				newLogin = new ClientAccountWindow(DEF_UDP_PORT, login);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread loginThread = new Thread(newLogin);
			loginThread.start();

		}

	}
	/**
	 * Reacts when the menu button in the creator is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class CreatorMenuButton implements ActionListener {
		private CreatorWorld creator;

		public CreatorMenuButton(CreatorWorld creator) {
			this.creator = creator;
		}

		public void actionPerformed(ActionEvent e) {
			int dialogResult = JOptionPane.NO_OPTION;
			if (!creator.justSaved())
				dialogResult = JOptionPane.showConfirmDialog(null,
						"Do you want to save your map?", "Warning",
						JOptionPane.YES_NO_CANCEL_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION)
				try {
					creator.save();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if (dialogResult != JOptionPane.CANCEL_OPTION) {
				creatorPanel.setVisible(false);
				mainFrame.remove(creatorPanel);
				mainFrame.invalidate();
				mainFrame.validate();
				creatorPanel = null;

				mainMenu = new MainPanel();
				mainFrame.add(mainMenu);
				mainFrame.setVisible(true);
				mainMenu.revalidate();
				mainFrame.requestFocus();

			}
		}
	}

	/**
	 * Reacts when the menu button in the lobby is pressed
	 * 
	 * @author Alex Raita & William Xu
	 */
	private static class LobbyMenuButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("went to menu from lobby");
			lobby.setVisible(false);
			lobby.close();
			mainFrame.remove(lobby);
			mainFrame.invalidate();
			mainFrame.validate();
			lobby = null;

			mainMenu = new MainPanel();
			mainFrame.add(mainMenu);
			mainFrame.setVisible(true);
			mainFrame.requestFocus();
			mainMenu.revalidate();
		}

	}

	/**
	 * The instruction menu
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class InstructionPanel extends JPanel implements
	ActionListener {
		int currentPanel = 0;
		JButton next;
		JButton previous;

		Image objective = Images.getImage("Objective");
		Image controls = Images.getImage("Controls");
		Image stats = Images.getImage("Stats");

		/**
		 * Constructor
		 */
		public InstructionPanel() {
			setDoubleBuffered(true);
			setBackground(Color.red);
			setFocusable(true);
			setLayout(null);
			setLocation(0, 0);
			requestFocusInWindow();
			setSize(Client.SCREEN_WIDTH + ClientInventory.INVENTORY_WIDTH,
					Client.SCREEN_HEIGHT);

			Image nextImage = Images.getImage("Next");
			next = new JButton(new ImageIcon(nextImage));
			next.setSize(nextImage.getWidth(null), nextImage.getHeight(null));
			next.setLocation(Client.SCREEN_WIDTH
					+ ClientInventory.INVENTORY_WIDTH - 350,
					Client.SCREEN_HEIGHT - 200);
			next.setBorder(BorderFactory.createEmptyBorder());
			next.setContentAreaFilled(false);
			next.setOpaque(false);
			next.addActionListener(this);
			add(next);

			setVisible(true);

			repaint();
		}

		/**
		 * Paints an image depending on the screen the user is on
		 */
		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);

			if (currentPanel == 0)
				graphics.drawImage(objective, 0, 0, null);
			else if (currentPanel == 1)
				graphics.drawImage(controls, 0, 0, null);
			else if (currentPanel == 2)
				graphics.drawImage(stats, 0, 0, null);

		}

		/**
		 * Increase the currentPanel until it reaches the main menu again
		 */
		public void actionPerformed(ActionEvent e) {
			currentPanel++;

			if (currentPanel == 3) {
				setVisible(false);
				mainFrame.remove(this);
				mainFrame.invalidate();
				mainFrame.validate();

				mainMenu = new MainPanel();
				mainFrame.add(mainMenu);
				mainFrame.setVisible(true);
				mainMenu.revalidate();
			}
			repaint();

		}
	}

	static boolean addedKeyListener = false;

	/**
	 * Reacts when the menu button in game is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class GameMenuButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			client.leaveGame = true;
			client.getOutput().close();
			StartGame.restart(mainFrame);
			addedKeyListener = false;

		}
	}

	/**
	 * Starts the game when this button is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class GameStart implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(!ClientAccountWindow.loggedIn)
			{
				JOptionPane.showMessageDialog(MainMenu.mainFrame, "You are not logged in!");
				return;
			}
			if(ClientServerSelection.open)
			{
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if(ClientAccountWindow.open)
			{
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}
			// Get user info. If it invalid, then ask for it again or exit back
			// to the main menu
			String serverIP;
			int port = DEF_PORT;
			// String playerName;

			serverIP = JOptionPane
					.showInputDialog("Enter the server IP or leave blank for a server on this machine");
			if (serverIP == null) {
				mainFrame.requestFocus();
				return;
			} else if ((serverIP.trim()).equals("")) {
				serverIP = "127.0.0.1";
			}

			port = DEF_PORT;

			mainFrame.remove(mainMenu);
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			try {
				gamePanel = new GamePanel(serverIP, port);
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mainFrame.add(gamePanel);
			mainFrame.setVisible(true);
			gamePanel.revalidate();

		}

	}

	public static void joinLobby(String IP, int port)
	{
		mainFrame.remove(mainMenu);
		mainFrame.invalidate();
		mainFrame.validate();
		mainMenu = null;

		try {
			gamePanel = new GamePanel(IP, port);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mainFrame.add(gamePanel);
		mainFrame.setVisible(true);
		gamePanel.revalidate();
	}

	/**
	 * Starts the server when this button is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class StartServer implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(ClientServerSelection.open)
			{
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if(ClientAccountWindow.open)
			{
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}
			int maxRooms;
			String name;
			Images.importImages();
			Audio.importAudio();
			Maps.importMaps();
			while (true) {
				try {
					String maxRoomsStr = JOptionPane
							.showInputDialog("What is the maximum number of gamerooms you would like?");
					if (maxRoomsStr == null)
					{
						mainFrame.requestFocus();
						return;
					}
					maxRooms = Integer.parseInt(maxRoomsStr);
					if (maxRooms < 1)
						maxRooms = 1;
					break;
				} catch (Exception E) {

				}
			}

			while (true) {
				try {
					name = JOptionPane
							.showInputDialog("What would you like to name the room? (No spaces)");
					if (name == null)
					{
						mainFrame.requestFocus();
						return;
					}
					if (name.contains(" ") || name.equals(""))
						throw new Exception();
					break;
				} catch (Exception E) {

				}
			}

			int portNum = DEF_PORT;
			// while (true)
			// {
			// String port = JOptionPane
			// .showInputDialog("Please enter the port you want to use for the server (Default "
			// + DEF_PORT + ")");
			// if (port == null)
			// return;
			// else if (port.equals(""))
			// {
			// port = "" + DEF_PORT;
			// }
			// try
			// {
			// portNum = Integer.parseInt(port);
			// break;
			// }
			// catch (NumberFormatException E)
			// {
			// }
			// }

			// Starts the server
			System.out.println("rooms " + maxRooms);
			ServerManager server = null;
			try {
				server = new ServerManager(name, portNum, maxRooms,
						mainFrame);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Thread serverThread = new Thread(server);

			serverThread.start();

		}

	}

	/**
	 * Starts the creator when this button is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class StartCreator implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Get filename. If it is invalid, exit
			if(ClientServerSelection.open)
			{
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if(ClientAccountWindow.open)
			{
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}

			String fileName = "";
			String[] mapNames = null;
			final String DEFAULT_MAP_NAME = "New Map Name";
			try {
				BufferedReader maps = new BufferedReader(new FileReader(
						new File("Resources", "Maps")));
				int numMaps = Integer.parseInt(maps.readLine());
				mapNames = new String[numMaps + 1];
				mapNames[0] = DEFAULT_MAP_NAME;
				for (int i = 0; i < numMaps; i++) {
					mapNames[i + 1] = maps.readLine();
				}
				maps.close();
			} catch (Exception E) {
				E.printStackTrace();
			}

			while (true) {
				try {
					final JComboBox<String> jcb = new JComboBox<String>(
							mapNames);
					jcb.setEditable(true);
					jcb.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							String selectedItem = (String) jcb
									.getSelectedItem();
							boolean editable = selectedItem instanceof String
									&& ((String) selectedItem)
									.equals(DEFAULT_MAP_NAME);
							jcb.setEditable(editable);
						}
					});
					int result = JOptionPane.showOptionDialog(null, jcb,
							"Choose A Map To Edit", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, new String[] {
									"Next", "Cancel" }, null);
					if (result == JOptionPane.NO_OPTION
							|| result == JOptionPane.CLOSED_OPTION)
						throw new NullPointerException();
					fileName = ((String) jcb.getSelectedItem()).trim();
				} catch (NullPointerException e2) {
					mainFrame.requestFocus();
					return;
				}

				if (fileName != null && !fileName.isEmpty()) {
					break;
				}
			}
			mainFrame.remove(mainMenu);
			mainFrame.invalidate();
			mainFrame.validate();
			mainMenu = null;

			creatorPanel = new CreatorPanel(fileName);
			mainFrame.add(creatorPanel);
			mainFrame.setVisible(true);
			creatorPanel.revalidate();

		}

	}

	/**
	 * Open the instructions when this button is pressed
	 * 
	 * @author Alex Raita & William Xu
	 *
	 */
	private static class OpenInstructions implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(ClientServerSelection.open)
			{
				serverList.setVisible(true);
				serverList.toFront();
				return;
			}
			if(ClientAccountWindow.open)
			{
				newLogin.setVisible(true);
				newLogin.toFront();
				return;
			}

			JOptionPane
			.showMessageDialog(
					null,
					"We are updating the instructions! The controls are shown in the lobby.",
					"Sorry", JOptionPane.ERROR_MESSAGE);

			mainFrame.requestFocus();
			// mainFrame.remove(mainMenu);
			// mainFrame.invalidate();
			// mainFrame.validate();
			// mainMenu = null;
			//
			// instructionPanel = new InstructionPanel();
			// mainFrame.add(instructionPanel);
			// mainFrame.setVisible(true);
			// instructionPanel.revalidate();
			// instructionPanel.repaint();

		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}

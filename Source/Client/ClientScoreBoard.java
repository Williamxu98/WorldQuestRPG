package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.PriorityQueue;

import javax.swing.JButton;
import javax.swing.JPanel;

import Imports.Images;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

@SuppressWarnings("serial")
public class ClientScoreBoard extends JPanel implements ActionListener{

	private PriorityQueue<ClientPlayerScore> redTeam = new PriorityQueue<ClientPlayerScore>();
	private PriorityQueue<ClientPlayerScore> blueTeam = new PriorityQueue<ClientPlayerScore>();
	Image scoreboardImage;
	public static boolean gameover = false;
	private String winner = "Red Team";
	private int team = 0;
	private JButton mainMenu;
	
	public ClientScoreBoard(Client client)
	{
		setDoubleBuffered(true);
		setFocusable(false);
		setOpaque(false);
		//setBorder(BorderFactory.createLineBorder(Color.black, 10));
		setSize((Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH)/2, Client.SCREEN_HEIGHT/2);
		setLocation((Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH)/4 - ClientInventory.INVENTORY_WIDTH/2, Client.SCREEN_HEIGHT/4);
		scoreboardImage = Images.getImage("scoreboard");
		mainMenu = client.getInventory().mainMenu;
		gameover = false;
	}

	public void setLoser(int loser)
	{
		gameover = true;
		this.team = loser;
		if (team == ServerPlayer.RED_TEAM) 
		{
			winner = "Blue Team";
		}
		//client.getInventory().mainMenu.setLocation(Client.SCREEN_WIDTH/2-50, Client.SCREEN_HEIGHT/2+300);
		repaint();
	}
	
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		int yPos = 100;;

		graphics.drawImage(scoreboardImage, 0, 0, (Client.SCREEN_WIDTH+ClientInventory.INVENTORY_WIDTH)/2, Client.SCREEN_HEIGHT/2, null);

		graphics.setFont(ClientWorld.BIG_NORMAL_FONT);
		if(gameover)
		{
			graphics.setColor(Color.pink);
			if(team == ServerPlayer.RED_TEAM)
				graphics.setColor(Color.CYAN);
			String message = String.format("%s wins!", winner);
			graphics.drawString(message, this.getWidth()/2 - graphics.getFontMetrics().stringWidth(message)/2,this.getHeight() - 85);
			message = "Your ranking has been updated";
			graphics.drawString(message, this.getWidth()/2 - graphics.getFontMetrics().stringWidth(message)/2,this.getHeight() - 65);
			mainMenu.setSize(200, 25);
			mainMenu.setLocation(this.getWidth()/2 - mainMenu.getWidth()/2, this.getHeight() - mainMenu.getHeight() - 25);
			mainMenu.setText("Back to lobby");
			mainMenu.setVisible(true);
			add(mainMenu);
		}
		
		graphics.setColor(Color.WHITE);
		yPos = ClientFrame.getScaledHeight(140);
		
		for(ClientPlayerScore player: redTeam)
		{
			
			int fieldWidth = 130;
			String name = player.getName();
			int currentWidth = graphics.getFontMetrics().stringWidth("...");

			for (int i=0; i<name.length(); i++){
				int letterWidth = graphics.getFontMetrics().stringWidth(name.charAt(i)+"");
				currentWidth += letterWidth;
				if (currentWidth > fieldWidth){
					name = name.substring(0, i) + "...";
					break;
				}
			}
			
			//graphics.drawString(String.format("%-24s%-10d%-10d%-15d%-10d",player.getName(),player.getKills(), player.getDeaths(), player.getScore(), player.getPing()), xPos, yPos);
			graphics.drawString(name, ClientFrame.getScaledWidth(32), yPos);
			graphics.drawString(String.format("%13d",player.getKills()), ClientFrame.getScaledWidth(135), yPos);
			graphics.drawString(String.format("%13d", player.getDeaths()), ClientFrame.getScaledWidth(130) + ClientFrame.getScaledWidth(85), yPos);
			graphics.drawString(String.format("%13d", player.getScore()), ClientFrame.getScaledWidth(130) + ClientFrame.getScaledWidth(155), yPos);
			graphics.drawString(String.format("%13d", player.getPing()), ClientFrame.getScaledWidth(130) + ClientFrame.getScaledWidth(215), yPos);
			yPos += ClientFrame.getScaledHeight(40);
		}

		yPos = ClientFrame.getScaledHeight(140);
		graphics.setColor(Color.WHITE);
		for(ClientPlayerScore player: blueTeam)
		{
			int fieldWidth = 130;
			String name = player.getName();
			int currentWidth = graphics.getFontMetrics().stringWidth("...");
			for (int i=0; i<name.length(); i++){
				int letterWidth = graphics.getFontMetrics().stringWidth(name.charAt(i)+"");
				currentWidth += letterWidth;
				if (currentWidth > fieldWidth){
					name = name.substring(0, i) + "...";
					break;
				}
			}
			//graphics.drawString(String.format("%-24s%-10d%-10d%-15d%-10d",player.getName(),player.getKills(), player.getDeaths(), player.getScore(), player.getPing()), xPos, yPos);
			graphics.drawString(name, ClientFrame.getScaledWidth(525), yPos);
			graphics.drawString(String.format("%13d",player.getKills()), ClientFrame.getScaledWidth(630), yPos);
			graphics.drawString(String.format("%13d", player.getDeaths()), ClientFrame.getScaledWidth(625) + ClientFrame.getScaledWidth(85), yPos);
			graphics.drawString(String.format("%13d", player.getScore()), ClientFrame.getScaledWidth(625) + ClientFrame.getScaledWidth(155), yPos);
			graphics.drawString(String.format("%13d", player.getPing()), ClientFrame.getScaledWidth(625) + ClientFrame.getScaledWidth(215), yPos);
			
			yPos += ClientFrame.getScaledHeight(40);
		}
		
		
	}


	public void addPlayer(String name, int id, int team, int kills, int deaths, int score, int ping)
	{
		if(team == ServerCreature.RED_TEAM)
			redTeam.add(new ClientPlayerScore(name,id, kills, deaths, score, ping));
		else
			blueTeam.add(new ClientPlayerScore(name,id, kills, deaths, score, ping));
	}

	public void addKill(int id, int team)
	{
		PriorityQueue<ClientPlayerScore> players;
		if(team == ServerCreature.RED_TEAM)
			players = redTeam;
		else
			players = blueTeam;

		ClientPlayerScore p = null;
		int kills = 0;
		for(ClientPlayerScore player : players)
			if(player.getId() == id)
			{
				p = player;
				kills = p.getKills()+1;
				p.setKills(-1);
				break;
			}
		players.remove(p);
		p.setKills(kills);
		players.add(p);
	}

	public void addDeath(int id, int team)
	{
		PriorityQueue<ClientPlayerScore> players;
		if(team == ServerCreature.RED_TEAM)
			players = redTeam;
		else
			players = blueTeam;

		ClientPlayerScore p = null;
		int kills = 0;
		for(ClientPlayerScore player : players)
			if(player.getId() == id)
			{
				p = player;
				kills = player.getKills();
				player.setKills(-1);
				break;
			}
		players.remove(p);
		p.setKills(kills);
		p.addDeath();
		players.add(p);
	}

	public void update(int id, int score, int ping, int team)
	{
		if(team == ServerCreature.RED_TEAM)
		{
			for(ClientPlayerScore player : redTeam)
				if(player.getId() == id)
				{
					player.setScore(score);
					player.setPing(ping);
					return;
				}
		}
		else
			for(ClientPlayerScore player : blueTeam)
				if(player.getId() == id)
				{
					player.setScore(score);
					player.setPing(ping);
					return;
				}
	}

	public void removePlayer(int id, int team)
	{
		PriorityQueue<ClientPlayerScore> players;
		if(team == ServerCreature.RED_TEAM)
			players = redTeam;
		else
			players = blueTeam;

		ClientPlayerScore toRemove = null;
		for(ClientPlayerScore player : players)
			if(player.getId() == id)
			{
				player.setKills(-1);
				toRemove = player;
			}
		players.remove(toRemove);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}

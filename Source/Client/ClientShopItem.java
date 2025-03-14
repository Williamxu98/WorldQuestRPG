package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import Imports.Images;
import Server.ServerWorld;
import Server.Creatures.ServerPlayer;
import Server.Items.ServerArmour;
import Server.Items.ServerPotion;
import Server.Items.ServerWeapon;

/**
 * Item for the client shop. Works similar to ClientItem
 * @author Alex Raita & William Xu
 *
 */
@SuppressWarnings("serial")
public class ClientShopItem extends JButton implements ActionListener{

	private String type;
	private int amount;
	private int cost;
	private int row;
	private int col;
	private ClientShop inventory;
	
	//private Image image;

	/**
	 * Constructor
	 */
	public ClientShopItem(String imageName, String type,int amount,int cost,int row, int col, ClientShop inventory)
	{
		super(new ImageIcon(Images.getImage(imageName).getScaledInstance(ClientFrame.getScaledWidth(Images.INVENTORY_IMAGE_SIDELENGTH), ClientFrame.getScaledHeight(Images.INVENTORY_IMAGE_SIDELENGTH), 0)));
		this.type = type;
		this.amount = amount;
		this.cost = cost;
		this.row = row;
		this.col = col;
		this.inventory = inventory;
		
		setSize(ClientFrame.getScaledWidth(Images.INVENTORY_IMAGE_SIDELENGTH),ClientFrame.getScaledHeight(Images.INVENTORY_IMAGE_SIDELENGTH));
		setLocation(ClientFrame.getScaledWidth(col*Images.INVENTORY_IMAGE_SIDELENGTH+(col+1)*20+20),ClientFrame.getScaledHeight(60 + row*(Images.INVENTORY_IMAGE_SIDELENGTH+20)+40));
		setVisible(true);
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusable(false);
		addActionListener(this);

		//Add tooltips
		switch(type)
		{
		case ServerWorld.HP_POTION_TYPE:
			setToolTipText(String.format("+%d HP",ServerPotion.HEAL_AMOUNT));
			break;
		case ServerWorld.MAX_HP_TYPE:
			setToolTipText(String.format("Max HP +%d",ServerPotion.MAX_HP_INCREASE));
			break;
		case ServerWorld.MANA_POTION_TYPE:
			setToolTipText(String.format("+%d Mana",ServerPotion.MANA_AMOUNT));
			break;
		case ServerWorld.MAX_MANA_TYPE:
			setToolTipText(String.format("Max Mana +%d",ServerPotion.MAX_MANA_INCREASE));
			break;
		case ServerWorld.DMG_POTION_TYPE:
			setToolTipText(String.format("+%d%% Base Damage",ServerPotion.DMG_AMOUNT));
			break;
		case ServerWorld.SPEED_POTION_TYPE:
			setToolTipText(String.format("+%d Speed",ServerPotion.SPEED_AMOUNT));
			break;
		case ServerWorld.JUMP_POTION_TYPE:
			setToolTipText(String.format("+%d Jump",ServerPotion.JUMP_AMOUNT));
			break;
		case ServerWorld.MONEY_TYPE:
			setToolTipText("Money");
			break;
		case ServerWorld.STEEL_ARMOUR:
			setToolTipText(String.format("Steel Armour (%.0f%% Damage Reduction)",ServerArmour.STEEL_DEF*100));
			break;
		case ServerWorld.BLUE_NINJA_ARMOUR:
			setToolTipText(String.format("Blue Ninja Armour (%.0f%% Damage Reduction)",ServerArmour.BLUE_DEF*100));
			break;
		case ServerWorld.RED_NINJA_ARMOUR:
			setToolTipText(String.format("Red Ninja Armour (%.0f%% Damage Reduction)",ServerArmour.RED_DEF*100));
			break;
		case ServerWorld.GREY_NINJA_ARMOUR:
			setToolTipText(String.format("Grey Ninja Armour (%.0f%% Damage Reduction)",ServerArmour.GREY_DEF*100));
			break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.DIAMOND_TIER:
			setToolTipText(String.format("Diamond Dagger (+%d Damage)",ServerWeapon.DADIAMOND_DMG));
		break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.GOLD_TIER:
			setToolTipText(String.format("Gold Dagger (+%d Damage)",ServerWeapon.DAGOLD_DMG));
		break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.IRON_TIER:
			setToolTipText(String.format("Iron Dagger (+%d Damage)",ServerWeapon.DAIRON_DMG));
		break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.STONE_TIER:
			setToolTipText(String.format("Stone Dagger (+%d Damage)",ServerWeapon.DASTONE_DMG));
		break;
		case ServerWorld.DAGGER_TYPE + ServerWorld.WOOD_TIER:
			setToolTipText(String.format("Wood Dagger (+%d Damage)",ServerWeapon.DAWOOD_DMG));
		break;
		case ServerWorld.AX_TYPE + ServerWorld.DIAMOND_TIER:
			setToolTipText(String.format("Diamond Ax (+%d Damage)",ServerWeapon.AXDIAMOND_DMG));
		break;
		case ServerWorld.AX_TYPE + ServerWorld.GOLD_TIER:
			setToolTipText(String.format("Gold Ax (+%d Damage)",ServerWeapon.AXGOLD_DMG));
		break;
		case ServerWorld.AX_TYPE + ServerWorld.IRON_TIER:
			setToolTipText(String.format("Iron Ax (+%d Damage)",ServerWeapon.AXIRON_DMG));
		break;
		case ServerWorld.AX_TYPE + ServerWorld.STONE_TIER:
			setToolTipText(String.format("Stone Ax (+%d Damage)",ServerWeapon.AXSTONE_DMG));
		break;
		case ServerWorld.AX_TYPE + ServerWorld.WOOD_TIER:
			setToolTipText(String.format("Wood Ax (+%d Damage)",ServerWeapon.AXWOOD_DMG));
		break;
		case ServerWorld.SWORD_TYPE + ServerWorld.DIAMOND_TIER:
			setToolTipText(String.format("Diamond Sword (+%d Damage)",ServerWeapon.SWDIAMOND_DMG));
		break;
		case ServerWorld.SWORD_TYPE + ServerWorld.GOLD_TIER:
			setToolTipText(String.format("Gold Sword (+%d Damage)",ServerWeapon.SWGOLD_DMG));
		break;
		case ServerWorld.SWORD_TYPE + ServerWorld.IRON_TIER:
			setToolTipText(String.format("Iron Sword (+%d Damage)",ServerWeapon.SWIRON_DMG));
		break;
		case ServerWorld.SWORD_TYPE + ServerWorld.STONE_TIER:
			setToolTipText(String.format("Stone Sword (+%d Damage)",ServerWeapon.SWSTONE_DMG));
		break;
		case ServerWorld.SWORD_TYPE + ServerWorld.WOOD_TIER:
			setToolTipText(String.format("Wood Sword (+%d Damage)",ServerWeapon.SWWOOD_DMG));
		break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.DIAMOND_TIER:
			setToolTipText(String.format("Diamond Halberd (+%d Damage)",ServerWeapon.HADIAMOND_DMG));
		break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.GOLD_TIER:
			setToolTipText(String.format("Gold Halberd (+%d Damage)",ServerWeapon.HAGOLD_DMG));
		break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.IRON_TIER:
			setToolTipText(String.format("Iron Halberd (+%d Damage)",ServerWeapon.HAIRON_DMG));
		break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.STONE_TIER:
			setToolTipText(String.format("Stone Halberd (+%d Damage)",ServerWeapon.HASTONE_DMG));
		break;
		case ServerWorld.HALBERD_TYPE + ServerWorld.WOOD_TIER:
			setToolTipText(String.format("Wood Halberd (+%d Damage)",ServerWeapon.HAWOOD_DMG));
		break;
		case ServerWorld.SLINGSHOT_TYPE:
			setToolTipText(String.format("Slingshot (+%d Damage)",ServerWeapon.SLING_DMG));
			break;
		case ServerWorld.WOODBOW_TYPE:
			setToolTipText(String.format("Wood Bow (+%d Damage)",ServerWeapon.WOODBOW_DMG));
			break;
		case ServerWorld.STEELBOW_TYPE:
			setToolTipText(String.format("Steel Bow (+%d Damage)",ServerWeapon.STEELBOW_DMG));
			break;
		case ServerWorld.MEGABOW_TYPE:
			setToolTipText(String.format("Mega Bow (+%d Damage)",ServerWeapon.MEGABOW_DMG));
			break;
		case ServerWorld.FIREWAND_TYPE:
			setToolTipText(String.format("<html>Fire Wand (+%d Damage)<p>ManaCost: %d",ServerWeapon.FIREWAND_DMG,ServerWeapon.FIREWAND_MANA));
			break;
		case ServerWorld.ICEWAND_TYPE:
			setToolTipText(String.format("<html>Ice Wand (+%d Damage)<p>Mana Cost: %d",ServerWeapon.ICEWAND_DMG,ServerWeapon.ICEWAND_MANA));
			break;
		case ServerWorld.DARKWAND_TYPE:
			setToolTipText(String.format("<html>Dark Wand (+%d Damage)<p>Mana Cost: %d",ServerWeapon.DARKWAND_DMG,ServerWeapon.DARKWAND_MANA));
			break;
		}
	}

	/**
	 * If the item is clicked buy it
	 */
	public void actionPerformed(ActionEvent event) 
	{
		System.out.println("clicked");
		//If the user has enough money
		if(type.equals(ServerWorld.HP_POTION_TYPE) && inventory.getClient().getInventory().getNumHP() >= ServerPlayer.MAX_HP_POTS)
			return;
		if(type.equals(ServerWorld.MANA_POTION_TYPE) && inventory.getClient().getInventory().getNumMana() >= ServerPlayer.MAX_MANA_POTS)
			return;
		
		if(inventory.getClient().getMoney() >= cost)
		{
			inventory.getClient().printToServer("B "+type+" "+ row + " "+col);
		}	
	}

	public void sell()
	{
		inventory.getClient().decreaseMoney(cost);

		if(amount > 1)
		{
			amount--;
		}
		else 
			inventory.removeItem(this);
		repaint();
	}
	
	/**
	 * Paint the amount of the item
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		graphics.setColor(Color.white);
		if(amount >= 10)
			graphics.drawString(amount+"", getWidth()-16, 10);
		else if(amount > 1)
			graphics.drawString(amount+"", getWidth()-8, 10);
	}

	public int getRow()
	{
		return row;
	}

	public int getCol()
	{
		return col;
	}

	public int getCost()
	{
		return cost;
	}

	public void increaseAmount(int amount)
	{
		this.amount += amount;
	}

	public String getType()
	{
		return type;
	}
}

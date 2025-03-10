package Server.Creatures;

import java.util.ArrayList;
import java.util.LinkedList;

import Server.ServerObject;
import Server.ServerWorld;
import Server.Effects.ServerText;
import Server.Items.ServerItem;
import Server.Items.ServerPotion;
import Tools.RowCol;

/**
 * A creature in the world This encompasses players, enemies, chests, vendors,
 * castles
 * @author Alex Raita & William Xu
 *
 */
public abstract class ServerCreature extends ServerObject
{
	/**
	 * Actions
	 */
	public final static int NO_ACTION = 0;
	public final static int SWING = 1;
	public final static int BOW = 2;
	public final static int WAND = 3;
	public final static int BLOCK = 4;
	public final static int HOP = 5;
	public final static int PUNCH = 6;
	public final static int OUT_OF_MANA = 7;
	public final static int SHOOT = 8;

	/**
	 * Teams
	 */
	public final static int NEUTRAL = 0;
	public final static int RED_TEAM = 1;
	public final static int BLUE_TEAM = 2;
	
	/**
	 * Maximum possible HP of the creature
	 */
	private int maxHP;
	/**
	 * Current HP of the creature
	 */
	private int HP;

	/**
	 * Name of the creature
	 */
	private String name;


	/**
	 * Whether this creature is attackable or not
	 */
	private boolean attackable;

	/**
	 * Team of the creature
	 */
	private int team = NEUTRAL;

	/**
	 * Stores the inventory of the creature
	 */
	private ArrayList<ServerItem> inventory = new ArrayList<ServerItem>();

	/**
	 * World that the creature is in
	 */
	private ServerWorld world;

	/**
	 * The amount of resistance to a knockback by a weapon (normally based on
	 * size of the creature)
	 */
	private double knockBackResistance;

	/**
	 * The horizontal direction the creature is facing
	 */
	private String direction;

	/**
	 * The direction to change the character to right after an action
	 */
	private String nextDirection;

	/**
	 * The number of pixels relative to the hitbox to draw the creature (sent to
	 * the client)
	 */
	private double relativeDrawX;

	/**
	 * The number of pixels relative to the hitbox to draw the creature (sent to
	 * the client)
	 */
	private double relativeDrawY;

	/**
	 * The current frame of the creature's animation
	 */
	private RowCol rowCol;

	/**
	 * Whether or not the creature is alive
	 */
	private boolean alive;

	/**
	 * The base damage the creature does
	 */
	private int baseDamage = 0;

	/**
	 * Whether or not the creature has already punched in one instance of punching (to avoid multiple punch detection in one punch)
	 */
	private boolean hasPunched = false;

	/**
	 * Constructor for a creature
	 */
	public ServerCreature(double x, double y, int width, int height,
			double relativeDrawX, double relativeDrawY,
			double gravity, String image, String type, int maxHP,
			ServerWorld world, boolean attackable)
	{
		super(x, y, width, height, gravity, image, type,world.getEngine());

		this.attackable = attackable;
		this.relativeDrawX = relativeDrawX;
		this.relativeDrawY = relativeDrawY;

		this.alive = true;
		this.maxHP = maxHP;
		HP = maxHP;
		this.world = world;

		direction = "RIGHT";
		nextDirection = "RIGHT";

		// Calculate the resistance to knockback based on weight
		knockBackResistance = Math.sqrt((getWidth() * getHeight())) / 16;
		
		this.rowCol = new RowCol(0, 0);
	}

	public boolean isAttackable()
	{
		return attackable;
	}

	public void setAttackable(boolean attackable)
	{
		this.attackable = attackable;
	}

	public void setTeam(int team)
	{
		this.team = team;

		if (team == ServerPlayer.BLUE_TEAM)
		{
			world.addToBlue(this);

			if (getType().equals(ServerWorld.CASTLE_TYPE))
			{
				//System.out.println("BlueCastle " + getX() + " " + getY());
			}
		}
		else if (team == ServerPlayer.RED_TEAM)
		{
			world.addToRed(this);
			if (getType().equals(ServerWorld.CASTLE_TYPE))
			{
				//System.out.println("RedCastle " + getX() + " " + getY());
			}
		}
	}

	@Override
	public void destroy()
	{
		super.destroy();
		if (team == ServerPlayer.BLUE_TEAM)
		{
			world.removeFromBlue(this);
		}
		else if (team == ServerPlayer.RED_TEAM)
		{
			world.removeFromRed(this);
		}
	}

	public int getTeam()
	{
		return team;
	}

	public int getMaxHP()
	{
		return maxHP;
	}

	public int getHP()
	{
		return HP;
	}

	/**
	 * Set HP to a certain amount
	 * 
	 * @param HP
	 */
	public void setHP(int HP)
	{
		this.HP = HP;
	}

	public void addCastleXP(int amount, ServerCreature source)
	{
		//Add xp to the team of the source
		if (source.getType().equals(ServerWorld.PLAYER_TYPE))
		{
			((ServerPlayer)source).addTotalDamage(amount);
			if (source.getTeam() == RED_TEAM)
				world.getRedCastle().addXP(amount);
			else
				world.getBlueCastle().addXP(amount);
		}
		else if (source.getType().equals(ServerWorld.PLAYER_AI_TYPE))
		{
			((ServerAIPlayer)source).addTotalDamage(amount);
			if (source.getTeam() == RED_TEAM)
				world.getRedCastle().addXP(amount);
			else
				world.getBlueCastle().addXP(amount);
		}
	}
	/**
	 * Inflict a certain amount of damage to the npc and destroy if less than 0
	 * hp
	 * 
	 * @param amount
	 */
	public void inflictDamage(int amount, ServerCreature source)
	{
		if (HP > 0)
		{
			addCastleXP(Math.min(amount,HP), source);
			HP -= amount;

			// Where the damage indicator appears
			double damageX = Math.random() * getWidth() + getX();
			double damageY = Math.random() * getHeight() / 2 + getY()
			+ getHeight() / 4;


			char colour = ServerText.YELLOW_TEXT;

			if (amount ==0)
			{
				colour = ServerText.BLUE_TEXT;
			}
			if (getType().equals(ServerWorld.PLAYER_TYPE))
			{
				colour = ServerText.RED_TEXT;
			}

			world.add(new ServerText(damageX, damageY, Integer
					.toString(amount), colour, world));
		}

		// Drop the creature's inventory and kill it when its HP drops below 0
		if (HP <= 0)
		{
			destroy();
			dropInventory();
		}
	}

	/**
	 * Drop every item in the creature's inventory, except for money in the case of the player
	 */
	public void dropInventory()
	{
		ServerItem money = null;
		for (ServerItem item : inventory)
		{
			if (item.getType().equals(ServerWorld.MONEY_TYPE)
					&& getType().substring(0, 2)
					.equals(ServerWorld.PLAYER_TYPE))
				money = item;
			else
				dropItem(item);
		}

		inventory.clear();
		if (money != null)
			inventory.add(money);

	}

	public int addItem(ServerItem item)
	{		
		if (item.getType().charAt(1) == ServerWorld.STACK_TYPE.charAt(1))
			for (ServerItem sItem : inventory)
			{
				if (item.getType().equals(sItem.getType()))
				{
					sItem.increaseAmount(item.getAmount());
					return 1;
				}
			}
		inventory.add(item);
		return 1;
	}

	/**
	 * Drop an item from inventory
	 * @param item
	 */
	public void dropItem(ServerItem item)
	{
		item.setX(getX() + getWidth() / 2);
		item.setY(getY() + getHeight() / 2 - item.getHeight());
		item.makeExist();
		item.startCoolDown(world.getWorldCounter());
		item.setSource(this);
		world.add(item);
		item.setOnSurface(false);
		item.setVSpeed(-Math.random() * 10 - 5);

		if (HP <= 0 && !item.getType().equals(ServerWorld.MONEY_TYPE))
		{
			int direction = Math.random() < 0.5 ? -1 : 1;
			item.setHSpeed(direction * (Math.random() * 5 + 3));
		}
		else
		{
			item.setHSpeed(Math.random() * 5 + 3);
		}

	}

	public void use(String item)
	{
		ServerItem toRemove = null;
		for (ServerItem sItem : inventory)
		{
			if (sItem.getType().equals(item))
			{
				toRemove = sItem;
				switch (item)
				{
				case ServerWorld.HP_POTION_TYPE:
					ServerPlayer thisPlayer = (ServerPlayer) this;
					HP = Math.min(maxHP, HP + ServerPotion.HEAL_AMOUNT);
					thisPlayer.decreaseNumHPPots();
					break;
				case ServerWorld.MAX_HP_TYPE:
					thisPlayer = (ServerPlayer) this;
					thisPlayer.setMaxHP(maxHP + ServerPotion.MAX_HP_INCREASE);
					break;
				case ServerWorld.MANA_POTION_TYPE:
					thisPlayer = (ServerPlayer) this;
					thisPlayer.setMana(Math.min(thisPlayer.getMaxMana(),
							thisPlayer.getMana() + ServerPotion.MANA_AMOUNT));
					thisPlayer.decreaseNumHPPots();
					break;
				case ServerWorld.MAX_MANA_TYPE:
					thisPlayer = (ServerPlayer) this;
					thisPlayer.setMaxMana(thisPlayer.getMaxMana()
							+ ServerPotion.MAX_MANA_INCREASE);
					break;
				case ServerWorld.DMG_POTION_TYPE:
					thisPlayer = (ServerPlayer) this;
					thisPlayer.setBaseDamage(baseDamage+ ServerPotion.DMG_AMOUNT);
					break;
				case ServerWorld.SPEED_POTION_TYPE:
					thisPlayer = (ServerPlayer) this;
					thisPlayer.addHorizontalMovement(ServerPotion.SPEED_AMOUNT);
					break;
				case ServerWorld.JUMP_POTION_TYPE:
					thisPlayer = (ServerPlayer) this;
					thisPlayer.addVerticalMovement(ServerPotion.JUMP_AMOUNT);
					break;

				}
			}
		}

		if (toRemove != null)
			if (toRemove.getAmount() > 1)
				toRemove.decreaseAmount();
			else
				inventory.remove(toRemove);
	}

	public void drop(String item)
	{
		ServerItem toRemove = null;
		for (ServerItem sItem : inventory)
		{
			if (sItem.getType().equals(item))
			{
				toRemove = sItem;
				break;
			}
		}
		if (toRemove.getAmount() > 1)
		{
			toRemove.decreaseAmount();
			dropItem(ServerItem.copy(toRemove));
		}
		else
		{
			inventory.remove(toRemove);
			dropItem(toRemove);
		}
	}

	/**
	 * Punch stuff
	 * @param damage
	 */
	public void punch(int damage)
	{
		// List of creatures we've already punched so we dont hit them
		// twice
		ArrayList<ServerCreature> alreadyPunched = new
				ArrayList<ServerCreature>();

		int startRow = (int) (getY() / ServerWorld.OBJECT_TILE_SIZE);
		int endRow = (int) ((getY() + getHeight()) /
				ServerWorld.OBJECT_TILE_SIZE);

		int y1 = (int)(getY());
		int y2 = (int)(getY()+getHeight());

		int startColumn = (int) ((getX()- getWidth()/2) / ServerWorld.OBJECT_TILE_SIZE);
		int endColumn = (int) ((getX() + getWidth()) /
				ServerWorld.OBJECT_TILE_SIZE);
		int x1 = (int) (getX()- getWidth()/2);
		int x2 = (int) (getX() + getWidth());



		if (getDirection().equals("RIGHT"))
		{
			startColumn = (int) ((getX()) / ServerWorld.OBJECT_TILE_SIZE);
			endColumn = (int) ((getX() + getWidth() + getWidth()/2) /
					ServerWorld.OBJECT_TILE_SIZE);
			x1 = (int)(getX());
			x2 = (int) (getX() + getWidth() + getWidth()/2);
		}

		// Inflict damage to every creature in range of the player's
		// punch
		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{
				for (ServerObject otherObject : getWorld().getObjectGrid()[row][column])
				{
					if (otherObject.getType().charAt(0) == ServerWorld.CREATURE_TYPE
							&& ((ServerCreature) otherObject)
							.isAttackable()
							&& ((ServerCreature) otherObject)
							.getTeam() != getTeam()
							&& otherObject.collidesWith(x1,y1,x2,y2)
							&& !alreadyPunched.contains(otherObject))
					{
						((ServerCreature) otherObject)
						.inflictDamage(damage, this);
						alreadyPunched
						.add((ServerCreature) otherObject);
					}
				}
			}
		}
	}

	/**
	 * Find the nearest enemy creature and attack it (in this case any creature
	 * from the enemy team)
	 */
	public ServerCreature findTarget(int range) {
		LinkedList<ServerCreature> enemyTeam = null;

		if (getTeam() == ServerPlayer.BLUE_TEAM) {
			enemyTeam = getWorld().getRedTeam();
		} else if (getTeam() == ServerPlayer.RED_TEAM) {
			enemyTeam = getWorld().getBlueTeam();
		}
		for (ServerCreature enemy : enemyTeam) {
			if (enemy.isAlive() && quickInRange(enemy, range))
			{
				return enemy;
			}
		}
		return null;
	}

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////
	public ArrayList<ServerItem> getInventory()
	{
		return inventory;
	}
	public double getKnockBackResistance()
	{
		return knockBackResistance;
	}
	public void setKnockBackResistance(double knockBackResistance)
	{
		this.knockBackResistance = knockBackResistance;
	}
	public ServerWorld getWorld()
	{
		return world;
	}
	public void setWorld(ServerWorld world)
	{
		this.world = world;
	}
	public String getDirection()
	{
		return direction;
	}
	public void setDirection(String direction)
	{
		this.direction = direction;
	}
	/**
	 * Get the location to draw the image of the creature (used when the image dimensions are different from the hitbox)
	 * @return
	 */
	public int getDrawX()
	{
		return (int) (getX() + relativeDrawX + 0.5);
	}
	/**
	 * Get the location to draw the image of the creature (used when the image dimensions are different from the hitbox)
	 * @return
	 */
	public int getDrawY()
	{
		return (int) (getY() + relativeDrawY + 0.5);
	}
	public String getNextDirection()
	{
		return nextDirection;
	}
	public void setNextDirection(String nextDirection)
	{
		this.nextDirection = nextDirection;
	}
	public RowCol getRowCol()
	{
		return rowCol;
	}
	public void setRowCol(int row, int col)
	{
		this.rowCol.setRow(row);
		this.rowCol.setColumn(col);
	}
	public boolean isAlive()
	{
		return alive;
	}
	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
	public int getBaseDamage()
	{
		return baseDamage;
	}

	public void setBaseDamage(int baseDamage)
	{
		this.baseDamage = baseDamage;
	}

	public void setMaxHP(int maxHP)
	{
		this.maxHP = maxHP;
	}

	public double getRelativeDrawX()
	{
		return relativeDrawX;
	}

	public void setRelativeDrawX(double relativeDrawX)
	{
		this.relativeDrawX = relativeDrawX;
	}

	public double getRelativeDrawY()
	{
		return relativeDrawY;
	}

	public void setRelativeDrawY(double relativeDrawY)
	{
		this.relativeDrawY = relativeDrawY;
	}

	public boolean isHasPunched()
	{
		return hasPunched;
	}

	public void setHasPunched(boolean hasPunched)
	{
		this.hasPunched = hasPunched;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}

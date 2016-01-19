package Imports;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Client.ClientInventory;
import Client.ClientShop;
import Server.ServerWorld;
import Tools.BinaryTree;
import WorldCreator.CreatorObject;

/**
 * Class that imports and stores all possible images in the game
 * @author William Xu & Alex Raita
 *
 */
public class Images
{
	/**
	 * The format of the images (.jpg, .png, .gif, etc.)
	 */
	public static final String IMAGE_FORMAT = ".png";

	/**
	 * The size the image will be in the inventory
	 */
	public static final int INVENTORY_IMAGE_SIDELENGTH = ClientInventory.INVENTORY_WIDTH
			/ (ClientInventory.WIDTH + 2) - 20;

	/**
	 * Array list of the game images
	 */
	public static BinaryTree<GameImage> images = null;

	/**
	 * Determines whether images were already imported or not
	 */
	private static boolean imported = false;
	
	/**
	 * Modify images using java and write them to the file
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		BufferedImage image = ImageIO.read(new File("RESOURCE_SHEET_3.png"));
		BufferedImage[][] imageTiles = new BufferedImage[image
				.getHeight() / 32][image.getWidth() / 32];
		for (int row = 0; row < imageTiles.length; row++)
		{
			for (int column = 0; column < imageTiles[0].length; column++)
			{
				imageTiles[row][column] = image.getSubimage(
						column * 32, row * 32, 32, 32);
			}
		}

		image = imageTiles[2][5];
		
		double rotationRequired = Math.toRadians(135);
		AffineTransform tx;

		tx = AffineTransform.getRotateInstance(
				rotationRequired, image.getWidth()/2+1, image.getHeight()/2+1);

		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_BILINEAR);

		BufferedImage newImage = op
				.filter(image, null);
		
		ImageIO.write(newImage, "PNG", new File(
				"STEELARROW.png"));
	

	}

	/**
	 * Import the images from the folder
	 * @throws IOException
	 */
	public static void importImages()
	{
		if(imported)
			return;
		
		imported = true;
		// Only import if the images haven't been imported already
		if (images == null)
		{
			images = new BinaryTree<GameImage>();
			BufferedImage image;

			// Import sprite sheets to create images with
			try
			{
				
				// Add all the pics from the player sprite sheets
				String[] slimeSheets = { "GREENSLIME","REDSLIME", "BLUESLIME", "YELLOWSLIME", "DARKSLIME"};
				for (int sheetNo = 0; sheetNo < slimeSheets.length; sheetNo++)
				{
					image = ImageIO.read(new File(
							slimeSheets[sheetNo] + ".png"));
					
					for (int no = 0; no < 9; no++)
					{
						images.add(new GameImage(slimeSheets[sheetNo] + "_" + no + IMAGE_FORMAT,
								image.getSubimage(no * 19, 0, 19, 17), 38, 34));
					}
				}
				
				image = ImageIO.read(new File(
						"GREENSLIME.png"));
				
				for (int no = 0; no < 9; no++)
				{
					images.add(new GameImage("GIANTSLIME" + "_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 19, 0, 19, 17), 144, 102));
				}


				image = ImageIO.read(new File(
						"EXPLOSION0_SHEET.png"));
				for (int no = 0; no < image.getWidth()/32; no++)
				{
					images.add(new GameImage("EXPLOSION0_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 32, 0, 32, 32)));
				}
				
				image = ImageIO.read(new File(
						"EXPLOSION1_SHEET.png"));
				for (int no = 0; no < image.getWidth()/160; no++)
				{
					images.add(new GameImage("EXPLOSION1_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 160, 0, 160, 160),100,100));
				}
				for (int no = 0; no < image.getWidth()/160; no++)
				{
					images.add(new GameImage("EXPLOSION1_" + (no+5) + IMAGE_FORMAT,
							image.getSubimage(no * 160, 160, 160, 160),100,100));
				}
				
				image = ImageIO.read(new File(
						"EXPLOSION2_SHEET.png"));
				for (int no = 0; no < image.getWidth()/64; no++)
				{
					images.add(new GameImage("EXPLOSION2_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 64, 0, 64, 64)));
				}
				
				image = ImageIO.read(new File(
						"EXPLOSION3_SHEET.png"));
				for (int no = 0; no < image.getWidth()/82; no++)
				{
					images.add(new GameImage("EXPLOSION3_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 82, 0, 82, 82)));
				}
				
				image = ImageIO.read(new File(
						"EXPLOSION4_SHEET.png"));
				for (int no = 0; no < image.getWidth()/82; no++)
				{
					images.add(new GameImage("EXPLOSION4_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 82, 0, 82, 82)));
				}
				
				image = ImageIO.read(new File(
						"STAR0.png"));
				for (int no = 0; no < image.getWidth()/24; no++)
				{
					images.add(new GameImage("STAR0_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 24, 0, 24, 24)));
				}
				
				image = ImageIO.read(new File(
						"STAR1.png"));
				for (int no = 0; no < image.getWidth()/24; no++)
				{
					images.add(new GameImage("STAR1_" + no + IMAGE_FORMAT,
							image.getSubimage(no * 24, 0, 24, 24)));
				}
				

				// Add all the pics from the player sprite sheets
				String[] playerSheets = { "BASE_DARK", "BASE_LIGHT",
						"BASE_TAN", "HAIR0BEIGE", "HAIR1BEIGE",
						"HAIR0BLACK", "HAIR1BLACK", "HAIR0BLOND",
						"HAIR1BLOND", "HAIR0GREY", "HAIR1GREY",
						"OUTFITARMOR", "OUTFITNINJABLUE",
						"OUTFITNINJAGREY", "OUTFITNINJARED" };
				for (int no = 0; no < playerSheets.length; no++)
				{
					image = ImageIO.read(new File(playerSheets[no] + ".png"));
					BufferedImage[][] imageTiles = new BufferedImage[5][image
							.getWidth() / 32];
					for (int row = 0; row < imageTiles.length; row++)
					{
						for (int column = 0; column < imageTiles[0].length; column++)
						{
							BufferedImage currentImage = image.getSubimage(
									column * 32, row * 64, 32, 64);

							// Add a right version of this image
							images.add(new GameImage(playerSheets[no]
									+ "_RIGHT_" + row + "_" + column + ".png",
									currentImage, 64, 128));

							AffineTransform tx;
							tx = AffineTransform.getScaleInstance(-1, 1);
							tx.translate(-currentImage.getWidth(null), 0);
							AffineTransformOp op = new AffineTransformOp(tx,
									AffineTransformOp.TYPE_BILINEAR);

							// Add a left version of this image
							images.add(new GameImage(playerSheets[no]
									+ "_LEFT_" + row + "_" + column + ".png",
									op.filter(currentImage, null).getSubimage(
											0, 0,
											currentImage.getWidth(),
											currentImage.getHeight()), 64, 128));
						}
					}

					// Load death images (they are different sizes)

					BufferedImage currentImage = image.getSubimage(32, 5 * 64,
							32, 64);

					// Add a right version of this image
					images.add(new GameImage(playerSheets[no] + "_RIGHT_" + 5
							+ "_" + 1 + ".png", currentImage, 64, 128));

					AffineTransform tx;
					tx = AffineTransform.getScaleInstance(-1, 1);
					tx.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op = new AffineTransformOp(tx,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					images.add(new GameImage(playerSheets[no] + "_LEFT_" + 5
							+ "_" + 1 + ".png", op.filter(currentImage, null)
							.getSubimage(0, 0,
									currentImage.getWidth(),
									currentImage.getHeight()), 64, 128));

					currentImage = image.getSubimage(2 * 32, 5 * 64, 40, 64);
					// Add a right version of this image
					images.add(new GameImage(playerSheets[no] + "_RIGHT_" + 5
							+ "_" + 2 + ".png", currentImage, 80, 128));

					AffineTransform tx2;
					tx2 = AffineTransform.getScaleInstance(-1, 1);
					tx2.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op2 = new AffineTransformOp(tx2,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					images.add(new GameImage(playerSheets[no] + "_LEFT_" + 5
							+ "_" + 2 + ".png", op2.filter(currentImage, null)
							.getSubimage(0, 0,
									currentImage.getWidth(),
									currentImage.getHeight()), 80, 128));

					currentImage = image.getSubimage(4 * 32, 5 * 64, 52, 64);
					// Add a right version of this image
					images.add(new GameImage(playerSheets[no] + "_RIGHT_" + 5
							+ "_" + 4 + ".png", currentImage, 104, 128));

					AffineTransform tx3;
					tx3 = AffineTransform.getScaleInstance(-1, 1);
					tx3.translate(-currentImage.getWidth(null), 0);
					AffineTransformOp op3 = new AffineTransformOp(tx3,
							AffineTransformOp.TYPE_BILINEAR);

					// Add a left version of this image
					images.add(new GameImage(playerSheets[no] + "_LEFT_" + 5
							+ "_" + 4 + ".png", op3.filter(currentImage, null)
							.getSubimage(0, 0,
									currentImage.getWidth(),
									currentImage.getHeight()), 104, 128));

				}
				
				
				// Add all the pics from the player sprite sheets
				String[] goblinSheets = { "GOB","GOBGENERAL","GOBGUARD","GOBKING","GOBKNIGHT","GOBLORD","GOBNINJA","GOBPEASANT","GOBSOLDIER","GOBWIZARD","GOBWORKER",};
				
				for (int no = 0; no < goblinSheets.length; no++)
				{
					image = ImageIO.read(new File(goblinSheets[no] + ".png"));
					BufferedImage[][] imageTiles = new BufferedImage[image.getHeight()/64][image.getWidth() / 32];
					for (int row = 0; row < imageTiles.length; row++)
					{
						for (int column = 0; column < imageTiles[0].length; column++)
						{
							if (row == 1 && column >= 4)
							{
								continue;
							}
							BufferedImage currentImage = image.getSubimage(
									column * 32, row * 64, 32, 64);

							// Add a right version of this image
							images.add(new GameImage(goblinSheets[no]
									+ "_RIGHT_" + row + "_" + column + ".png",
									currentImage, 64, 128));

							AffineTransform tx;
							tx = AffineTransform.getScaleInstance(-1, 1);
							tx.translate(-currentImage.getWidth(null), 0);
							AffineTransformOp op = new AffineTransformOp(tx,
									AffineTransformOp.TYPE_BILINEAR);

							// Add a left version of this image
							images.add(new GameImage(goblinSheets[no]
									+ "_LEFT_" + row + "_" + column + ".png",
									op.filter(currentImage, null).getSubimage(
											0, 0,
											currentImage.getWidth(),
											currentImage.getHeight()), 64, 128));
						}
						
						// Add the death images
						BufferedImage currentImage = image.getSubimage(32*4 + 16, 64,
								32, 64);
						
						// Add a right version of this image
						images.add(new GameImage(goblinSheets[no] + "_RIGHT_" + 1
								+ "_" + 4 + ".png", currentImage, 64, 128));

						AffineTransform tx;
						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-currentImage.getWidth(null), 0);
						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						// Add a left version of this image
						images.add(new GameImage(goblinSheets[no] + "_LEFT_" + 1
								+ "_" + 4 + ".png", op.filter(currentImage, null)
								.getSubimage(0, 0,
										currentImage.getWidth(),
										currentImage.getHeight()), 64, 128));
						
						currentImage = image.getSubimage(32*6 + 8, 64,
								36, 64);
						
						// Add a right version of this image
						images.add(new GameImage(goblinSheets[no] + "_RIGHT_" + 1
								+ "_" + 6 + ".png", currentImage, 84, 128));

						tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-currentImage.getWidth(null), 0);
						op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						// Add a left version of this image
						images.add(new GameImage(goblinSheets[no] + "_LEFT_" + 1
								+ "_" + 6 + ".png", op.filter(currentImage, null)
								.getSubimage(0, 0,
										currentImage.getWidth(),
										currentImage.getHeight()), 84, 128));
					}
				}
				
				
				images.add(new GameImage("OUTFITARMOR_ICON.png",32,32));
				images.add(new GameImage("OUTFITNINJABLUE_ICON.png",32,32));
				images.add(new GameImage("OUTFITNINJARED_ICON.png",32,32));
				images.add(new GameImage("OUTFITNINJAGREY_ICON.png",32,32));

				String[] weapons = { "DAWOOD", "DASTONE", "DAIRON",
						"DAGOLD", "DADIAMOND", "AXWOOD", "AXSTONE", "AXIRON",
						"AXGOLD", "AXDIAMOND", "SWWOOD", "SWSTONE", "SWIRON",
						"SWGOLD", "SWDIAMOND", "HAWOOD", "HASTONE", "HAIRON",
						"HAGOLD", "HADIAMOND" };

				for (int no = 0; no < weapons.length; no++)
				{
					//Add the icon image to the game also
					images.add(new GameImage(weapons[no]+"_ICON.png"));
					
					image = ImageIO.read(new File(
							weapons[no] + ".png"));
					double locationX = image.getWidth() / 2;
					double locationY = image.getHeight() / 2;

					for (int angle = 180; angle > -180; angle -= 15)
					{
						double rotationRequired = Math.toRadians(angle);
						AffineTransform tx;

						tx = AffineTransform.getRotateInstance(
								rotationRequired, locationX, locationY);

						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);

						BufferedImage newImage = op
								.filter(image, null).getSubimage(0, 0,
										image.getWidth(), image.getHeight());

						if (weapons[no].contains("HA"))
						{
							images.add(new GameImage(
									weapons[no] + "_" + angle + ".png",
									newImage));
						}
						else
						{
						images.add(new GameImage(
								weapons[no] + "_" + angle + ".png",
								newImage));
						}
						
					}
				}
				
				images.add(new GameImage("OUTFITARMOR_ICON.png",32,32));
				images.add(new GameImage("OUTFITNINJABLUE_ICON.png",32,32));
				images.add(new GameImage("OUTFITNINJARED_ICON.png",32,32));
				images.add(new GameImage("OUTFITNINJAGREY_ICON.png",32,32));

				images.add(new GameImage("BULLET_0.png"));
				
				String[] projectiles = {"WOODARROW","STEELARROW","MEGAARROW", "FIREBALL_0", "FIREBALL_1", "ICEBALL_0", "ICEBALL_1", "DARKBALL_0", "DARKBALL_1"};

				for (int no = 0; no < projectiles.length; no++)
				{
					image = ImageIO.read(new File(
							projectiles[no] + ".png"));
					double locationX = image.getWidth() / 2;
					double locationY = image.getHeight() / 2;

					for (int angle = 180; angle > -180; angle -= 15)
					{
						double rotationRequired = Math.toRadians(angle);
						AffineTransform tx;
						BufferedImage newImage = image;

						if (angle <-90 || angle >= 90)
						{
							tx = AffineTransform.getScaleInstance(1, -1);
							tx.translate(0, -image.getHeight());
							AffineTransformOp op = new AffineTransformOp(tx,
									AffineTransformOp.TYPE_BILINEAR);
							newImage = op.filter(image, null);
							int cropWidth = Math.min(image.getWidth(), newImage.getWidth());
							int cropHeight = Math.min(image.getHeight(), newImage.getHeight());
							newImage = newImage.getSubimage(0, 0,cropWidth, cropHeight);
							
						}
						
						tx = AffineTransform.getRotateInstance(
								rotationRequired, locationX, locationY);
						
						AffineTransformOp op = new AffineTransformOp(tx,
								AffineTransformOp.TYPE_BILINEAR);
						
						newImage = op.filter(newImage, null);
						
						int cropWidth = Math.min(image.getWidth(), newImage.getWidth());
						int cropHeight = Math.min(image.getHeight(), newImage.getHeight());
						
						
								
						newImage = newImage.getSubimage(0, 0,cropWidth, cropHeight);

						int height = 42;
						int width = 42;
						
						if (no >=7)
						{
							width = 58;
							height = 58;
						}
						else if (no >= 3 && no <= 6)
						{
							width = 48;
							height = 48;
						}
						else if (no == 2)
						{
							width = 81;
							height = 81;
						}
						
						
						images.add(new GameImage(
								projectiles[no] + "_" + (angle) + ".png",
								newImage,width,height));
						//ImageIO.write(newImage, "PNG", new File(projectiles[no] + "_" + (actualAngle) + ".png"));
					}
				}
				
			}
			catch (IOException e)
			{
				System.out.println("Error loading sprite sheets");
				e.printStackTrace();
			}

			// Add the rest of the images normally
			images.add(new GameImage("BRICK.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("GRASS.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("WATER.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("DIRT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("DIRTGRASS.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("COBBLESTONE.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("STONEBRICKS.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("NOTHING.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("BLACK.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("BDIRT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("BDIRTPLAT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			images.add(new GameImage("PLAT.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));
			
			images.add(new GameImage("SLIME_6_ICON.png", 2*ServerWorld.TILE_SIZE,
					2*ServerWorld.TILE_SIZE));
			images.add(new GameImage("RED_CASTLE_ICON.png", 2*ServerWorld.TILE_SIZE,
					2*ServerWorld.TILE_SIZE));
			images.add(new GameImage("BLUE_CASTLE_ICON.png", 2*ServerWorld.TILE_SIZE,
					2*ServerWorld.TILE_SIZE));
			images.add(new GameImage("CHEST_ICON.png", 2*ServerWorld.TILE_SIZE,
					2*ServerWorld.TILE_SIZE));
			images.add(new GameImage("VENDOR_LEFT_ICON.png", 2*ServerWorld.TILE_SIZE,
					2*ServerWorld.TILE_SIZE));
			images.add(new GameImage("SLIME_SPAWN.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));	
			images.add(new GameImage("SLIME_SPAWN_ICON.png", 2*ServerWorld.TILE_SIZE,
							2*ServerWorld.TILE_SIZE));
			images.add(new GameImage("RED_GOBLIN_SPAWN.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));	
			images.add(new GameImage("RED_GOBLIN_SPAWN_ICON.png", 2*ServerWorld.TILE_SIZE,
							2*ServerWorld.TILE_SIZE));
			images.add(new GameImage("BLUE_GOBLIN_SPAWN.png", ServerWorld.TILE_SIZE,
					ServerWorld.TILE_SIZE));	
			images.add(new GameImage("BLUE_GOBLIN_SPAWN_ICON.png", 2*ServerWorld.TILE_SIZE,
							2*ServerWorld.TILE_SIZE));

			for (int no = 0; no < 6; no++)
			{
				images.add(new GameImage("CLOUD_" + no + ".png"));

			}

			images.add(new GameImage("VENDOR_RIGHT.png", 4*ServerWorld.TILE_SIZE, 5*ServerWorld.TILE_SIZE));
			images.add(new GameImage("VENDOR_LEFT.png", 4*ServerWorld.TILE_SIZE, 5*ServerWorld.TILE_SIZE));
			
			images.add(new GameImage("FIREWAND_RIGHT.png"));
			images.add(new GameImage("FIREWAND_LEFT.png"));
			images.add(new GameImage("FIREWAND_ICON.png"));
			
			images.add(new GameImage("ICEWAND_RIGHT.png"));
			images.add(new GameImage("ICEWAND_LEFT.png"));
			images.add(new GameImage("ICEWAND_ICON.png"));
			
			images.add(new GameImage("DARKWAND_RIGHT.png"));
			images.add(new GameImage("DARKWAND_LEFT.png"));
			images.add(new GameImage("DARKWAND_ICON.png"));
			
			images.add(new GameImage("SLINGSHOT_RIGHT.png"));
			images.add(new GameImage("SLINGSHOT_LEFT.png"));
			images.add(new GameImage("SLINGSHOT_ICON.png"));
			
			images.add(new GameImage("WOODBOW_RIGHT.png"));
			images.add(new GameImage("WOODBOW_LEFT.png"));
			images.add(new GameImage("WOODBOW_ICON.png"));
			
			images.add(new GameImage("STEELBOW_RIGHT.png"));
			images.add(new GameImage("STEELBOW_LEFT.png"));
			images.add(new GameImage("STEELBOW_ICON.png"));
			
			images.add(new GameImage("MEGABOW_RIGHT.png"));
			images.add(new GameImage("MEGABOW_LEFT.png"));
			images.add(new GameImage("MEGABOW_ICON.png"));
			
			images.add(new GameImage("CHEST.png",5*ServerWorld.TILE_SIZE,3*ServerWorld.TILE_SIZE));
			images.add(new GameImage("RED_CASTLE.png", 26*ServerWorld.TILE_SIZE, 52*ServerWorld.TILE_SIZE));
			images.add(new GameImage("BLUE_CASTLE.png", 26*ServerWorld.TILE_SIZE,52*ServerWorld.TILE_SIZE));
			images.add(new GameImage("COIN.png",10,10));
			images.add(new GameImage("HP_POTION.png", Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("MANA_POTION.png", Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("HP_POTION_ICON.png",
					INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("MAX_HP_POTION.png", Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("MAX_MANA_POTION.png", Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("DMG_POTION.png", Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("SPEED_POTION.png", Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("JUMP_POTION.png", Images.INVENTORY_IMAGE_SIDELENGTH,Images.INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("MONEY.png", INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH));
			images.add(new GameImage("MONEY_ICON.png",INVENTORY_IMAGE_SIDELENGTH, INVENTORY_IMAGE_SIDELENGTH));

			images.add(new GameImage("BACKGROUND.png",
					Client.Client.SCREEN_WIDTH, Client.Client.SCREEN_HEIGHT));
			
			images.add(new GameImage("FindAGame.png",
					300,100));
			images.add(new GameImage("FindAGameClicked.png",
					300,100));
			images.add(new GameImage("CreateAServer.png",
					400,100));
			images.add(new GameImage("CreateAServerClicked.png",
					400,100));
			images.add(new GameImage("CreateAMap.png",
					300,100));
			images.add(new GameImage("CreateAMapClicked.png",
					300,100));
			images.add(new GameImage("WorldQuestOnline.png"));
			images.add(new GameImage("Instructions.png",
					300,100));
			images.add(new GameImage("InstructionsClicked.png",
					300,100));
			
			images.add(new GameImage("Inventory.png",300,1080));
			
			images.add(new GameImage("Objective.png"));
			images.add(new GameImage("Controls.png"));
			images.add(new GameImage("Stats.png"));
			
			images.add(new GameImage("Next.png"));
			
			images.add(new GameImage("Shop.png",ClientShop.SHOP_WIDTH,ClientShop.SHOP_HEIGHT));
		}
	}

	/**
	 * Get a specific image from the list using the name of the image
	 * @param name the name of the image
	 * @return the image (inside a game image) from the list
	 */
	public static Image getImage(String name)
	{
		try
		{
			return images.get(new GameImage(name, true)).getImage();
		}
		catch (NullPointerException e)
		{
			System.out.println("Could not find image " + name);
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Get a specific image from the list using the name of the image
	 * @param name the name of the image
	 * @return the image (inside a game image) from the list
	 */
	public static Image getImage(String name, int width, int height)
	{
		try
		{
			return images.get(new GameImage(name, true)).getImage()
					.getScaledInstance(width, height, 0);
		}
		catch (NullPointerException e)
		{
			System.out.println("Could not find image " + name);
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Get a specific game image from the list using the name of the image
	 * @param name the name of the image
	 * @return the game image from the list
	 */
	public static GameImage getGameImage(String name)
	{
		try
		{
			return images.get(new GameImage(name, true));
		}
		catch (NullPointerException e)
		{
			System.out.println("Could not find image " + name);
			e.printStackTrace();
			return null;
		}
	}
}

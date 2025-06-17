package vazkii.ambience;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vaskii.ambience.Init.ItemInit;
import vaskii.ambience.objects.items.Ocarina;
import vaskii.ambience.render.CinematicRender;
import vazkii.ambience.Util.Handlers.EventHandlers;
import vazkii.ambience.Util.particles.DripWaterParticleFactory;
import vazkii.ambience.World.Biomes.Area;

public final class SongPicker {

	public static final String EVENT_ATTACKED = "attacked";
	public static final String EVENT_MAIN_MENU = "mainMenu";
	public static final String EVENT_PAUSE = "paused";
	public static final String EVENT_CONNECTING = "connecting";
	public static final String EVENT_DISCONNECTED = "disconnected";
	public static final String EVENT_SLEEPING = "sleeping";
	public static final String EVENT_GAMEOVER = "gameover";
	public static final String EVENT_BOSS = "boss";
	public static final String EVENT_BOSS_WITHER = "bossWither";
	public static final String EVENT_BOSS_DRAGON = "bossEnderDragon";
	public static final String EVENT_IN_NETHER = "nether";
	public static final String EVENT_IN_END = "end";
	public static final String EVENT_HORDE = "horde";
	public static final String EVENT_NIGHT = "night";
	public static final String EVENT_RAIN = "rain";
	public static final String EVENT_LAVA = "lava";
	public static final String EVENT_FALLING = "falling";
	public static final String EVENT_ELYTRA = "flyingelytra";
	public static final String EVENT_UNDERWATER = "underwater";
	public static final String EVENT_DRIPWATERINCAVE = "dripwaterincave";
	public static final String EVENT_UNDERGROUND = "underground";
	public static final String EVENT_OCEANMONUMENT = "oceanMonument";
	public static final String EVENT_DEEP_UNDEGROUND = "deepUnderground";
	public static final String EVENT_LOADINGWORLD = "loadingWorld";
	public static final String EVENT_HIGH_UP = "highUp";
	public static final String EVENT_VILLAGE = "village";
	public static final String EVENT_VILLAGE_NIGHT = "villageNight";
	public static final String EVENT_MINECART = "minecart";
	public static final String EVENT_RANCH = "ranch";
	public static final String EVENT_RANCH_NIGHT = "ranchNight";
	public static final String EVENT_BOAT = "boat";
	public static final String EVENT_HORSE = "horse";
	public static final String EVENT_PIG = "pig";
	public static final String EVENT_FISHING = "fishing";
	public static final String EVENT_DYING = "dying";
	public static final String EVENT_PUMPKIN_HEAD = "pumpkinHead";
	public static final String EVENT_CREDITS = "credits";
	public static final String EVENT_GENERIC = "generic";

	public static Map<String, String[]> eventMap = new HashMap();
	public static Map<Biome, String[]> biomeMap = new HashMap();
	public static Map<String, String[]> areasMap = new HashMap();	
	public static Map<String, String[]> mobMap = new HashMap();
	public static Map<String, String[]> aroundMap = new HashMap();
	public static Map<String, String[]> effectMap = new HashMap();	
	public static Map<String, Long> cinematicMap = new HashMap<String, Long>();	
	public static Map<BiomeDictionary.Type, String[]> primaryTagMap = new HashMap();
	public static Map<BiomeDictionary.Type, String[]> secondaryTagMap = new HashMap();	
	//public static final List<String> speakerMap = new ArrayList<String>();
	public static List<String> transitionsMap = new ArrayList<String>();
	
	public static final Random rand = new Random();

	public static int songTimer = 0;
	private static int uncountDripWater=0;
	
	public static boolean areaSongsLoaded = false;
	public static boolean falling = false;
	
	public static boolean horde=false;

	public static int forcePlayID=-1;
	public static boolean ForcePlaying=false;
	public static int musicLenght=0;	
	public static Ocarina Ocarina;

	public static BlockPos lastPlayerPos=new BlockPos(Vec3d.ZERO);
	public static String StructureName="";
	
	public static void reset() {
		eventMap.clear();
		biomeMap.clear();
		areasMap.clear();
		mobMap.clear();
		aroundMap.clear();
		primaryTagMap.clear();
		secondaryTagMap.clear();
	}
	
	public static boolean GuiSoundOpened=false;

	public static String[] getSongs() {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		World world = mc.world;
		int dimension=0;
		Ocarina = (Ocarina) ItemInit.itemOcarina;

		if (mc.currentScreen instanceof GuiScreenWorking)
			return getSongsForEvent(EVENT_LOADINGWORLD);
		
		if(world!=null)
			dimension=world.provider.getDimension();		
		
		if (mc.currentScreen instanceof GuiScreenOptionsSounds) {
			GuiSoundOpened=true;
		}
		else{
			if(GuiSoundOpened) {
				GuiSoundOpened=false;
				GameSettings settings = Minecraft.getMinecraft().gameSettings;
				EventHandlers.oldVolume=settings.getSoundLevel(SoundCategory.MASTER);
			}
		}
				
		if (mc.currentScreen instanceof GuiConnecting)
			return getSongsForEvent(EVENT_CONNECTING);
		
		if (mc.currentScreen instanceof GuiDisconnected)
			return getSongsForEvent(EVENT_DISCONNECTED);
				
		if (player == null || world == null) {
			areaSongsLoaded = false;
			
			Ambience.dimension=-25412;
			//System.out.println(Ambience.thread.currentSong);
			
			if(Ambience.thread.currentSong!=null)
			{				
				if(getSongsForEvent(EVENT_MAIN_MENU) !=null)
					Ambience.overideBackMusicDimension=true;
				else {					
					EventHandlers.fadeIn=true;
					EventHandlers.fadeInTicks= EventHandlers.FADE_DURATION-1;	
					Ambience.overideBackMusicDimension=false;	
				}
			}
						
			return getSongsForEvent(EVENT_MAIN_MENU);
		}
		if (mc.currentScreen instanceof GuiWinGame)
			return getSongsForEvent(EVENT_CREDITS);
		
		if (mc.currentScreen instanceof GuiIngameMenu)
			return getSongsForEvent(EVENT_PAUSE);
		
		if (mc.currentScreen instanceof GuiSleepMP)
			return getSongsForEvent(EVENT_SLEEPING);
				
		if (mc.currentScreen instanceof GuiGameOver)
			return getSongsForEvent(EVENT_GAMEOVER);

		BlockPos pos = new BlockPos(player);
						
		/*AmbienceEventEvent event = new AmbienceEventEvent.Pre(world, pos);
		MinecraftForge.EVENT_BUS.post(event);
		String[] eventr = getSongsForEvent(event.event);
		if (eventr != null)
			return eventr;
		 */
						
		//Silences all the musics while playing the ocarina
		if(Ocarina.playing) {
            return new String[]{"silent"};
		}
		
		if(!Ambience.ExternalEvent.event.isEmpty())
		{
			return  getSongsForEvent(Ambience.ExternalEvent.event);
		}
		
		double distance = Math.sqrt(player.getDistanceSq(lastPlayerPos.getX(), lastPlayerPos.getY(), lastPlayerPos.getZ()));
		
		if(Ambience.playingJuckebox & distance<50) {

            return new String[]{"silent"};
		}
		
		GuiBossOverlay bossOverlay = mc.ingameGUI.getBossOverlay();
		Map<UUID, BossInfoClient> map = ReflectionHelper.getPrivateValue(GuiBossOverlay.class, bossOverlay,	Ambience.OBF_MAP_BOSS_INFOS);
		if (!map.isEmpty()) {
			try {
				BossInfoClient first = map.get(map.keySet().iterator().next());
				ITextComponent comp = first.getName();
				String type = "";

				if (comp instanceof TextComponentString) {
					type = comp.getStyle().getHoverEvent().getValue().getUnformattedComponentText();
					type = type.substring(type.indexOf("type:\"") + 6, type.length() - 2);
				} else if (comp instanceof TextComponentTranslation) {
					type = ((TextComponentTranslation) comp).getKey();
					if (type.startsWith("entity.") && type.endsWith(".name"))
						type = type.substring(7, type.length() - 5);
				}

				if (type.equals("minecraft:wither")) {
					String[] songs = getSongsForEvent(EVENT_BOSS_WITHER);
					if (songs != null)
						return songs;
				} else if (type.equals("EnderDragon")) {
					String[] songs = getSongsForEvent(EVENT_BOSS_DRAGON);
					if (songs != null)
						return songs;
				}
								

				 //Makes the mob. event have high priority over the boss
				 String[] tokens = type.split(":");				 
				 if(tokens.length>1) {
					 //player.sendStatusMessage(new TextComponentString( tokens[1] ),(true));
					 if (mobMap.containsKey(tokens[1]))
							return mobMap.get(tokens[1]);	
				 }else {
					 if (mobMap.containsKey(type))
					 // player.sendStatusMessage(new TextComponentString( type ),(true));
							return mobMap.get(type);	
				 }
					
				
			} catch (NullPointerException e) {
			}

			
			String[] songs = getSongsForEvent(EVENT_BOSS);
			if (songs != null)
				return songs;
		}

		// Boss and Enemies Battle Musics******************

		if (Ambience.attacked) {			
				try {
					String[] songs = null;
					int countEntities = 0;
					String mobName = null;
					
					int combatDistance= AmbienceConfig.attackedDistance;
								
					List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
						new AxisAlignedBB(player.posX - combatDistance, player.posY - combatDistance, player.posZ - combatDistance, player.posX + combatDistance,
								player.posY + combatDistance, player.posZ + combatDistance));

				for (EntityLivingBase mob : entities) {
					
					mobName = mob.getName().toLowerCase();
					

					if (mob instanceof EntityLiving)
						//if(mob instanceof EntityMob) {
						if( mob.isCreatureType(EnumCreatureType.MONSTER, false) | ((EntityLiving) mob).canAttackClass(player.getClass())) {
						countEntities++;
					}
					
					if (mobMap.containsKey(mobName) & countEntities>0 & !(mob instanceof EntityPlayer))
						return mobMap.get(mobName);			
				}
				
				
				
				//****************
				
				if (mobName != null & countEntities > 0) {
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_ATTACKED+"\\"+dimension); 	
						if(songs==null)
							songs = getSongsForEvent(EVENT_ATTACKED);
					}
					else
						songs = getSongsForEvent(EVENT_ATTACKED);
				}
				
				//****************
				//Termina a musica de ataque
				if (mobName == null || countEntities < 1  || System.currentTimeMillis() > EventHandlers.attackingExitTimer) {
					Ambience.attacked = false;
				}
				
				//**Play horde musig				
				if (countEntities > 5) {
					horde=true;
					
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_HORDE+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_HORDE);
					}
					else
						songs = getSongsForEvent(EVENT_HORDE);
					if (songs != null)
						return songs;
				}else {
					horde=false;
				}

				if (songs != null)
					return songs;
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		int mobAroundDistance = AmbienceConfig.mobAroundDistance;
		List<EntityLivingBase> mobAround = world.getEntitiesWithinAABB(EntityLivingBase.class,
				new AxisAlignedBB(player.posX - mobAroundDistance, player.posY - mobAroundDistance, player.posZ - mobAroundDistance, player.posX + mobAroundDistance,
						player.posY + mobAroundDistance, player.posZ + mobAroundDistance));

		for (EntityLivingBase mob : mobAround) {

			String mobName = mob.getName().toLowerCase();

			if (aroundMap.containsKey(mobName) && !(mob instanceof EntityPlayer))
				return aroundMap.get(mobName);
		}
		
		// ******************
		if (Ambience.forcePlay ) {
			String[] songs = getSongsForEvent("shortcut"+forcePlayID);
			
			if(songs!=null & !ForcePlaying) {
				String[] song= {songs[0]};	
				ForcePlaying=true;
				musicLenght=getSongLenght(song[0]);					
			}

			if(songs!=null)
			{		
				//Pick only the first music in the shortcut event and ignore the rest	
				String[] song= {songs[0]};	
								
				if(song!=null) {				
					// Para a musica no fim dela
					if (songTimer > musicLenght * 39) {
						Ambience.forcePlay = false;
						ForcePlaying=false;
						songTimer = 0;
					} else
						songTimer++;
				}
				
				return song;
			}
			else {
				Ambience.forcePlay=false;
			}
			
			return null;
		}else {
			ForcePlaying=false;
			songTimer=0;
		}

		// ******************

		float hp = player.getHealth();
		if (hp < 7) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_DYING+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_DYING);
			}
			else
				songs = getSongsForEvent(EVENT_DYING);
			
			if (songs != null)
				return songs;
		}
		
		//Events for potions
		Collection<PotionEffect> potions= player.getActivePotionEffects();		
		if(potions!=null && potions.size()>0) {			
			String potionName= ((PotionEffect)potions.toArray()[0]).getEffectName().replace("effect.", "");
			
			if (dimension !=0 ) {
				 if (effectMap.containsKey(potionName+"."+ dimension))
						return effectMap.get(potionName+"."+ dimension);	
				 
				 if (effectMap.containsKey(potionName))
						return effectMap.get(potionName);		
			}else {
				 if (effectMap.containsKey(potionName))
						return effectMap.get(potionName);					
			}			
		}
		
		//Events for the World Structures
		if(!StructureName.isEmpty())
		{		
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(StructureName+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(StructureName);
			}
			else
				songs = getSongsForEvent(StructureName);
			

			getTransition(world,player,StructureName,true);
			
			if (songs != null)
				return songs;		
		}

		/*int monsterCount = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(player.posX - 16,
				player.posY - 8, player.posZ - 16, player.posX + 16, player.posY + 8, player.posZ + 16)).size();
		if (monsterCount > 5) {
			horde=true;
			String[] songs=null;
			//Songs for other dimensions
			if (dimension <-1 | dimension >1 )
				songs = getSongsForEvent(EVENT_HORDE+"\\"+dimension);
			else
				songs = getSongsForEvent(EVENT_HORDE);
			if (songs != null)
				return songs;
		}else {
			horde=false;
		}*/

		if (player.fishEntity != null) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_FISHING+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_FISHING);
			}
			else
				songs = getSongsForEvent(EVENT_FISHING);
			if (songs != null)
				return songs;
		}

		ItemStack headItem = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if (headItem != null && headItem.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN)) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0 ) {
				songs = getSongsForEvent(EVENT_PUMPKIN_HEAD+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_PUMPKIN_HEAD);
			}
			else
				songs = getSongsForEvent(EVENT_PUMPKIN_HEAD);
			if (songs != null)
				return songs;
		}
		int indimension = world.provider.getDimension();

		if (indimension == -1) {
			
			String[] songs = getSongsForEvent(EVENT_IN_NETHER);
			if (songs != null)
				return songs;
		} else if (indimension == 1) {
			String[] songs = getSongsForEvent(EVENT_IN_END);
			if (songs != null)
				return songs;
		}

		Entity riding = player.getRidingEntity();
		if (riding != null) {
			
			//Get songs for custom entities
			if (riding!=null) {
								
				String mobName = EntityList.getEntityString(riding).toLowerCase();
				
				String[] songs=null;
				
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent("riding." + mobName+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent("riding." + mobName);
					}
					else
						songs = getSongsForEvent("riding." + mobName);			
				if (songs != null)
					return songs;
			}	
			
			if (riding instanceof EntityMinecart) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_MINECART+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_MINECART);
				}
				else
					songs = getSongsForEvent(EVENT_MINECART);
				if (songs != null)
					return songs;
			}
			if (riding instanceof EntityBoat) {
				
				String[] songs = null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs= eventMap.get(EVENT_BOAT+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_BOAT);
				}
				if (songs != null)
					return songs;
			}
			if (riding instanceof EntityHorse) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_HORSE+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_HORSE);
				}
				else
					songs = getSongsForEvent(EVENT_HORSE);
				if (songs != null)
					return songs;
			}
			if (riding instanceof EntityPig) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_PIG+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_PIG);
				}
				else
					songs = getSongsForEvent(EVENT_PIG);
				if (songs != null)
					return songs;
			}
			
		}

		if (player.isInsideOfMaterial(Material.WATER) & !Ambience.attacked) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_UNDERWATER+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_UNDERWATER);
			}
			else
				songs = getSongsForEvent(EVENT_UNDERWATER);
			if (songs != null)
				return songs;
		}
				
		if(DripWaterParticleFactory.dripsCount>0) {
			boolean underground = !world.canSeeSky(pos);

			uncountDripWater++;
			if(uncountDripWater >50 & DripWaterParticleFactory.dripsCount>=0) {
				DripWaterParticleFactory.dripsCount--;
				uncountDripWater=0;
			}

			if (underground) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_DRIPWATERINCAVE+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_DRIPWATERINCAVE);
				}
				else
					songs = getSongsForEvent(EVENT_DRIPWATERINCAVE);
				if (songs != null)
					return songs;
			}
		}
				
		if(player.fallDistance>15) 
		{
			falling=true;
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_FALLING+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_FALLING);
			}
			else
				songs = getSongsForEvent(EVENT_FALLING);
			if (songs != null) {
				EventHandlers.playInstant();
				return songs;	
			}
		}
		else {
			if(falling) {
				falling=false;			
				EventHandlers.playInstant();
			}
		}
		
		if (player.isElytraFlying()) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_ELYTRA+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_ELYTRA);
			}
			else
				songs = getSongsForEvent(EVENT_ELYTRA);
			if (songs != null)
				return songs;
		}
		
		if (player.isInLava()) {
			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_LAVA+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_LAVA);
			}
			else
				songs = getSongsForEvent(EVENT_LAVA);
			if (songs != null)
				return songs;
		}
				
		long time = world.getWorldTime() % 24000;
		boolean night = time > 13300 && time < 23200;
		
		// Get Area Songs
		if (world != null) {
			if (Ambience.getWorldData().listAreas != null) {

				for (Area area : Ambience.getWorldData().listAreas) {
					if (area.getDimension() == player.dimension) {
								
						Area currentArea = Area.getPlayerStandingArea(player);
						if(currentArea!=null & currentArea==area) {
								if(night & area.isPlayatNight()) {
									if(area.isInstantPlay())
										EventHandlers.playInstant();
								
									getTransition(world,player,area.getName().toLowerCase(),false);
									return areasMap.get(area.getName());
								}else if(!night) {
									if(area.isInstantPlay())
										EventHandlers.playInstant();
								
									if(area.getRedstoneStrength()==0) {		
										
										getTransition(world,player,area.getName().toLowerCase(),false);
										return areasMap.get(area.getName());	
									}else {			
										String[] songs= areasMap.get(area.getName()+"."+area.getRedstoneStrength());	
										
										getTransition(world,player,area.getName().toLowerCase(),false);
										if(songs!=null)
											return songs;
										else						
											return areasMap.get(area.getName());	
											
									}								
								}
						}		
					}
				}				
			}
		}


		if (world.provider.isSurfaceWorld()) {
			boolean underground = !world.canSeeSky(pos);

			if (underground) {
				if (pos.getY() < 20) {
					String[] songs=null;
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_DEEP_UNDEGROUND+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_DEEP_UNDEGROUND);
					}
					else
						songs = getSongsForEvent(EVENT_DEEP_UNDEGROUND);
					if (songs != null)
						return songs;
				}
				if (pos.getY() < 55) {
					String[] songs=null;
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_UNDERGROUND+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_UNDERGROUND);
					}
					else
						songs = getSongsForEvent(EVENT_UNDERGROUND);
					if (songs != null)
						return songs;
				}
				 else if (world.isRaining()) {
					String[] songs=null;
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_RAIN+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_RAIN);
					}
					else
						songs = getSongsForEvent(EVENT_RAIN);
					if (songs != null)
						return songs;
				}
			}
			 else if (world.isRaining()) {
					String[] songs=null;
					//Songs for other dimensions
					if (dimension !=0) {
						songs = getSongsForEvent(EVENT_RAIN+"\\"+dimension);
						if(songs==null)
							songs = getSongsForEvent(EVENT_RAIN);
					}
					else
						songs = getSongsForEvent(EVENT_RAIN);
					if (songs != null)
						return songs;
				}
			
			if (pos.getY() > 128) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_HIGH_UP+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_HIGH_UP);
				}
				else
					songs = getSongsForEvent(EVENT_HIGH_UP);
				if (songs != null)
					return songs;
			}

			if (night) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_NIGHT+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_NIGHT);
				}
				else
					songs = getSongsForEvent(EVENT_NIGHT);
				if (songs != null)
					return songs;
			}
		}

		List<EntityLiving> EntitiesCount = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(player.getPosition().getX() - 30,
				player.getPosition().getY() - 8, player.getPosition().getZ() - 30, player.getPosition().getX() + 30, player.getPosition().getY() + 8, player.getPosition().getZ() + 30));

		int villagerCount=0, countPassiveMobs=0;
		
		for (EntityLiving mob : EntitiesCount) {	
			if(mob instanceof EntityVillager) 
				villagerCount++;		
			
			if (mob instanceof EntityHorse
				| mob instanceof EntityCow
				| mob instanceof EntityChicken 
				| mob instanceof EntitySheep
				| mob instanceof EntityPig 
				| mob instanceof EntityRabbit
			)
				countPassiveMobs++;		
		}
		
		if (villagerCount > 3) {
			if (night) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_VILLAGE_NIGHT+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_VILLAGE_NIGHT);
				}
				else
					songs = getSongsForEvent(EVENT_VILLAGE_NIGHT);
				if (songs != null)
					return songs;
			}

			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_VILLAGE+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_VILLAGE);
			}
			else
				songs = getSongsForEvent(EVENT_VILLAGE);
			if (songs != null)
				return songs;
		}
		
		if(countPassiveMobs>15) {
			if (night) {
				String[] songs=null;
				//Songs for other dimensions
				if (dimension !=0) {
					songs = getSongsForEvent(EVENT_RANCH_NIGHT+"\\"+dimension);
					if(songs==null)
						songs = getSongsForEvent(EVENT_RANCH_NIGHT);
				}
				else
					songs = getSongsForEvent(EVENT_RANCH_NIGHT);
				if (songs != null)
					return songs;
			}

			String[] songs=null;
			//Songs for other dimensions
			if (dimension !=0) {
				songs = getSongsForEvent(EVENT_RANCH+"\\"+dimension);
				if(songs==null)
					songs = getSongsForEvent(EVENT_RANCH);
			}
			else
				songs = getSongsForEvent(EVENT_RANCH);
			if (songs != null)
				return songs;
		}

		/*event = new AmbienceEventEvent.Post(world, pos);
		MinecraftForge.EVENT_BUS.post(event);
		eventr = getSongsForEvent(event.event);
		if (eventr != null)
			return eventr;	
		 */
		
		if (world != null) {
			if(Ambience.instantPlaying) {
				EventHandlers.playInstant();
				Ambience.instantPlaying=false;
			}
			
			if(eventMap.containsKey("dim"+dimension)) {
				
				return eventMap.get("dim"+dimension);
			}
			
			Chunk chunk = world.getChunkFromBlockCoords(pos);
			Biome biome = chunk.getBiome(pos, world.getBiomeProvider());
			if (biomeMap.containsKey(biome)) 
			{
				Ambience.overideBackMusicDimension=true;
				return biomeMap.get(biome);
			}else {
				Ambience.overideBackMusicDimension=false;
			}

			Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
			for (Type t : types)
				if (primaryTagMap.containsKey(t))
					return primaryTagMap.get(t);
			for (Type t : types)
			{
				if(dimension >=-1 & dimension<=1) {					
					if (secondaryTagMap.containsKey(t))
						return secondaryTagMap.get(t);					
				}else {
					return new String[] {"null"};
				}
			}
		}

		return getSongsForEvent(EVENT_GENERIC);
	}
	
	public static String getSongsString() {
		return StringUtils.join(getSongs(), ",");
	}

	public static String getRandomSong() {
		String[] songChoices = getSongs();

		try {

			if (songChoices != null) {
				if (songChoices.length > 0)
					return songChoices[rand.nextInt(songChoices.length)];
				else
					return null;
			} else
				return null;

		} catch (Exception ex) {
			return null;
		}
	}

	public static String[] getSongsForEvent(String event) {
		
		if (eventMap.containsKey(event))
			return eventMap.get(event);

		return null;
	}

	public static String getSongName(String song) {
		return song == null ? "" : song.replaceAll("([^A-Z])([A-Z])", "$1 $2");
	}
	
	private static int getSongLenght(String song) {
		
		int songLenght=0;
		// Obt�m o tempo do som selecionado********************
		File f = new File(Ambience.ambienceDir+"\\music", song + ".mp3");

		if (f.isFile()) {
			try {
				AudioFile af = AudioFileIO.read(f);
				AudioHeader ah = af.getAudioHeader();
				songLenght = ah.getTrackLength();
			} catch (Exception e) {

			}
		}else {
			songLenght=0;
		}
		// ****************************************************
		return songLenght;
	}
	
public static Long removeDays(String structureName) {
		
		if (cinematicMap.containsKey(structureName))
			return cinematicMap.remove(structureName);

		return null;
	}
	
	public static Long getDays(String structureName) {
		
		if (cinematicMap.containsKey(structureName))
			return cinematicMap.get(structureName);

		return null;
	}
	
	private static void getTransition(World world, EntityPlayer player, String structureName, boolean playSound) {

		if(AmbienceConfig.structuresCinematic)
			if(getDays(structureName)==null) {										
				if(transitionsMap.contains(structureName)) {
					
					if(!cinematicMap.containsKey(structureName) & playSound)
					world.playSound(player, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft:block.end_portal.spawn")),
							SoundCategory.MASTER, (float) 0.5f, (float) 1);
					
					cinematicMap.put(structureName, world.getWorldTime() / 24000);		
					CinematicRender.AREA_LOGO=new ResourceLocation(Reference.MOD_ID,"textures/transitions/"+structureName.toLowerCase()+".png");						
					CinematicRender.ativated=true;
				}
			}
			else 
			{
				Long days;		
				long daysPassed = world.getWorldTime() / 24000;
				days=getDays(structureName);
				
				if(daysPassed-days>=2) {
					removeDays(structureName);
				}						
			}
	}
}

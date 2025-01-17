package noppes.mpm;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumParts;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ModelData extends ModelDataShared implements IExtendedEntityProperties{
	public static ExecutorService saveExecutor = Executors.newFixedThreadPool(1);

	public boolean resourceInit = false;
	public boolean resourceLoaded = false;
	public boolean cloakInnit = false;
	public boolean cloakLoaded = false;

	public boolean didSit = false;

	public boolean webapiActive = false;
	public boolean webapiInit = false;

	public ResourceLocation cloakObject = null;

	public ItemStack backItem;

	public int inLove = 0;
	public int animationTime = -1;

	public EnumAnimation animation = EnumAnimation.NONE;
	public EnumAnimation prevAnimation = EnumAnimation.NONE;
	public int animationStart = 0;
	public long lastEdited = System.currentTimeMillis();

	public short soundType = 0;
	public double prevPosX, prevPosY, prevPosZ;
	public EntityPlayer player = null;

	public int rev = MorePlayerModels.Revision;

	public byte urlType = 0;	//	0:url, 1:url64
	public int modelType = 0; 	// 	0: Steve, 1: Steve64, 2: Alex
	public int size = 5;

	public String url= "";
	public String cloakUrl= "";
	public String displayName = "";

	public ModelData(){
	}
	public NBTTagCompound writeToNBT(){
		NBTTagCompound compound = super.writeToNBT();
		compound.setInteger("Revision", rev);

		compound.setInteger("Animation", animation.ordinal());
		compound.setInteger("Size", size);
		
		compound.setShort("SoundType", soundType);
		compound.setString("DisplayName", displayName);
		compound.setInteger("ModelType", modelType);

		compound.setString("CustomSkinUrl", url);
		compound.setByte("UrlType", urlType);
		compound.setString("CloakUrl", cloakUrl);

		compound.setLong("LastEdited", lastEdited);

		return compound;
	}
	
	public void readFromNBT(NBTTagCompound compound){
		String prevUrl = url;
		String prevCloakUrl = cloakUrl;
		super.readFromNBT(compound);
		rev = compound.getInteger("Revision");
		size = compound.getInteger("Size");
		if(size <= 0)
			size = 5;
		if(size > 10)
			size = 5;

		soundType = compound.getShort("SoundType");
		lastEdited = compound.getLong("LastEdited");
		displayName = compound.getString("DisplayName");
		player.refreshDisplayName();
		setAnimation(compound.getInteger("Animation"));

		url = compound.getString("CustomSkinUrl");
		urlType = compound.getByte("UrlType");
		modelType = compound.getInteger("ModelType");
		cloakUrl = compound.getString("CloakUrl");

		if(!prevUrl.equals(url)) {
			resourceInit = false;
			resourceLoaded = false;
		}
		if(!prevCloakUrl.equals(cloakUrl)){
			cloakLoaded = false;
			cloakInnit = false;
		}
	}

	public void setAnimation(int i) {
		if(i < EnumAnimation.values().length)
			animation = EnumAnimation.values()[i];
		else
			animation = EnumAnimation.NONE;
		setAnimation(animation);
	}

	public void setAnimation(EnumAnimation ani) {
		animationTime = -1;
		animation = ani;
		lastEdited = System.currentTimeMillis();

		if(animation == EnumAnimation.WAVING)
			animationTime = 80;

		if(animation == EnumAnimation.YES || animation == EnumAnimation.NO)
			animationTime = 60;

		if(animation == EnumAnimation.SITTING)
			didSit = true;

		if(player == null || ani == EnumAnimation.NONE)
			animationStart = -1;
		else
			animationStart = player.ticksExisted;
	}

	public EntityLivingBase getEntity(World world, EntityPlayer player){
		if(entityClass == null)
			return null;
		if(entity == null){
			try {
				entity = entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
				entity.readEntityFromNBT(extra);
				if(entity instanceof EntityLiving){
					EntityLiving living = (EntityLiving)entity;
					living.setCurrentItemOrArmor(0, player.getHeldItem());
					living.setCurrentItemOrArmor(1, player.inventory.armorItemInSlot(3));
					living.setCurrentItemOrArmor(2, player.inventory.armorItemInSlot(2));
					living.setCurrentItemOrArmor(3, player.inventory.armorItemInSlot(1));
					living.setCurrentItemOrArmor(4, player.inventory.armorItemInSlot(0));
				}
			} catch (Exception e) {
			}
		}
		return entity;
	}

	public EntityLivingBase getEntity(EntityPlayer player){
		if(entityClass == null)
			return null;
		if(entity == null){
			try {
				entity = entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {player.worldObj});

				entity.readEntityFromNBT(extra);
				if(entity instanceof EntityLiving){
					EntityLiving living = (EntityLiving)entity;
					living.setCurrentItemOrArmor(0, player.getHeldItem());
					living.setCurrentItemOrArmor(1, player.inventory.armorItemInSlot(3));
					living.setCurrentItemOrArmor(2, player.inventory.armorItemInSlot(2));
					living.setCurrentItemOrArmor(3, player.inventory.armorItemInSlot(1));
					living.setCurrentItemOrArmor(4, player.inventory.armorItemInSlot(0));
				}
			} catch (Exception e) {
			}
		}
		return entity;
	}


	public String getHash(){
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			String toHash = arms.toString() + legs.toString() + body.toString() + head.toString();

			if(entityClass != null)
				toHash += entityClass.getCanonicalName();

			toHash += legParts.toString() + headwear + soundType + url;
			
			for(EnumParts e : parts.keySet()){
				toHash += e.name + ":" + parts.get(e).toString();
			}
			byte[] hash = digest.digest(toHash.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder(2*hash.length);
			for(byte b : hash){
				sb.append(String.format("%02x", b&0xff));
			}
          
			return sb.toString();
		} catch (Exception e) {
			
		}
		return "";
	}
	public ModelData copy(){
		ModelData data = new ModelData();
		data.readFromNBT(this.writeToNBT());
		data.resourceLoaded = false;
		data.cloakLoaded = false;
		data.player = player;
		return data;
	}

	public boolean isSleeping() {
		return isSleeping(animation);
	}
	private boolean isSleeping(EnumAnimation animation) {
		return animation == EnumAnimation.SLEEPING_EAST || animation == EnumAnimation.SLEEPING_NORTH ||
				animation == EnumAnimation.SLEEPING_SOUTH || animation == EnumAnimation.SLEEPING_WEST;
	}

	public boolean animationEquals(EnumAnimation animation2) {
		return animation2 == animation || isSleeping() && isSleeping(animation2);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		
	}

	@Override
	public void init(Entity entity, World world) {
		
	}

	public void save(){
		if(player == null)
			return;
		final EntityPlayer player = this.player;
		saveExecutor.submit(() -> {
			try {
				String filename = player.getUniqueID().toString().toLowerCase();
				if(filename.isEmpty())
					filename = "noplayername";
				filename += ".dat";
				File file = new File(MorePlayerModels.dir, filename+"_new");
				File file1 = new File(MorePlayerModels.dir, filename+"_old");
				File file2 = new File(MorePlayerModels.dir, filename);
				CompressedStreamTools.writeCompressed(writeToNBT(), new FileOutputStream(file));
				if(file1.exists()){
					file1.delete();
				}
				file2.renameTo(file1);
				if(file2.exists()){
					file2.delete();
				}
				file.renameTo(file2);
				if(file.exists()){
					file.delete();
				}
			} catch (Exception e) {
				LogWriter.except(e);
			}
		});
	}


	private boolean isBlocked(EntityPlayer player) {
		return !player.worldObj.isAirBlock((int)player.posX, (int)player.posY + 2, (int)player.posZ);
	}



	public void setExtra(EntityLivingBase entity, String key, String value){
		key = key.toLowerCase();

		if(key.equals("breed") && EntityList.getEntityString(entity).equals("doggystyle.Dog")){
			try {
				Method method = entity.getClass().getMethod("setBreedID", int.class);
				method.invoke(entity, Integer.parseInt(value));
				NBTTagCompound comp = new NBTTagCompound();
				entity.writeEntityToNBT(comp);
				extra.setString("EntityData21", comp.getString("EntityData21"));
	    		clearEntity();
			} catch (Exception e) {

			}
		}
	}
}

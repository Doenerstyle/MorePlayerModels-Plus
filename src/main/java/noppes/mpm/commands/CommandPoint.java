package noppes.mpm.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.mpm.ModelData;
import noppes.mpm.PlayerDataController;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class CommandPoint extends MpmCommandInterface {

	@Override
	public String getCommandName() {
		return "point";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] var2) {
		if(!(icommandsender instanceof EntityPlayerMP))
			return;
		EntityPlayerMP player = (EntityPlayerMP) icommandsender;
		ModelData data = PlayerDataController.instance.getPlayerData(player);
		EnumAnimation ani = data.animation == EnumAnimation.POINT?EnumAnimation.NONE:EnumAnimation.POINT;
		Server.sendAssociatedData(player, EnumPackets.ANIMATION, player.getCommandSenderName(), ani);
		data.animation = ani;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/point to point arm";
	}
}

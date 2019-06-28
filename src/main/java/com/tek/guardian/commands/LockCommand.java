package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class LockCommand extends Command {

	public LockCommand() {
		super("lock", Arrays.asList(), "[channel]", "Locks a channel's activity.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length > 1) return false;
		
		if(member.hasPermission(Permission.MANAGE_CHANNEL)) {
			TextChannel lockChannel = channel;
			if(args.length == 1) {
				Optional<TextChannel> channelOpt = Reference.textChannelFromString(guild, args[0]);
				if(channelOpt.isPresent()) {
					lockChannel = channelOpt.get();
				} else {
					channel.sendMessage(Reference.embedError(jda, "The channel specified does not exist.")).queue();
					return true;
				}
			}
			
			if(!profile.getLockedChannels().contains(lockChannel.getId())) {
				Guardian.getInstance().getActionManager().lock(member, profile, channel, lockChannel);
			} else {
				channel.sendMessage(Reference.embedError(jda, "This channel is already locked.")).queue();
			}
		} else {
			channel.sendMessage(Reference.embedError(jda, "You cannot lock channels.")).queue();
		}
		
		return true;
	}

}

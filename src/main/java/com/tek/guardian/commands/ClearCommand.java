package com.tek.guardian.commands;

import java.util.Arrays;

import com.tek.guardian.data.ServerProfile;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class ClearCommand extends Command {

	public ClearCommand() {
		super("clear", Arrays.asList(), "<amount> [user]", "Deletes a bulk of messages.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 2) {
			if(member.hasPermission(Permission.MESSAGE_MANAGE)) {
				/*
				 * CLEAR MESSAGES lmao
				 */
			} else {
				channel.sendMessage("**You cannot delete messages.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

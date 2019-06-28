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

public class ClearCommand extends Command {

	public ClearCommand() {
		super("clear", Arrays.asList(), "<amount> [user]", "Deletes a bulk of messages.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 1 && args.length <= 2) {
			if(member.hasPermission(Permission.MESSAGE_MANAGE)) {
				if(Reference.isInteger(args[0])) {
					int amount = Integer.parseInt(args[0]);
					
					if(amount >= 1 && amount <= 100) {
						if(args.length == 1) {
							Guardian.getInstance().getActionManager().clear(member, channel, amount, profile);
						} else if(args.length == 2) {
							Optional<Member> memberOpt = Reference.memberFromString(guild, args[1]);
							
							if(memberOpt.isPresent()) {
								Guardian.getInstance().getActionManager().clearUser(member, memberOpt.get(), channel, amount, profile);
							} else {
								channel.sendMessage(Reference.embedError(jda, "No member was found by the identifier `" + args[1] + "`.")).queue();
							}
						}
					} else {
						channel.sendMessage(Reference.embedError(jda, "The message amount must be a number from 1-100.")).queue();
					}
				} else {
					channel.sendMessage(Reference.embedError(jda, "Invalid Amount `" + args[0] + "`.")).queue();
				}
			} else {
				channel.sendMessage(Reference.embedError(jda, "You cannot delete messages.")).queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

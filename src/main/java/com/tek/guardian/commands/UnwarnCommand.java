package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.data.UserProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class UnwarnCommand extends Command {

	public UnwarnCommand() {
		super("unwarn", Arrays.asList("remwarn"), "<user> <number/all>", "Removes a warning from a user.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 2) {
			if(member.hasPermission(Permission.MESSAGE_MANAGE)) {
				Optional<Member> memberOpt = Reference.memberFromString(guild, args[0]);
				
				if(memberOpt.isPresent()) {
					if(member.canInteract(memberOpt.get()) && !member.equals(memberOpt.get())) {
						UserProfile userProfile = Guardian.getInstance().getMongoAdapter().getUserProfile(memberOpt.get());
						
						int count;
						if(args[1].equalsIgnoreCase("all")) {
							count = Integer.MAX_VALUE;
						} else {
							if(Reference.isInteger(args[1])) {
								count = Integer.parseInt(args[1]);
							} else {
								channel.sendMessage("**Invalid Amount** `" + args[1] + "`").queue();
								return true;
							}
						}
						
						if((count >= 1 && count <= userProfile.getWarnings().size()) || count == Integer.MAX_VALUE) {
							Guardian.getInstance().getActionManager().unwarn(member, memberOpt.get(), channel, count, profile, userProfile);
						} else {
							channel.sendMessage("**Warning number out of range: 1-" + userProfile.getWarnings().size() + "**").queue();
						}
					} else {
						channel.sendMessage("**You cannot unwarn this person.**").queue();
					}
				} else {
					channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot unwarn members.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

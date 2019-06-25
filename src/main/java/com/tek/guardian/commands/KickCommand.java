package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class KickCommand extends Command {

	public KickCommand() {
		super("kick", Arrays.asList(), "<user> [reason]", "Kicks a member and specifies a reason.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 1) {
			if(member.hasPermission(Permission.KICK_MEMBERS)) {
				String reason;
				if(args.length >= 2) {
					StringBuilder reasonBuilder = new StringBuilder();
					for(int i = 1; i < args.length; i++) reasonBuilder.append(args[i] + " ");
					if(reasonBuilder.length() > 0) reasonBuilder.setLength(reasonBuilder.length() - 1);
					reason = reasonBuilder.toString();
				} else {
					reason = "No reason specified.";
				}
				
				Optional<Member> memberOpt = CommandHandler.fromString(guild, args[0]);
				
				if(memberOpt.isPresent()) {
					if(!memberOpt.get().equals(member) && member.canInteract(memberOpt.get())) {
						memberOpt.get().getUser().openPrivateChannel().queue(pm -> {
							pm.sendMessage("You were kicked from the server **" + guild.getName() + "** for the reason: `" + reason + "`").queue(m -> {
								guild.kick(memberOpt.get(), reason).queue();
							}, e -> {
								guild.kick(memberOpt.get(), reason).queue();
							});
						}, e -> {
							guild.kick(memberOpt.get(), reason).queue();
						});
						
						channel.sendMessage("Successfully kicked " + memberOpt.get().getUser().getAsMention() + " from the server. `" + reason + "`").queue();
					} else {
						channel.sendMessage("**You cannot kick this person.**").queue();
					}
				} else {
					channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot kick members.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class DeafenCommand extends Command {

	public DeafenCommand() {
		super("deafen", Arrays.asList("deaf"), "<user> [reason]", "Deafens a member and specifies a reason.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 1) {
			if(member.hasPermission(Permission.VOICE_DEAF_OTHERS)) {
				String reason;
				if(args.length >= 2) {
					StringBuilder reasonBuilder = new StringBuilder();
					for(int i = 1; i < args.length; i++) reasonBuilder.append(args[i] + " ");
					if(reasonBuilder.length() > 0) reasonBuilder.setLength(reasonBuilder.length() - 1);
					reason = reasonBuilder.toString();
				} else {
					reason = "No reason specified.";
				}
				
				Optional<Member> memberOpt = Reference.memberFromString(guild, args[0]);
				
				if(memberOpt.isPresent()) {
					if(!memberOpt.get().equals(member) && member.canInteract(memberOpt.get())) {
						if(!memberOpt.get().getVoiceState().isGuildDeafened()) {
							memberOpt.get().getUser().openPrivateChannel().queue(pm -> {
								pm.sendMessage("You have been deafened in the server **" + guild.getName() + "** for the reason: `" + reason + "`").queue(m -> {
									memberOpt.get().deafen(true).queue();
								}, e -> {
									memberOpt.get().deafen(true).queue();
								});
							}, e -> {
								memberOpt.get().deafen(true).queue();
							});
							
							channel.sendMessage("Successfully deafened " + memberOpt.get().getUser().getAsMention() + ". `" + reason + "`").queue();
						} else {
							channel.sendMessage("**This person is already deafened.**").queue();
						}
					} else {
						channel.sendMessage("**You cannot deafen this person.**").queue();
					}
				} else {
					channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot deafen members.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

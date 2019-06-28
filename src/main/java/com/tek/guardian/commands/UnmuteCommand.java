package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.enums.BotRole;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class UnmuteCommand extends Command {

	public UnmuteCommand() {
		super("unmute", Arrays.asList(), "<user>", "Unmutes a member.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 1) {
			if(member.hasPermission(Permission.VOICE_MUTE_OTHERS)) {
				Optional<Member> memberOpt = Reference.memberFromString(guild, args[0]);
				
				if(memberOpt.isPresent()) {
					if(!memberOpt.get().equals(member) && member.canInteract(memberOpt.get())) {
						Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
						
						if(memberOpt.get().getRoles().contains(r)) {
							Guardian.getInstance().getActionManager().unmute(member, memberOpt.get(), profile, channel);
						} else {
							channel.sendMessage(Reference.embedError(jda, "This person is not muted.")).queue();
						}
					} else {
						channel.sendMessage(Reference.embedError(jda, "You cannot unmute this person.")).queue();
					}
				} else {
					channel.sendMessage(Reference.embedError(jda, "No member was found by the identifier `" + args[0] + "`.")).queue();
				}
			} else {
				channel.sendMessage(Reference.embedError(jda, "You cannot unmute members.")).queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

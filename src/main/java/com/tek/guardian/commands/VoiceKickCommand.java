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

public class VoiceKickCommand extends Command {

	public VoiceKickCommand() {
		super("voicekick", Arrays.asList("vkick"), "<user>", "Kicks a user from his current voice channel.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 1) {
			if(member.hasPermission(Permission.VOICE_MOVE_OTHERS)) {
				Optional<Member> memberOpt = Reference.memberFromString(guild, args[0]);
				
				if(memberOpt.isPresent()) {
					if(!member.equals(memberOpt.get()) && member.canInteract(memberOpt.get())) {
						if(memberOpt.get().getVoiceState().inVoiceChannel()) {
							Guardian.getInstance().getActionManager().voiceKick(member, memberOpt.get(), channel, profile);
						} else {
							channel.sendMessage(Reference.embedError(jda, "This member is not in a voice channel.")).queue();
						}
					} else {
						channel.sendMessage(Reference.embedError(jda, "You cannot kick this member from voice channels.")).queue();
					}
				} else {
					channel.sendMessage(Reference.embedError(jda, "Couldn't find a member from the identifier `" + args[0] + "`.")).queue();
				}
			} else {
				channel.sendMessage(Reference.embedError(jda, "You cannot kick other users from voice channels.")).queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

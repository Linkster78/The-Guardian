package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class MentionCommand extends Command {

	public MentionCommand() {
		super("mention", Arrays.asList(), "<role>", "Mentions a role.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length > 0) {
			if(member.hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
				String roleName;
				StringBuilder nameBuilder = new StringBuilder();
				for(int i = 0; i < args.length; i++) nameBuilder.append(args[i] + " ");
				if(nameBuilder.length() > 0) nameBuilder.setLength(nameBuilder.length() - 1);
				roleName = nameBuilder.toString();
				
				Optional<Role> roleOpt = Reference.roleFromString(guild, roleName);
				
				if(roleOpt.isPresent()) {
					boolean isMentionable = roleOpt.get().isMentionable();
					if(!isMentionable) roleOpt.get().getManager().setMentionable(true).queue(r -> {
						channel.sendMessage(roleOpt.get().getAsMention()).queue(message -> {
							if(!isMentionable) roleOpt.get().getManager().setMentionable(false).queue();
						});
					});
				} else {
					channel.sendMessage(Reference.embedError(jda, "No role was found by the identifier `" + roleName + "`.")).queue();
				}
			} else {
				channel.sendMessage(Reference.embedError(jda, "You cannot mention this role.")).queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

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

public class WarnCommand extends Command {

	public WarnCommand() {
		super("warn", Arrays.asList(), "<user> <warning>", "Gives and logs a warning to a user.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 2) {
			if(member.hasPermission(Permission.MESSAGE_MANAGE)) {
				StringBuilder warningBuilder = new StringBuilder();
				for(int i = 1; i < args.length; i++) warningBuilder.append(args[i] + " ");
				if(warningBuilder.length() > 0) warningBuilder.setLength(warningBuilder.length() - 1);
				String warningText = warningBuilder.toString();
				
				Optional<Member> memberOpt = Reference.memberFromString(guild, args[0]);
				
				if(memberOpt.isPresent()) {
					if(member.canInteract(memberOpt.get()) && !member.equals(memberOpt.get())) {
						Guardian.getInstance().getActionManager().warn(member, memberOpt.get(), channel, warningText, profile);
					} else {
						channel.sendMessage("**You cannot warn this person.**").queue();
					}
				} else {
					channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot warn members.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

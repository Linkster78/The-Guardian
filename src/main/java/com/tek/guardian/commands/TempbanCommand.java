package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.data.TemporaryAction;
import com.tek.guardian.enums.Action;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class TempbanCommand extends Command {

	public TempbanCommand() {
		super("tempban", Arrays.asList(), "<user> [reason]", "Temporarily bans a member and specifies a reason.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 2) {
			if(member.hasPermission(Permission.BAN_MEMBERS)) {
				String reason;
				if(args.length >= 3) {
					StringBuilder reasonBuilder = new StringBuilder();
					for(int i = 2; i < args.length; i++) reasonBuilder.append(args[i] + " ");
					if(reasonBuilder.length() > 0) reasonBuilder.setLength(reasonBuilder.length() - 1);
					reason = reasonBuilder.toString();
				} else {
					reason = "No reason specified.";
				}
				
				Optional<Member> memberOpt = Reference.memberFromString(guild, args[0]);
				
				if(memberOpt.isPresent()) {
					try {
						long time = Reference.timeToMillis(args[1]);
						
						if(!memberOpt.get().equals(member) && member.canInteract(memberOpt.get())) {
							memberOpt.get().getUser().openPrivateChannel().queue(pm -> {
								pm.sendMessage("You have been temporarily banned in the server **" + guild.getName() + "** for **" + Reference.formatTime(time) + "** for the reason: `" + reason + "`").queue(m -> {
									guild.ban(memberOpt.get(), 0, reason).queue();
								}, e -> {
									guild.ban(memberOpt.get(), 0, reason).queue();
								});
							}, e -> {
								guild.ban(memberOpt.get(), 0, reason).queue();
							});
								
							TemporaryAction action = new TemporaryAction(memberOpt.get().getId(), guild.getId(), Action.TEMPBAN, System.currentTimeMillis(), time);
							Guardian.getInstance().getMongoAdapter().createTemporaryAction(action);
								
							channel.sendMessage("Temporarily banned " + memberOpt.get().getUser().getAsMention() + " for " + Reference.formatTime(time) + ". `" + reason + "`").queue();
						} else {
							channel.sendMessage("**You cannot ban this person.**").queue();
						}
					} catch(IllegalArgumentException e) {
						channel.sendMessage("**Invalid time. Format:** `time[s/m/h/d] Ex: 5h = 5 hours`").queue();
					}
				} else {
					channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot ban members.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

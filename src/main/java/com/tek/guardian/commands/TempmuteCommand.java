package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.data.TemporaryAction;
import com.tek.guardian.enums.Action;
import com.tek.guardian.enums.BotRole;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class TempmuteCommand extends Command {

	public TempmuteCommand() {
		super("tempmute", Arrays.asList(), "<user> <time> [reason]", "Mutes a member temporarily.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 2) {
			if(member.hasPermission(Permission.VOICE_MUTE_OTHERS)) {
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
							Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
							
							if(!memberOpt.get().getRoles().contains(r)) {
								memberOpt.get().getUser().openPrivateChannel().queue(pm -> {
									pm.sendMessage("You have been temporarily muted in the server **" + guild.getName() + "** for **" + Reference.formatTime(time) + "** for the reason: `" + reason + "`").queue(m -> {
										guild.addRoleToMember(memberOpt.get(), r).queue();
									}, e -> {
										guild.addRoleToMember(memberOpt.get(), r).queue();
									});
								}, e -> {
									guild.addRoleToMember(memberOpt.get(), r).queue();
								});
								
								TemporaryAction action = new TemporaryAction(memberOpt.get().getId(), guild.getId(), Action.TEMPMUTE, System.currentTimeMillis(), time);
								Guardian.getInstance().getMongoAdapter().createTemporaryAction(action);
								
								channel.sendMessage("Temporarily muted " + memberOpt.get().getUser().getAsMention() + " for " + Reference.formatTime(time) + ". `" + reason + "`").queue();
							} else {
								channel.sendMessage("**This person is already muted.**").queue();
							}
						} else {
							channel.sendMessage("**You cannot mute this person.**").queue();
						}
					} catch(IllegalArgumentException e) {
						channel.sendMessage("**Invalid time. Format:** `time[s/m/h/d] Ex: 5h = 5 hours`").queue();
					}
				} else {
					channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot mute members.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

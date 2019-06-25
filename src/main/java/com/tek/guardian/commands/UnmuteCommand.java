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
							memberOpt.get().getUser().openPrivateChannel().queue(pm -> {
								pm.sendMessage("You have been unmuted in the server **" + guild.getName() + "**.").queue(m -> {
									guild.removeRoleFromMember(memberOpt.get(), r).queue();
								}, e -> {
									guild.removeRoleFromMember(memberOpt.get(), r).queue();
								});
							}, e -> {
								guild.removeRoleFromMember(memberOpt.get(), r).queue();
							});
							
							for(TemporaryAction action : Guardian.getInstance().getMongoAdapter().getTemporaryActions()) {
								if(action.getUserId().equals(memberOpt.get().getId())) {
									if(action.getAction().equals(Action.TEMPMUTE)) {
										Guardian.getInstance().getMongoAdapter().removeTemporaryAction(action);
									}
								}
							}
							
							channel.sendMessage("Successfully unmuted " + memberOpt.get().getUser().getAsMention() + ".").queue();
						} else {
							channel.sendMessage("**This person is not muted.**").queue();
						}
					} else {
						channel.sendMessage("**You cannot unmute this person.**").queue();
					}
				} else {
					channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot unmute members.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

package com.tek.guardian.commands;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jdautilities.menu.Paginator;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.enums.BotRole;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class WhoisCommand extends Command {

	private final ButtonMenu.Builder menuBuilder;
	
	public WhoisCommand(EventWaiter waiter) {
		super("whois", Arrays.asList("see"), "<user>", "Displays information and actions about a member.", true);
		
		menuBuilder = new ButtonMenu.Builder()
				.setEventWaiter(waiter)
				.setFinalAction(message -> {
					message.clearReactions().queue(m -> {}, e -> {
						message.delete().queue();
					});
				})
				.setTimeout(1, TimeUnit.MINUTES);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 1) {
			Optional<Member> memberOpt = Reference.memberFromString(guild, args[0]);
			if(memberOpt.isPresent()) {
				Member who = memberOpt.get();
				
				menuBuilder.addChoice(Paginator.STOP).setAction(emote -> {
					String reason = "No reason specified.";
					
					if(guild.getMemberById(who.getId()) != null) {
						if(emote.getEmoji().equals(Reference.BOOT)) {
							Guardian.getInstance().getActionManager().kick(member, memberOpt.get(), channel, reason);
						} else if(emote.getEmoji().equals(Reference.HAMMER)) {
							Guardian.getInstance().getActionManager().ban(member, memberOpt.get(), channel, reason);
						} else if(emote.getEmoji().equals(Reference.SILENCE)) {
							Role r = guild.getRoleById(profile.getRoleMap().get(BotRole.MUTED.name()));
							
							if(!memberOpt.get().getRoles().contains(r)) {
								Guardian.getInstance().getActionManager().mute(member, memberOpt.get(), profile, channel, reason);
							}
						} else if(emote.getEmoji().equals(Reference.DEAF)) {
							if(!memberOpt.get().getVoiceState().isGuildDeafened()) {
								Guardian.getInstance().getActionManager().deafen(member, memberOpt.get(), channel, reason);
							}
						}
					}
				});
				
				if(member.canInteract(memberOpt.get()) && member.hasPermission(Permission.KICK_MEMBERS)) menuBuilder.addChoice(Reference.BOOT);
				if(member.canInteract(memberOpt.get()) && member.hasPermission(Permission.BAN_MEMBERS)) menuBuilder.addChoice(Reference.HAMMER);
				if(member.canInteract(memberOpt.get()) && member.hasPermission(Permission.VOICE_MUTE_OTHERS)) menuBuilder.addChoice(Reference.SILENCE);
				if(member.canInteract(memberOpt.get()) && member.hasPermission(Permission.VOICE_DEAF_OTHERS)) menuBuilder.addChoice(Reference.DEAF);
				
				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				
				Role highestRole = null;
				for(Role role : who.getRoles()) {
					if(highestRole == null) {
						highestRole = role;
						continue;
					}
					if(role.getPosition() > highestRole.getPosition()) highestRole = role;
				}
				
				final Role highestRoleFinal = highestRole;
				
				menuBuilder.setRenderer(builder -> {
					MessageEmbed embed = Reference.formatEmbed(jda, "User Information")
							.setColor(highestRoleFinal == null ? Color.blue : highestRoleFinal.getColor())
							.setThumbnail(who.getUser().getEffectiveAvatarUrl())
							.addField("Name", who.getUser().getName() + "#" + who.getUser().getDiscriminator(), true)
							.addField("Nickname", who.getNickname() == null ? "None" : who.getNickname(), true)
							.addField("User ID", who.getId(), true)
							.addField("Highest Role", highestRoleFinal == null ? "None" : highestRoleFinal.getName(), true)
							.addField("Join Time", who.getTimeJoined().format(timeFormatter), true)
							.addField("Account Creation Time", who.getUser().getTimeCreated().format(timeFormatter), true)
							.build();
					builder.setEmbed(embed);
				});
				
				menuBuilder.build().display(channel);
			} else {
				channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

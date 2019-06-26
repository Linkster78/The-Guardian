package com.tek.guardian.events;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AccountFlaggingListener extends ListenerAdapter {
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		OffsetDateTime accountCreation = event.getUser().getTimeCreated();
		OffsetDateTime timeDelta = event.getMember().getTimeJoined().minus(accountCreation.toEpochSecond(), ChronoUnit.SECONDS);
		long secondDelta = timeDelta.toEpochSecond();
		
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(event.getGuild());
		
		if(secondDelta < TimeUnit.DAYS.toSeconds(5)) {
			if(profile.getFlagChannel() != null) {
				TextChannel channel = event.getGuild().getTextChannelById(profile.getFlagChannel());
				if(channel != null) {
					MessageEmbed flagEmbed = Reference.formatEmbed(event.getJDA(), "Account Flagged")
							.setColor(Color.red)
							.setDescription("An account was flagged due to its small time difference between account creation and server joining.")
							.setThumbnail(event.getUser().getEffectiveAvatarUrl())
							.addField("Name", event.getUser().getName() + "#" + event.getUser().getDiscriminator(), true)
							.addField("User ID", event.getUser().getId(), true)
							.addField("Flagging Cause", "Account Creation", true)
							.addField("Account Creation Delta", Reference.formatTime(secondDelta * 1000) + " ago", true)
							.build();
					
					channel.sendMessage(flagEmbed).queue();
				} else {
					profile.setFlagChannel(null);
					profile.save();
				}
			}
		}
	}
	
}

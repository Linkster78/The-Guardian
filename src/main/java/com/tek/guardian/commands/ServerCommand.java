package com.tek.guardian.commands;

import java.awt.Color;
import java.util.Arrays;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class ServerCommand extends Command {

	public ServerCommand() {
		super("server", Arrays.asList("serverinfo"), null, "Displays some handy dandy information on the server.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		MessageEmbed embed = Reference.formatEmbed(jda, "Server Information")
				.setColor(Color.green)
				.setThumbnail(guild.getIconUrl())
				.addField("Name", guild.getName(), true)
				.addField("Description", guild.getDescription() == null ? "None" : guild.getDescription(), true)
				.addField("Server ID", guild.getId(), true)
				.addField("Owner", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), true)
				.addField("Boost Count", Integer.toString(guild.getBoostCount()), true)
				.addField("Member Count", Integer.toString(guild.getMembers().size()), true)
				.addField("User Count", Long.toString(guild.getMembers().stream().filter(m -> !m.getUser().isBot()).count()), true)
				.addField("Bot Count", Long.toString(guild.getMembers().stream().filter(m -> m.getUser().isBot()).count()), true)
				.addField("Category Count", Integer.toString(guild.getCategories().size()), true)
				.addField("Text Channel Count", Integer.toString(guild.getTextChannels().size()), true)
				.addField("Voice Channel Count", Integer.toString(guild.getVoiceChannels().size()), true)
				.addField("Emote Count", Integer.toString(guild.getEmotes().size()), true)
				.build();
		
		channel.sendMessage(embed).queue();
		
		return true;
	}

}

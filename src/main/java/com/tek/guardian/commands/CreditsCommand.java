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

public class CreditsCommand extends Command {

	public CreditsCommand() {
		super("credits", Arrays.asList("info", "desc", "description"), null, "Displays the bot credits and information.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 0) {
			MessageEmbed embed = Reference.formatEmbed(jda, "Credits")
					.setColor(Color.green)
					.setDescription("Some credits and information about the bot.")
					.addField("Huge thanks to", "[Austin Keener](https://github.com/DV8FromTheWorld) - Developer of JDA\n[John Grosh](https://github.com/jagrosh) - Developer of JDA Utilities\nDiscord - Hosting this cool event /o/\nFriends - Epic ideas and support", true)
					.addField("Discord Library", "[JDA](https://github.com/DV8FromTheWorld/JDA) - Java", true)
					.addField("Developer", "RedstoneTek / Toon Link#8313", true)
					.addField("Additional Information", "This bot was made in 5 days within the **Discord Hack Week**.", true)
					.build();
			channel.sendMessage(embed).queue();
			return true;
		} else {
			return false;
		}
	}

}

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

public class SuggestCommand extends Command {

	public SuggestCommand() {
		super("suggest", Arrays.asList(), "<suggestion>", "Suggest something to the server administration.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length > 0) {
			String suggestion = label.substring(profile.getPrefix().length() + getName().length() + 1);
			
			if(profile.getSuggestionChannel() != null) {
				TextChannel suggestions = guild.getTextChannelById(profile.getSuggestionChannel());
				
				if(suggestions != null) {
					MessageEmbed embed = Reference.formatEmbed(jda, "New Suggestion")
							.setColor(Color.green)
							.setThumbnail(member.getUser().getEffectiveAvatarUrl())
							.addField("Suggestion", suggestion, true)
							.addField("Author", member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true)
							.build();
					
					suggestions.sendMessage(embed).queue(m -> {
						channel.sendMessage("Your suggestion has been submitted!").queue();
					});
				} else {
					profile.setSuggestionChannel(null);
					profile.save();
					channel.sendMessage("**The configured suggestions channel is invalid.**").queue();
				}
			} else {
				channel.sendMessage("**There is no suggestions channel configured. Contact the server administrators.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

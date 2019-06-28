package com.tek.guardian.commands;

import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.CustomVoiceChannel;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class CustomVoiceChannelCommand extends Command {

	public CustomVoiceChannelCommand() {
		super("customvc", Arrays.asList("customvoicechannel", "cvc"), "<capacity>", "Creates a private voice channel.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 1) {
			if(profile.getVoiceChannelCategory() != null) {
				Category category = guild.getCategoryById(profile.getVoiceChannelCategory());
				
				if(category != null) {
					Optional<CustomVoiceChannel> vcOpt = Guardian.getInstance().getMongoAdapter().getCustomVoiceChannel(guild.getId(), member.getId());
					
					if(!vcOpt.isPresent()) {
						if(Reference.isInteger(args[0])) {
							int capacity = Integer.parseInt(args[0]);
							CustomVoiceChannel customvc = new CustomVoiceChannel(category, member);
							customvc.create(category, member, capacity, () -> {
								Guardian.getInstance().getMongoAdapter().saveCustomVoiceChannel(customvc);
								channel.sendMessage(Reference.embedSuccess(jda, "Created a custom voice channel for you!\n Here's the Video Chat URL: https://discordapp.com/channels/" + guild.getId() + "/" + customvc.getChannelId())).queue();
							});
						} else {
							channel.sendMessage(Reference.embedError(jda, "Invalid Amount `" + args[0] + "`.")).queue();
						}
					} else {
						channel.sendMessage(Reference.embedError(jda, "You already own a custom voice channel.")).queue();
					}
				} else {
					profile.setVoiceChannelCategory(guild, null);
					profile.save();
					channel.sendMessage(Reference.embedError(jda, "The configured voice channel category is invalid.")).queue();
				}
			} else {
				channel.sendMessage(Reference.embedError(jda, "There is no voice channel category configured. Contact the server administrators.")).queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

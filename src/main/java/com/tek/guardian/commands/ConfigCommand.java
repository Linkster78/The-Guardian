package com.tek.guardian.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class ConfigCommand extends Command {

	public ConfigCommand() {
		super("config", Arrays.asList(), "<list/key> <value>", "Configures the server to your needs", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("list")) {
				MessageEmbed embed = Reference.formatEmbed(jda, "Configuration Keys")
						.setColor(Color.orange)
						.setDescription("A list of all configuration keys of the server.")
						.addField("Prefix", "**Description:** The command prefix\n**Key:** `prefix`\n**Value:** A string of 3 characters or less.", true)
						.addField("Delete Commands", "**Description:** Does the bot delete commands after execution?\n**Key:** `delcmd`\n**Value:** yes/true/on no/false/off", true)
						.addField("Reply Unknown", "**Description:** Does the bot inform the user when the command doesn't exist?\n**Key:** `replyunknown`\n**Value:** yes/true/on no/false/off", true)
						.addField("Command Channels", "**Description:** Which channels can users use commands in?\n**Key:** `cmdchannels`\n**Value:** A list of channels, separated by spaces.", true)
						.build();
				
				channel.sendMessage(embed).queue();
				
				return true;
			} else {
				return false;
			}
		} else if(args.length >= 2) {
			if(member.hasPermission(Permission.MANAGE_SERVER)) {
				String key = args[0];
				StringBuilder reasonBuilder = new StringBuilder();
				for(int i = 1; i < args.length; i++) reasonBuilder.append(args[i] + " ");
				if(reasonBuilder.length() > 0) reasonBuilder.setLength(reasonBuilder.length() - 1);
				String value = reasonBuilder.toString();
				
				if(key.equalsIgnoreCase("prefix")) {
					if(!value.contains(" ")) {
						if(value.length() <= 3) {
							profile.setPrefix(value);
							profile.save();
							channel.sendMessage("**Success!** New server prefix is `" + value + "`.").queue();
						} else {
							channel.sendMessage("**The prefix must be 3 characters or less.**").queue();
						}
					} else {
						channel.sendMessage("**The prefix must not contain spaces.**").queue();
					}
				} 
				
				else if(key.equalsIgnoreCase("delcmd") || key.equalsIgnoreCase("deletecommands")) {
					if(value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on")) {
						profile.setDeleteCommands(true);
						profile.save();
						channel.sendMessage("**Success!** The bot will now delete commands after execution.").queue();
					} else if(value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("off")) {
						profile.setDeleteCommands(false);
						profile.save();
						channel.sendMessage("**Success!** The bot will now leave commands after execution.").queue();
					} else {
						channel.sendMessage("**Invalid Value:** `yes/true/on no/false/off`").queue();
					}
				}
				
				else if(key.equalsIgnoreCase("replyunknown") || key.equalsIgnoreCase("replyerror")) {
					if(value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on")) {
						profile.setReplyUnknown(true);
						profile.save();
						channel.sendMessage("**Success!** The bot will now tell the user when the command doesn't exist.").queue();
					} else if(value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("off")) {
						profile.setReplyUnknown(false);
						profile.save();
						channel.sendMessage("**Success!** The bot will now ignore the user when the command doesn't exist.").queue();
					} else {
						channel.sendMessage("**Invalid Value:** `yes/true/on no/false/off`").queue();
					}
				}
				
				else if(key.equalsIgnoreCase("commandchannels") || key.equalsIgnoreCase("cmdchannels") || key.equalsIgnoreCase("channels")) {
					List<String> channels = new ArrayList<String>(args.length - 1);
					
					for(String token : value.split(" ")) {
						Optional<TextChannel> channelOpt = Reference.textChannelFromString(guild, token);
						if(channelOpt.isPresent()) channels.add(channelOpt.get().getId());
					}
					
					if(channels.size() == args.length - 1) {
						profile.getCommandChannels().clear();
						profile.getCommandChannels().addAll(channels);
						profile.save();
						channel.sendMessage("**Success!** The command channels are now " + channels.stream()
									.map(guild::getTextChannelById)
									.map(TextChannel::getAsMention)
									.collect(Collectors.joining(", ")) + ".").queue();
					} else {
						channel.sendMessage("**Invalid Value:** The value must be a list of text channels. `Ex: channel1 channel2`.").queue();
					}
				}
				
				else {
					channel.sendMessage("**Invalid Key. Accepted keys:** `prefix`.").queue();
				}
			} else {
				channel.sendMessage("**You cannot edit the server configuration.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean canCallAnywhere() {
		return true;
	}

}

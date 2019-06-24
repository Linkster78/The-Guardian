package com.tek.guardian.commands;

import java.util.ArrayList;
import java.util.List;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
	
	private List<Command> commands;
	
	public CommandHandler() {
		commands = new ArrayList<Command>();
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Member member = event.getMember();
		Guild guild = event.getGuild();
		TextChannel channel = event.getChannel();
		String label = event.getMessage().getContentRaw();
		String[] tokens = label.split(" ");
		String[] args = new String[tokens.length - 1];
		for(int i = 1; i < tokens.length; i++) args[i - 1] = tokens[i];
	
		ServerProfile profile = Guardian.getInstance().getMongoAdapter().getServerProfile(guild);
		if(label.startsWith(profile.getPrefix())) {
			if(profile.canSendCommand(channel.getId())) {
				if(tokens[0].length() != profile.getPrefix().length()) {
					String command = tokens[0].substring(profile.getPrefix().length());
					for(Command cmd : commands) {
						if(cmd.getName().equalsIgnoreCase(command)) {
							cmd.call(member, guild, channel, label, args);
							return;
						}
					}
					
					if(profile.doesDeleteCommands()) {
						event.getMessage().delete().queue();
					}
					
					if(profile.doesReplyUnknown()) {
						channel.sendMessage("**The command** `" + command + "` **does not exist.**").queue();
					}
				} else {
					if(profile.doesDeleteCommands()) {
						event.getMessage().delete().queue();
					}
				}
			} else {
				if(profile.doesDeleteCommands()) {
					event.getMessage().delete().queue();
				}
			}
		}
	}
	
	public List<Command> getCommands() {
		return commands;
	}
	
}

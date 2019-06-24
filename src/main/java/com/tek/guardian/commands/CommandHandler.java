package com.tek.guardian.commands;

import java.util.ArrayList;
import java.util.List;

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
		for(int i = 1; i < tokens.length; i++) args[i] = tokens[i];
	}
	
	public List<Command> getCommands() {
		return commands;
	}
	
}

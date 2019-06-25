package com.tek.guardian.commands;

import java.awt.Color;
import java.util.Arrays;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help", Arrays.asList("?"), null, "Displays the implemented bot commands.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 0) {
			StringBuilder helpBuilder = new StringBuilder();
			for(Command command : Guardian.getInstance().getCommandHandler().getCommands()) {
				if(command.isDisplayed()) {
					helpBuilder.append("`" + command.getFormattedSyntax() + "` **-** *" + command.getDescription() + "*\n");
				}
			}
			
			if(helpBuilder.length() > 0) helpBuilder.setLength(helpBuilder.length() - 1);
			
			MessageEmbed embed = Reference.formatEmbed(jda, "Help Menu")
					.setColor(Color.cyan)
					.setDescription("<> = Required Parameter, [] = Optional Parameter")
					.addField("Command List", helpBuilder.toString(), false)
					.build();
			
			channel.sendMessage(embed).queue();
			return true;
		} else {
			return false;
		}
	}

}

package com.tek.guardian.commands;

import java.util.Arrays;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class PingCommand extends Command {

	public PingCommand() {
		super("ping", Arrays.asList(), null, "Checks the ping the latency between the bot and the REST Api.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 0) {
			channel.sendMessage(Reference.embedSuccess(jda, "The bot's ping is **" + jda.getRestPing().complete() + "**ms")).queue();
			return true;
		} else {
			return false;
		}
	}

}

package com.tek.guardian.commands;

import java.util.Arrays;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class SlowmodeCommand extends Command {

	public SlowmodeCommand() {
		super("slowmode", Arrays.asList("slow"), "<time/off>", "Sets the slowmode of the current channel.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 1) {
			if(member.hasPermission(Permission.MANAGE_CHANNEL)) {
				int time;
				if(args[0].equalsIgnoreCase("off")) {
					time = 0;
				} else {
					try {
						time = Reference.timeToMillis(args[0]) / 1000;
					} catch(IllegalArgumentException e) {
						channel.sendMessage("**Invalid time. Format:** `time[s/m/h] Ex: 15s = 15 seconds`").queue();
						return true;
					}
				}
				
				if(time >= 0 && time <= 21600) {
					channel.getManager().setSlowmode(time).queue();
					if(time == 0) {
						channel.sendMessage("Disabled the channel slowmode.").queue();
					} else {
						channel.sendMessage("Set the channel slowmode to every " + Reference.formatTime(time * 1000) + ".").queue();
					}
				} else {
					channel.sendMessage("**Slow mode time out of range: 0-" + 21600 + "**").queue();
				}
			} else {
				channel.sendMessage("**You cannot set the channel slowmode.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

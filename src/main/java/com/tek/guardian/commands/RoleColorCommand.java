package com.tek.guardian.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.Optional;

import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class RoleColorCommand extends Command {

	public RoleColorCommand() {
		super("rolecolor",  Arrays.asList("rcolor"), "<role> <#color>", "Sets the hex color of a role.", true);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 2) {
			if(member.hasPermission(Permission.MANAGE_ROLES)) {
				Optional<Role> roleOpt = Reference.roleFromString(guild, args[0]);
				if(roleOpt.isPresent()) {
					try {
						int color = parseColor(args[1]);
						Color c = new Color(color);
						roleOpt.get().getManager().setColor(c).queue();
						
						channel.sendMessage(Reference.formatEmbed(jda, "Success")
								.setColor(c)
								.setDescription("Set the color to **" + String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()) + "**.").build()).queue();
					} catch(IllegalArgumentException e) {
						channel.sendMessage(Reference.embedError(jda, "Invalid Color. Format: `#RRGGBB`.")).queue();
					}
				} else {
					channel.sendMessage(Reference.embedError(jda, "No role was found by the identifier `" + args[0] + "`.")).queue();
				}
			} else {
				channel.sendMessage(Reference.embedError(jda, "You cannot edit roles.")).queue();
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public int parseColor(String colorString) {
	    if (colorString.charAt(0) == '#') {
	        long color = Long.parseLong(colorString.substring(1), 16);
	        if (colorString.length() == 7) {
	            color |= 0x00000000ff000000;
	        } else if (colorString.length() != 9) {
	            throw new IllegalArgumentException("Unknown color");
	        }
	        return (int)color;
	    }
	    throw new IllegalArgumentException("Unknown color");
	}

}

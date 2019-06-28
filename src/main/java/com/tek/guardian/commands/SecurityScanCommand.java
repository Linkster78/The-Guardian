package com.tek.guardian.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class SecurityScanCommand extends Command {

	private final int ROLE_THRESHOLD = 25;
	private final int PER_PAGE = 8;
	private final Paginator.Builder paginatorBuilder;
	
	public SecurityScanCommand(EventWaiter waiter) {
		super("securityscan", Arrays.asList("sscan"), null, "Scans the server for any potential permission issues.", true);
	
		paginatorBuilder = new Paginator.Builder().setColumns(1)
				.setItemsPerPage(PER_PAGE)
				.showPageNumbers(false)
				.waitOnSinglePage(false)
				.useNumberedItems(false)
				.setFinalAction(message -> {
					try {
						message.clearReactions().queue(m -> {}, e -> {
							message.delete().queue();
						});
					} catch(Exception e) { }
				})
				.setEventWaiter(waiter)
				.setTimeout(5, TimeUnit.MINUTES);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length != 0) return false;
		
		if(!member.hasPermission(Permission.MANAGE_SERVER)) {
			channel.sendMessage(Reference.embedError(jda, "You cannot perform security scans on the server.")).queue();
			return true;
		}
		
		List<String> vulnerabilities = new ArrayList<String>();
		List<String> warnings = new ArrayList<String>();
		
		for(Role role : guild.getRoles()) {
			if(!role.hasPermission(Permission.ADMINISTRATOR) && role.hasPermission(Permission.MANAGE_ROLES)) {
				for(Role other : guild.getRoles()) {
					if(other.getPosition() < role.getPosition()) {
						List<Permission> morePermissions = new ArrayList<Permission>();
						for(Permission permission : Permission.values()) {
							if(other.hasPermission(permission) && !role.hasPermission(permission)) {
								morePermissions.add(permission);
							}
						}
						if(!morePermissions.isEmpty()) {
							vulnerabilities.add("Role **" + role.getName() + "** can give itself the **" + other.getName() + "**"
									+ " role, which has more permissions: `" + morePermissions.stream()
									.map(Permission::getName).collect(Collectors.joining(", ")) + "`.");
						}
					}
				}
				
				List<Permission> channelPermissions = new ArrayList<Permission>();
				for(Permission permission : Permission.values()) {
					if(permission.isChannel()) {
						if(!role.hasPermission(permission)) channelPermissions.add(permission);
					}
				}
				if(!channelPermissions.isEmpty()) {
					warnings.add("Role **" + role.getName() + "**"
							+ " can edit roles/permissions and as such, can give itself missing permissions on a channel to channel basis: `"
							+ channelPermissions.stream().map(Permission::getName).collect(Collectors.joining(", ")) + "`.");
				}
			}
			
			if(guild.getMembersWithRoles(role).size() >= ROLE_THRESHOLD) {
				if(role.isMentionable()) {
					warnings.add("Role **" + role.getName() + "** has " + guild.getMembersWithRoles(role).size()
							+ " members and can be pinged by **anyone**.");
				}
			}
		}
		
		paginatorBuilder.clearItems();
		for(String vulnerability : vulnerabilities) {
			paginatorBuilder.addItems(Reference.DANGER + " " + vulnerability);
		}
		for(String warning : warnings) {
			paginatorBuilder.addItems(Reference.WARNING + " " + warning);
		}
		
		if(vulnerabilities.size() == 0 && warnings.size() == 0) paginatorBuilder.addItems(Reference.GOOD + " All good!");
		Color color = vulnerabilities.isEmpty() ? warnings.isEmpty() ? Color.green : Color.yellow : Color.red;
		
		Paginator paginator = paginatorBuilder
				.setColor(color)
				.setText((current, total) -> "Scan Results (" + current + "/" + total + ")")
				.setTitle("Warnings/Vulnerabilities")
				.setUsers(member.getUser())
				.build();

		paginator.paginate(channel, 1);
		
		return true;
	}

}

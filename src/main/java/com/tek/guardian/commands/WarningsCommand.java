package com.tek.guardian.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.data.UserProfile;
import com.tek.guardian.data.UserProfile.Warning;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class WarningsCommand extends Command {

	private final int PER_PAGE = 10;
	private final Paginator.Builder paginatorBuilder;
	
	public WarningsCommand(EventWaiter waiter) {
		super("warnings", Arrays.asList(), "<user>", "Provides a list of a user's warnings.", true);
		
		paginatorBuilder = new Paginator.Builder().setColumns(1)
				.setItemsPerPage(PER_PAGE)
				.showPageNumbers(false)
				.waitOnSinglePage(false)
				.useNumberedItems(true)
				.setFinalAction(message -> {
					try {
						message.clearReactions().queue(m -> {}, e -> {
							message.delete().queue();
						});
					} catch(Exception e) { }
				})
				.setEventWaiter(waiter)
				.setTimeout(1, TimeUnit.MINUTES);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 1) {
			if(member.hasPermission(Permission.MESSAGE_MANAGE)) {
				Optional<Member> memberOpt = Reference.memberFromString(guild, args[0]);
				
				if(memberOpt.isPresent()) {
					UserProfile userProfile = Guardian.getInstance().getMongoAdapter().getUserProfile(memberOpt.get());
					
					paginatorBuilder.clearItems();
					
					Map<String, Member> authors = new HashMap<String, Member>();
					
					for(Warning warning : userProfile.getWarnings()) {
						Member author;
						if(authors.containsKey(warning.getAuthorId())) {
							author = authors.get(warning.getAuthorId());
						} else {
							author = guild.getMemberById(warning.getAuthorId());
							authors.put(warning.getAuthorId(), author);
						}
						
						paginatorBuilder.addItems(Reference.WARNING + "From **" + (author == null ? "Unknown User" : author.getUser().getName() + "#" + author.getUser().getDiscriminator()) + "**: "
								+ "`" + warning.getWarning() + "`.");
					}
					
					if(userProfile.getWarnings().isEmpty()) {
						paginatorBuilder.addItems(Reference.GOOD + " No warnings found.");
					}
					
					Paginator paginator = paginatorBuilder
							.setColor(Color.yellow)
							.setText((current, total) -> "User Warnings (" + current + "/" + total + ")")
							.setTitle(memberOpt.get().getUser().getName() + "'s warnings.")
							.setDescription("Warnings given by moderators and staff.")
							.setUsers(member.getUser())
							.build();
					
					paginator.paginate(channel, 1);
				} else {
					channel.sendMessage("**No member was found by the identifier** `" + args[0] + "`").queue();
				}
			} else {
				channel.sendMessage("**You cannot view member warnings.**").queue();
			}
			
			return true;
		} else {
			return false;
		}
	}

}

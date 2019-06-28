package com.tek.guardian.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class HelpCommand extends Command {

	private final int PER_PAGE = 10;
	private final Paginator.Builder paginatorBuilder;
	
	public HelpCommand(EventWaiter waiter) {
		super("help", Arrays.asList("?"), "[page]", "Displays the implemented bot commands.", true);
		
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
				.setTimeout(1, TimeUnit.MINUTES);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length >= 0 && args.length <= 1) {
			int page = 1;
			
			if(args.length == 1) {
				if(Reference.isInteger(args[0])) {
					page = Integer.parseInt(args[0]);
				} else {
					channel.sendMessage(Reference.embedError(jda, "Invalid Amount `" + args[0] + "`.")).queue();
					return true;
				}
				
				if(!(page >= 1 && page <= getPageCount())) {
					channel.sendMessage(Reference.embedError(jda, "Page number out of range: 1-" + getPageCount() + ".")).queue();
					return true;
				}
			}
			
			paginatorBuilder.clearItems();
			
			for(Command command : Guardian.getInstance().getCommandHandler().getCommands()) {
				if(command.isDisplayed()) {
					paginatorBuilder.addItems("`" + profile.getPrefix() + command.getFormattedSyntax() + "` **-** *" + command.getDescription() + "*");
				}
			}
			
			Paginator paginator = paginatorBuilder
					.setColor(Color.cyan)
					.setText((current, total) -> "Help Menu (" + current + "/" + total + ")")
					.setTitle("Command List")
					.setDescription("<> = Required Parameter, [] = Optional Parameter")
					.setUsers(member.getUser())
					.build();
			
			paginator.paginate(channel, page);
			
			return true;
		} else {
			return false;
		}
	}
	
	public int getPageCount() {
		return (int) Math.ceil((double) Guardian.getInstance().getCommandHandler().getCommands().size() / (double) PER_PAGE);
	}

}

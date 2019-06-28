package com.tek.guardian.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuideCommand extends Command {

	private final List<String> GUIDES = Arrays.asList(
			"How to fetch a message's ID?;**1)** Open your user settings.\n**2)** Click in the \"Appearance\" tab.\n**3)** Scroll down and enable \"Developer Mode\"\n**4)** Go to a message, go to the side, as to delete it. An option should say \"Copy ID\".",
			"How to setup reaction roles?;*This guide assumes you have read the previous guide,* \"How to fetch a message's ID?\"\n**1)** Create a message in some public channel. *This message will be the one we apply the reaction role to.*\n**2)** Create a role to be assigned, this can be whatever you want.\n**3)** Use the command `reactionrole <channel> <Message ID>` with the channel the message was sent it, and the message ID. *See previous guide.*\n**4)** Send the name of the role you created. *This is the role that will be assigned.*\n**5)** Last but not least, react to the bot's message with the reaction you want to be used.",
			"How to setup user suggestions?;**1)** First of all, create a channel, uneditable by normal users. *Suggestions will be sent there.*\n**2)** Set the suggestions channel in your configuration with this command `config suggestionschannel <channel name>`.\n**3)** From this point on, if users want to send in a suggestion, they can simply use `suggest <suggestion>` and it will be sent directly in that channel as an embed message.",
			"How to setup logging?;*In this guide, we will see how to setup deleted message loggin as well as moderation action logging.*\n**1)** Create two channels, both uneditable by users and preferrably privated. Name them something along the lines of \"action-logs\" and \"deleted-logs\".\n**2)** Use the commands `config logchannel <channel>` and `config delchannel <channel>` with the names of both of the new channels.");
	private ButtonMenu.Builder menuBuilder;
	
	public GuideCommand(EventWaiter waiter) {
		super("guide", Arrays.asList("howto"), null, "Provides some guides on the bot's features.", true);
		
		menuBuilder = new ButtonMenu.Builder()
				.setEventWaiter(waiter)
				.setFinalAction(message -> {
					try {
						message.clearReactions().queue(m -> {}, e -> {
							message.delete().queue();
						});
					} catch(Exception e) { }
				})
				.setTimeout(1, TimeUnit.MINUTES);
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length == 0) {
			menuBuilder.reset();
			
			for(int i = 0; i < GUIDES.size(); i++) {
				menuBuilder.addChoice(PollCommand.EMOTES[i]);
			}
			
			menuBuilder.setUsers(member.getUser());
			menuBuilder.setAction((m, remote) -> {
				int i1 = 0;
				for(String guide : GUIDES) {
					String display = guide.split(";")[0];
					String guideText = guide.split(";")[1];
					if(remote.isEmoji() && remote.getEmoji().equals(PollCommand.EMOTES[i1])) {
						MessageEmbed embed = Reference.formatEmbed(jda, "Bot Guide")
								.setColor(Color.cyan)
								.setTitle(display)
								.setDescription(guideText)
								.build();
						m.editMessage(embed).queue();
					}
					i1++;
				}
			});
			
			menuBuilder.setRenderer(mb -> {
				StringBuilder guideBuilder = new StringBuilder();
				int i1 = 0;
				for(String guide : GUIDES) {
					String display = guide.split(";")[0];
					guideBuilder.append(PollCommand.EMOTES[i1] + " " + display + "\n");
					i1++;
				}
				
				guideBuilder.append("\n**Please note: There are more options configurable. You can see them with the command `config <list/show>` but they are quite straight forward to a guide for them is not needed.** *If you need any further help, please feel free to contact the bot developer,* `Toon Link#8313`*.*");
				
				MessageEmbed embed = Reference.formatEmbed(jda, "Bot Guide")
						.setColor(Color.blue)
						.setTitle("What would you like help with?")
						.setDescription(guideBuilder.toString())
						.build();
				mb.setEmbed(embed);
			});
			
			ButtonMenu menu = menuBuilder.build();
			menu.display(channel);
			
			return true;
		} else {
			return false;
		}
	}

}

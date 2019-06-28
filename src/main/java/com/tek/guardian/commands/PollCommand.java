package com.tek.guardian.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.tek.guardian.data.ServerProfile;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PollCommand extends Command {

	public static final String[] EMOTES = new String[] {"🇦","🇧","🇨","🇩","🇪","🇫","🇬","🇭","🇮","🇯","🇰","🇱","🇲","🇳","🇴","🇵","🇶","🇷","🇸","🇹","🇺","🇻","🇼","🇽","🇾","🇿"};
	private final EventWaiter waiter;
	
	public PollCommand(EventWaiter waiter) {
		super("poll", Arrays.asList(), "[channel]", "Creates a poll.", true);
		this.waiter = waiter;
	}

	@Override
	public boolean call(JDA jda, ServerProfile profile, Member member, Guild guild, TextChannel channel, String label, String[] args) {
		if(args.length > 1) return false;
		
		if(member.hasPermission(Permission.MESSAGE_MANAGE)) {
			TextChannel pollChannel = channel;
			if(args.length == 1) {
				Optional<TextChannel> channelOpt = Reference.textChannelFromString(guild, args[0]);
				if(channelOpt.isPresent()) {
					pollChannel = channelOpt.get();
				} else {
					channel.sendMessage("**The channel specified does not exist.**").queue();
					return true;
				}
			}
			final TextChannel pollChannelFinal = pollChannel;
			
			if(pollChannel.canTalk(member)) {
				channel.sendMessage("Poll Creation: What is the poll question?").queue(message1 -> {
					waitForMessage(waiter, channel, member, response -> {
						String question = response;
						
						final List<String> options = new ArrayList<String>();
						message1.editMessage("Poll Creation: Name a poll option. (Enter `done` if all the options are entered)").queue(message2 -> {
							waitForMessage(waiter, channel, member, getOptionCallback(waiter, pollChannelFinal, channel, message1, member, options, question), () -> {
								channel.sendMessage("**The poll creation timed out.**").queue();
							});
						});
					}, () -> {
						channel.sendMessage("**The poll creation timed out.**").queue();
					});
				});
			} else {
				channel.sendMessage("**The cannot create a poll in this channel.**").queue();
			}
		} else {
			channel.sendMessage("**The cannot create polls.**").queue();
		}
		
		return true;
	}
	
	public static void waitForMessage(EventWaiter waiter, TextChannel channel, Member member, Consumer<String> messageCallback, Runnable timeout) {
		waiter.waitForEvent(GuildMessageReceivedEvent.class, (GuildMessageReceivedEvent event) -> {
			return event.getChannel().getId().equals(channel.getId()) && member.getId().equals(event.getMember().getId());
		}, (GuildMessageReceivedEvent event) -> {
			Guardian.getInstance().getMessageCache().decache(event.getMessageId());
			event.getMessage().delete().queue();
			messageCallback.accept(event.getMessage().getContentRaw());
		}, 1, TimeUnit.MINUTES, () -> timeout.run());
	}
	
	public Consumer<String> getOptionCallback(EventWaiter waiter, TextChannel pollChannel, TextChannel channel, Message questionMessage, Member member, List<String> options, String question) {
		return option -> {
			if(option.equalsIgnoreCase("done")) {
				if(!options.isEmpty()) {
					if(options.size() <= 26) {
						questionMessage.editMessage("Created the poll in the " + pollChannel.getAsMention() + " channel.").queue();
						
						StringBuilder descriptionBuilder = new StringBuilder();
						int i = 0;
						for(String opt : options) {
							descriptionBuilder.append(EMOTES[i] + ": " + opt + "\n");
							i++;
						}
						
						MessageEmbed embed = Reference.formatEmbed(pollChannel.getJDA(), "Poll")
								.setColor(Color.blue)
								.setTitle(question)
								.setDescription(descriptionBuilder.toString())
								.build();
						
						pollChannel.sendMessage(embed).queue(m -> {
							for(int u = 0; u < options.size(); u++) {
								m.addReaction(EMOTES[u]).queue();
							}
						});
					} else {
						channel.sendMessage("**Poll creation cancelled. You can only have 26 options.**").queue();
					}
				} else {
					channel.sendMessage("**Poll creation cancelled. You need to provide at least one option.**").queue();
				}
			} else {
				options.add(option);
				waitForMessage(waiter, channel, member, getOptionCallback(waiter, pollChannel, channel, questionMessage, member, options, question), () -> {
					channel.sendMessage("**The poll creation timed out.**").queue();
				});
			}
		};
	}

}

package com.tek.guardian.main;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.tek.guardian.commands.BanCommand;
import com.tek.guardian.commands.ClearCommand;
import com.tek.guardian.commands.CommandHandler;
import com.tek.guardian.commands.DeafenCommand;
import com.tek.guardian.commands.HelpCommand;
import com.tek.guardian.commands.KickCommand;
import com.tek.guardian.commands.LockCommand;
import com.tek.guardian.commands.MuteCommand;
import com.tek.guardian.commands.TempbanCommand;
import com.tek.guardian.commands.TempdeafenCommand;
import com.tek.guardian.commands.TempmuteCommand;
import com.tek.guardian.commands.UndeafenCommand;
import com.tek.guardian.commands.UnlockCommand;
import com.tek.guardian.commands.UnmuteCommand;
import com.tek.guardian.config.Config;
import com.tek.guardian.data.MongoAdapter;
import com.tek.guardian.events.MessageReceivedListener;
import com.tek.guardian.events.ServerStatusListener;
import com.tek.guardian.timer.TaskTimer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Guardian {
	
	public static final Logger LOGGER = Logger.getLogger(Guardian.class);
	
	private static Guardian instance;
	
	private Config config;
	private MongoAdapter mongoAdapter;
	private JDA jda;
	private EventWaiter waiter;
	private CommandHandler commandHandler;
	private TaskTimer taskTimer;
	
	public Guardian(Config config) {
		this.config = config;
	}
	
	public void start() throws LoginException, InterruptedException {
		instance = this;
		
		mongoAdapter = new MongoAdapter();
		mongoAdapter.connect(Reference.DATABASE);
		
		jda = new JDABuilder()
				.setToken(config.getToken())
				.setActivity(Activity.playing(config.getPresence()))
				.build();
		jda.awaitReady();
		
		waiter = new EventWaiter();
		
		commandHandler = new CommandHandler();
		commandHandler.registerCommand(new HelpCommand(waiter));
		commandHandler.registerCommand(new KickCommand());
		commandHandler.registerCommand(new BanCommand());
		commandHandler.registerCommand(new TempbanCommand());
		commandHandler.registerCommand(new MuteCommand());
		commandHandler.registerCommand(new TempmuteCommand());
		commandHandler.registerCommand(new UnmuteCommand());
		commandHandler.registerCommand(new DeafenCommand());
		commandHandler.registerCommand(new TempdeafenCommand());
		commandHandler.registerCommand(new UndeafenCommand());
		commandHandler.registerCommand(new LockCommand());
		commandHandler.registerCommand(new UnlockCommand());
		commandHandler.registerCommand(new ClearCommand());
		jda.addEventListener(waiter, commandHandler, new ServerStatusListener(), new MessageReceivedListener());
		
		taskTimer = new TaskTimer();
		taskTimer.start();
		
		LOGGER.info("Launched Guardian successfully!");
	}
	
	public static Guardian getInstance() {
		return instance;
	}
	
	public Config getConfig() {
		return config;
	}
	
	public MongoAdapter getMongoAdapter() {
		return mongoAdapter;
	}
	
	public JDA getJDA() {
		return jda;
	}
	
	public EventWaiter getWaiter() {
		return waiter;
	}
	
	public CommandHandler getCommandHandler() {
		return commandHandler;
	}
	
	public TaskTimer getTaskTimer() {
		return taskTimer;
	}
	
}

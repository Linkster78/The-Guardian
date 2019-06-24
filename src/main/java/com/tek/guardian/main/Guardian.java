package com.tek.guardian.main;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;

import com.tek.guardian.commands.CommandHandler;
import com.tek.guardian.commands.HelpCommand;
import com.tek.guardian.commands.KickCommand;
import com.tek.guardian.config.Config;
import com.tek.guardian.data.MongoAdapter;
import com.tek.guardian.events.ServerStatusListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Guardian {
	
	public static final Logger LOGGER = Logger.getLogger(Guardian.class);
	
	private static Guardian instance;
	
	private Config config;
	private MongoAdapter mongoAdapter;
	private JDA jda;
	private CommandHandler commandHandler;
	
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
		
		commandHandler = new CommandHandler();
		commandHandler.registerCommand(new HelpCommand());
		commandHandler.registerCommand(new KickCommand());
		jda.addEventListener(commandHandler, new ServerStatusListener());
		
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
	
	public CommandHandler getCommandHandler() {
		return commandHandler;
	}
	
}

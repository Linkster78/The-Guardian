package com.tek.guardian;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.apache.log4j.BasicConfigurator;
import org.json.JSONException;

import com.tek.guardian.config.Config;
import com.tek.guardian.main.Guardian;
import com.tek.guardian.main.Reference;

public class Launcher {
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		
		try {
			Config config = Config.load(Reference.CONFIG_PATH);
			Guardian.LOGGER.info("Loaded configuration. Launching Guardian...");
			Guardian guardian = new Guardian(config);
			guardian.start();
		} catch(LoginException | InterruptedException e) {
			Guardian.LOGGER.error("[" + e.getClass().getSimpleName() + "]" + "Couldn't login using the provided token. Please verify its validity.");
		} catch(JSONException | IllegalArgumentException e) {
			Guardian.LOGGER.error("Error while parsing configuration JSON: " + e.getMessage());
		} catch(IOException e) {
			Guardian.LOGGER.error("Error while reading configuration: " + e.getMessage());
		}
	}
	
}

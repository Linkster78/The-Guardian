package com.tek.guardian.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class Config {
	
	private String token;
	private String defaultPrefix;
	private String presence;
	
	private Config(JSONObject json) throws IllegalArgumentException {
		if(!json.has("token")) throw new IllegalArgumentException("token field missing.");
		if(!json.has("prefix")) throw new IllegalArgumentException("prefix field missing.");
		if(!json.has("presence")) throw new IllegalArgumentException("presence field missing.");
	
		this.token = json.getString("token");
		this.defaultPrefix = json.getString("prefix");
		this.presence = json.getString("presence");
	}
	
	public String getToken() {
		return token;
	}
	
	public String getDefaultPrefix() {
		return defaultPrefix;
	}
	
	public String getPresence() {
		return presence;
	}
	
	public static Config load(String filePath) throws IllegalArgumentException, JSONException, IOException {
		File file = new File(filePath);
		if(file.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			StringBuilder read = new StringBuilder();
			while((line = br.readLine()) != null) {
				read.append(line);
			}
			br.close();
			String jsonString = read.toString();
			return new Config(new JSONObject(jsonString));
		} else {
			throw new FileNotFoundException(filePath + " was not found");
		}
	}
	
}

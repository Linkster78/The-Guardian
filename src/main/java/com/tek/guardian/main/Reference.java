package com.tek.guardian.main;

import java.util.regex.Pattern;

public class Reference {
	
	public static final Pattern TAG_REGEX = Pattern.compile("^(\\w|\\s)+#\\d{4}$");
	public static final Pattern SNOWFLAKE_REGEX = Pattern.compile("^\\d+$");
	
	public static final String CONFIG_PATH = "./config.json";
	public static final String DATABASE = "guardian";
	
}

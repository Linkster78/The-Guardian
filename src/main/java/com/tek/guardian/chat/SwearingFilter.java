package com.tek.guardian.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.tek.guardian.data.ServerProfile;

import net.dv8tion.jda.api.entities.User;

public class SwearingFilter implements ChatFilter {

	private final List<String> TOKENS_TO_OMIT = Arrays.asList(" ", "_", ".", ",", "!", "removeme", "remove", "^", ":", ";");
	private List<String> swears;
	
	public SwearingFilter() {
		String swearListString = new String(Base64.getDecoder().decode("YW5hbAphbnVzCmFyc2UKYXNzCmJhbGxzYWNrCmJhbGxzCmJhc3RhcmQKYml0Y2gKYmlhdGNoCmJsb29keQpibG93am9iCmJsb3cgam9iCmJvbGxvY2sKYm9sbG9rCmJvbmVyCmJvb2IKYnVnZ2VyCmJ1bQpidXR0CmJ1dHRwbHVnCmNsaXRvcmlzCmNvY2sKY29vbgpjcmFwCmN1bnQKZGFtbgpkaWNrCmRpbGRvCmR5a2UKZmFnCmZlY2sKZmVsbGF0ZQpmZWxsYXRpbwpmZWxjaGluZwpmdWNrCmYgdSBjIGsKZnVkZ2VwYWNrZXIKZnVkZ2UgcGFja2VyCmZsYW5nZQpHb2RkYW1uCkdvZCBkYW1uCmhlbGwKaG9tbwpqZXJrCmppenoKa25vYmVuZAprbm9iIGVuZApsYWJpYQpsbWFvCmxtZmFvCm11ZmYKbmlnZ2VyCm5pZ2dhCm9tZwpwZW5pcwpwaXNzCnBvb3AKcHJpY2sKcHViZQpwdXNzeQpxdWVlcgpzY3JvdHVtCnNleApzaGl0CnMgaGl0CnNoMXQKc2x1dApzbWVnbWEKc3B1bmsKdGl0CnRvc3Nlcgp0dXJkCnR3YXQKdmFnaW5hCndhbmsKd2hvcmUKd3Rm"));
		String[] swearTokens = swearListString.split("\n");
		swears = new ArrayList<String>(swearTokens.length);
		for(String swear : swearTokens) {
			swears.add(swear);
		}
	}
	
	@Override
	public String filterChat(User user, String message, ServerProfile profile) {
		if(!profile.isModerateSwearing()) return null;
		if(containsSwear(message)) return "This message contains swearing.";
		return null;
	}
	
	public boolean containsSwear(String message) {
		String readable = message.toLowerCase();
		for(String toOmit : TOKENS_TO_OMIT) {
			readable = readable.replace(toOmit, "");
		}
		for(String swear : swears) {
			if(readable.contains(swear.toLowerCase())) return true;
		}
		return false;
	}

}

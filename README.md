
![A guardian from breath of the wild](https://i.imgur.com/3DFIDkI.png)

## The Guardian
The guardian is a moderation/administration bot made with server owners in mind. It is easy to use, there is a large set of tools from simple moderation to server backups and security scans.

[Click Here for the Bot Invite Link](https://discordapp.com/oauth2/authorize?client_id=591840782793834505&scope=bot&permissions=8)

## Team Members
 - Toon Link#8313 (148118565386584065)
 - I'm by myself, there's no one else :/

## Features
 - Userinfo/See/Whois
 - Per-User private voice channels
 - Suggestions
 - Polls
 - Reaction Roles
 - Advanced server configuration (See further down)
 - Permission Security Scanning
 - Server Information
 - Channel Locking/Unlocking
 - Message Bulk-Clearing
 - Per-User warning system
 - Role Memory (Re-Apply roles on leave/rejoin)
 - New Account Flagging
 - Deleted Message Logging
 - Moderation Action Logging
 - Role mentioning
 - Channel slowmode
 - Chat Moderation
 - Kick
 - Ban/Temporary Ban
 - Mute/Temporary Mute/Unmute
 - Deafen/Temporary Deafen/Undeafen
 - Paginated Help Menu

## Server Configuration
**Every server** has their own custom server configuration. The bot adapts its features to it. **You can customize:**
 - The bot prefix
 - The bot command channels
 - The custom voice channel category
 - The suggestion channel
 - The account flagging channel
 - The moderation action logging channel
 - The deleted messages channel (Also moderated messages)
 - If the bot deletes commands after execution
 - If the bot tells the user when commands are invalid
 - If the bot saves the roles of a user when he leaves/rejoins
 - If the bot moderates the chat for advertising
 - If the bot moderates the chat for swearing
 - If the bot moderates the chat for spamming

## Setup
This should **only** be done if you are not able to use the provided bot (if he is offline or unavailable). Anyways, here it is:
 1. Compile the bot's source code into a runnable jar (Or download from the release section)
 2. Create a `config.json` file in the same folder.
 3. Inside of said file, insert this: 

~~~~
{
	"token": "YOUR TOKEN",
	"prefix": "THE DEFAULT PREFIX",
	"presence": "THE PRESENCE" //The presence will be Playing *your presence*
}
~~~~
4. You're good to go! Simply run the jar file with `java -jar <filename>` in the command line.

## Licensing
The project is under the **GNU General Public License v3.0**

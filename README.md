# Setup

Default

1. First you have to put the .jar in the plugins folder
2. Restart your server
3. The plugin should work now

MySQL

1. If you want to use MySQL, naviagate into the Friends2.0 folder who was genereated into the plugins folder
2. Open the MySQL.yml
3. Set 'Enable' to 'true'
4. Fill the other options with your login data
5. Restart your server
4. MySQL should work now

BungeeCord

1. First you have to download the BungeeCord-Addon from spigot (https://www.spigotmc.org/resources/friends2-0-mc1-8-1-10-bungeecord-addon.28909/)
2. Put in the downloaded .jar in the bungeecord Plugin folder
3. Restart your bungee
4. Navigate to the normal Friends plugin folder on your spigot/bukkit server
5. Open the config.yml
6. Set 'BungeeMode' to 'true'
7. Restart your server
8. BungeeCord should work now

# Commands & Permissions

| Commands      | Description   | Permission  |
| ------------- |:-------------:| -----:|
| /friends | Opens the gui | - Friends.Use|
| /friends help     | Shows all commands | - Friends.Use |
| /friends add [Player]      | Sends a friend-request to the player | - Friends.Commands.Add |
| /friends remove [Player] | Removes a player form your friendlist  | - Friends.Commands.Remove |
| /friends accept [Player] | Accepts a request  | - Friends.Commands.Accept |
| /friends acceptall | Accepts all open requets | - Friends.Commands.Acceptall
| /friends deny [Player] | Deny a request  | - Friends.Commands.Deny |
| /friends block [Player] | Blocks a player  | - Friends.Commands.Block |
| /friends unblock [Player] | Unlocks a player  | - Friends.CommandsUnblock |
| /friends list | Shows all your current friends  | - Friends.Commands.List |
| /friends toggle requests | Enable or disable requests  | - Friends.Commands.Toggle.Requests |
| /friends toggle chat | Enable or disable the friend-chat  | - Friends.Commands.Toggle.Chat |
| /friends toggle jumping | Allow or disallow friends to jump to you  | - Friends.Commands.Toggle.Jumping |
| /friends toggle msg | Allow or disallow friends to jsend private messages  | - Friends.Commands.Toggle.Msg |
| /friends toggle spychat | Allows admins to see the friendchat of others | - Friends.Commands.SpyChat |
| /friends info | See some additional informations  | / |
| /friends reload | Reload the plugin to commit changes | - Friends.Commands.Reload |
| /friends msg [Player] | Sends a private message | - Friends.Commands.Msg
| /status [Message] | Sets a status | - Friends.Commands.Status |
| /status clear [Player] | Clears the status of specified player | - Friends.Commands.Status.Clear |

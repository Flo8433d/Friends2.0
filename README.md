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
| /friends help     | Shows all commands | - Friends.Use |
| /friends add <Player>      | Sends a friend-request to the player | - Friends.Use |
| /friends remove <Player> | Removes a player form your friendlist  | - Friends.Use |
| /friends accept <Player> | Accepts a request  | - Friends.Use |
| /friends deny <Player> | Deny a request  | - Friends.Use |
| /friends block <Player> | Blocks a player  | - Friends.Use |
| /friends unblock <Player> | Unlocks a player  | - Friends.Use |
| /friends list | Shows all your current friends  | - Friends.Use |
| /friends toggle requests | Enable or disable requests  | - Friends.Use
| /friends toggle chat | Enable or disable the friend-chat  | - Friends.Use |
| /friends toggle jumping | Allow or disallow friends to jump to you  | - Friends.Use |
| /friends info | See some additional informations  | - Friends.Use |

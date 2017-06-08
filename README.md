# Setup

Default

1. First you have to put the .jar in the plugins folder
2. Restart your server
3. The plugin should work now

MySQL

1. If you want to use MySQL, naviagate into the Friends2.0 folder, who was generated into the plugins folder
2. Open the MySQL.yml
3. Set 'Enable' to 'true'
4. Fill the other options with your login data
5. Restart your server
4. MySQL should work now

BungeeCord

1. First you have to download the BungeeCord-Addon from spigot (https://www.spigotmc.org/resources/friends2-0-mc1-8-1-10-bungeecord-addon.28909/)
2. Put the downloaded .jar in the bungeecord Plugin folder
3. Restart your bungee
4. Navigate to the normal Friends plugin folder on your spigot/bukkit server
5. Open the config.yml
6. Set 'BungeeMode' to 'true'
7. Set BungeeCord and IP-Forwarding in your Spigot.yml in your server-folder to 'true'
8. Restart your server
9. BungeeCord should work now

# Commands & Permissions

| Commands      | Description   | Permission  |
| ------------- |:-------------:| -----:|
| /timesg | Shows all commands | - |
| /setlobby | Set the lobby to the players location | TimeSG.Commands.Setup |
| /createmap [Name] | Creates a map | TimeSG.Commands.Setup |
| /deletemap [Name] | Deletes a map  | TimeSG.Commands.Setup |
| /setspectator [Name] | Sets the spectator-spawn to the players location | TimeSG.Commands.Setup |
| /addspawn [Name] | Adds a spawn  | TimeSG.Commands.Setup |
| /removespawn [Name] | Removes a spawn | TimeSG.Commands.Setup |
| /sethologram | Sets the position for the holograms  | TimeSG.Commands.Setup |
| /setranking [Rank] [armorstand / sign] | Sets the armorstand/sign for a rank | TimeSG.Commands.Setup |
| /removeranking [Rank] [armorstand / sign] | Removes the armorstand/sign for a rank | TimeSG.Commands.Setup |
| /savearmorstand [Rank] | Saves the current loadout of a armorstand | TimeSG.Commands.Setup |
| /buildmode | Toggles the possibillity to build  | TimeSG.Commands.Buildmode |
| /fix | Fix your position  | TimeSG.Commands.Fix |
| /start | Reduces the counter to 10 seconds  | TimeSG.Commands.Start |
| /forcemap | Forces the map | TimeSG.Commands.Forcemap |
| /stats | Shows your stats  | - |
| /stats [Player] | Shows the stats of chosen the player | TimeSG.Stats.Others |
| /stats #[Rank] | Shows the stats of a specified rank | TimeSG.Stats.Others |
| /top | Shows the top players | TimeSG.Commands.Top |


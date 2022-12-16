# PVPToggle
MC-spigot plugin; Players can toggle their PVP on or off. 

Utilize minecraft player interaction events to prevent it harming the players that have PVP disabled.
Use schedulers to send messages less frequient and check for combat logging. Kill the player if it stays logged off long enough.

Combat log is implemented,. 

### Commands
- `/pvp <on/off> [player]`
  - Toggels player's pvp on or off
- `/pvpcheck <player>`
  - Check the PVP status of a player
- `/pvpkillprotection <on/off> [player]`
  - Set player pvp off automatically after killed when protection is on. Otherwise, pvp stays on after death. (Spawnkilling is possible with this setting)
- `/pvpreload`
  - Reloads the configuration files and messages.

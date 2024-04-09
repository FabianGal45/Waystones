# Custom MC plugin that changes the way players teleport
This plugin has been developed from the ground up where I had to work with the Spigot/Paper API which runs the server architecture. 

The main features of this plugin are:
- **Place a Private Waystone** - Lodestone block on top of Emerald block
- **Place a Public Waystione** - Lodestone block on top of Netherite block
- **Break a Waystone** - Break one either the lodestone, emerald or Netherite block or by external factors such as explosions or other players
- **Interact with Waystone**  - Upon right-clicking the lodestone, a GUI will open.
- **GUI** - Provides a paginated, modular split-view with private Waystones on top and Public Waystones on the bottom. If there are more public Waystones available and it finished displaying all the player's current active Waystones, then the GUI will only display public Waystones
- **Teleport a player** 
- **Teleport a player & nearby players** - With a confirmation chat system
- **Link a compass** - Upon right-clicking a Waystone with a compass in hand, the compass will be linked. This will allow a player to teleport back from anywhere in the world by right-clicking again whilst holding the compass.
- **Unlock more Waystones** - A player can purchase the right to own more private Waystones
- **Discounted price** - This feature depends on the voting plugin API and will be automatically disabled when not found. This offers a discount based on the number of points accumulated by voting on the server.

This is the project I am most proud of. Most of the work done for this plugin was done by me over a few weeks while managing a server. There were many issues and tests done to make sure that everything works as intended.
The part I am most proud of is the modular pagination system and the class inheritance used to make the development easier.

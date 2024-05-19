# Custom MC plugin that changes the way players teleport
This plugin has been developed from the ground up where I had to work with the Spigot/Paper API which runs the server architecture. 

The main features of this plugin are:
- **Place a Private Waystone** - Lodestone block on top of an Emerald block. [Private Waystone](https://github.com/FabianGal45/Waystones/assets/73531299/682f459f-7dd7-473b-b742-0e7c26826739 "Private Waystone")
- **Place a Public Waystione** - Lodestone block on top of a Netherite block. [Public Waystone](https://github.com/FabianGal45/Waystones/assets/73531299/a00ca445-a017-4049-a819-103592878934 "Public Waystone")
- **Break a Waystone** - Break one either the lodestone, emerald or Netherite block or by external factors such as explosions or other players
- **Interact with Waystone**  - Upon right-clicking the lodestone, a GUI will open.
- **GUI** - Provides a paginated, modular split-view with private Waystones on top and Public Waystones on the bottom. If there are more public Waystones available and it finished displaying all the player's current active Waystones, then the GUI will only display public Waystones. [GUI](https://github.com/FabianGal45/Waystones/assets/73531299/9b02cf24-6df3-4481-9494-514742978ddd "GUI showcase")
- **Teleport a player** 
- **Teleport a player & nearby players** - With a confirmation chat system
- **Teleport to death location** - Players have the option to teleport to their last death location within the GUI. Each teleportation costs 1 Echo Shard. [TP to death location](https://github.com/FabianGal45/Waystones/assets/73531299/9306ee69-9a88-423b-90ed-4600513fea5d "TP to death location showcase")
- **Link a compass** - Upon right-clicking a Waystone with a compass in hand, the compass will be linked. This will allow a player to teleport back from anywhere in the world by right-clicking again whilst holding the compass. [Compass Link](https://github.com/FabianGal45/Waystones/assets/73531299/914ec2c1-edba-405c-b0a2-7134b513b763 "Compass Link") | [Compass Link - Text](https://github.com/FabianGal45/Waystones/assets/73531299/b48e6f0b-ba7f-42df-9699-6c182b6b67d3 "Compass Link - Text") 
- **Unlock more Waystones** - A player can purchase the right to own more private Waystones
- **Discounted price** - This feature depends on the voting plugin API and will be automatically disabled when not found. This offers a discount based on the number of points accumulated by voting on the server. [Discount](https://github.com/FabianGal45/Waystones/assets/73531299/559b16d8-128a-4d91-a57b-d957496fb9ae "Discount showcase")
- **Edit Private/Public Waystone** - Players are able to edit the name, cathegory, and cost of a Waystone or even remove them without physically breaking it.
- **Rate a public Waystone** - Players can rate each other's public waystones.

This is the project I am most proud of. Most of the work done for this plugin was done by me over a few weeks while managing a server. There were many issues and tests done to make sure that everything worked as intended.
The part I am most proud of is the modular pagination system and the class inheritance used to make the development easier.

This project is also managed using Trello using multiple techniques to filter and manage my time efficiently.
![Trello Project showcase](https://github.com/FabianGal45/Waystones/assets/73531299/2a2cdaac-4c0c-4039-88db-5a6402747799 "Trello project showcase")

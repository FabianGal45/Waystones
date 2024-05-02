# Custom MC plugin that changes the way players teleport
This plugin has been developed from the ground up where I had to work with the Spigot/Paper API which runs the server architecture. 

The main features of this plugin are:
- **Place a Private Waystone** - Lodestone block on top of an Emerald block. [Private Waystone](https://cdn.discordapp.com/attachments/859895650900377610/1059539254885220433/image.png?ex=6626acd3&is=661437d3&hm=87ac0e3edd6153c8da1c2d32bb1916585663107e02b03eb59ce0e8c5b800ffae& "Private Waystone")
- **Place a Public Waystione** - Lodestone block on top of a Netherite block. [Public Waystone](https://media.discordapp.net/attachments/859895650900377610/1059539255262728242/image.png?ex=6626acd3&is=661437d3&hm=71d97b9bb1092b9d71140707ffc8cc7c76acf468f3a52f16be2949c19faec27d&=&format=webp&quality=lossless "Public Waystone")
- **Break a Waystone** - Break one either the lodestone, emerald or Netherite block or by external factors such as explosions or other players
- **Interact with Waystone**  - Upon right-clicking the lodestone, a GUI will open.
- **GUI** - Provides a paginated, modular split-view with private Waystones on top and Public Waystones on the bottom. If there are more public Waystones available and it finished displaying all the player's current active Waystones, then the GUI will only display public Waystones. [GUI](https://cdn.discordapp.com/attachments/866756818712789002/1227541750135132232/image.png?ex=6628c857&is=66165357&hm=aeac65cbde97e84bedc28beb3a04d186d8477a89f12c4cee78033f959d792a15& "GUI showcase")
- **Teleport a player** 
- **Teleport a player & nearby players** - With a confirmation chat system
- **Teleport to death location** - Players have the option to teleport to their last death location within the GUI. Each teleportation costs 1 Echo Shard. [TP to death location](https://cdn.discordapp.com/attachments/866756818712789002/1227543301800984606/image.png?ex=6628c9c9&is=661654c9&hm=9612a310b41147108068a9a3b8f08520579a67e019d22d07d285825f625c3c03& "TP to death location showcase")
- **Link a compass** - Upon right-clicking a Waystone with a compass in hand, the compass will be linked. This will allow a player to teleport back from anywhere in the world by right-clicking again whilst holding the compass. [Compass Link](https://cdn.discordapp.com/attachments/859895650900377610/1153823888191533056/image.png?ex=662839bb&is=6615c4bb&hm=3e8f13d17cd3dd95b019be4fbf3d72bf450198681eb4f28a54137367d6167a5b& "Compass Link") | [Compass Link - Text](https://cdn.discordapp.com/attachments/859895650900377610/1153823888459960431/image.png?ex=662839bc&is=6615c4bc&hm=1e4ddb8dbf231df19ec2d8aa4bd9ad274067528fe30038fa8d6bf125914239dc& "Compass Link - Text") 
- **Unlock more Waystones** - A player can purchase the right to own more private Waystones
- **Discounted price** - This feature depends on the voting plugin API and will be automatically disabled when not found. This offers a discount based on the number of points accumulated by voting on the server. [Discount](https://cdn.discordapp.com/attachments/866756818712789002/1227542489234145301/image.png?ex=6628c908&is=66165408&hm=5323430877ea980022aefd6c2fb84c7f2d89d49208e27f96e085758fd94dc685& "Discount showcase")
- **Edit Private/Public Waystone** - Players are able to edit the name, cathegory, and cost of a Waystone or even remove them without physically breaking it.
- **Rate a public Waystone** - Players can rate each other's public waystones.

This is the project I am most proud of. Most of the work done for this plugin was done by me over a few weeks while managing a server. There were many issues and tests done to make sure that everything worked as intended.
The part I am most proud of is the modular pagination system and the class inheritance used to make the development easier.

This project is also managed using Trello using multiple techniques to filter and manage my time efficiently.
![Trello Project showcase](https://github.com/FabianGal45/Waystones/assets/73531299/2a2cdaac-4c0c-4039-88db-5a6402747799 "Trello project showcase")

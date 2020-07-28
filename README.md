# OtherAnimalTeleport [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) [![Discord][discordImg]][discordLink]
Simple plugin to allow users to take any leashed animals in their proximity while teleporting.

Branch Name | Version | Build Status
---|---|---
Master | 2.0-b26 | [![Build Status](https://travis-ci.org/CoolLord22/OtherAnimalTeleport.svg?branch=master)](https://travis-ci.org/CoolLord22/OtherAnimalTeleport) 
Beta | 2.1 | [![Build Status](https://travis-ci.org/CoolLord22/OtherAnimalTeleport.svg?branch=2.1)](https://travis-ci.org/CoolLord22/OtherAnimalTeleport)

# Features
- Supports permission checking to only enable 'ranked' users to teleport animals.
- Gives the entities being teleported damage-resistance to ensure it doesn't die on teleport.
- Now supports tameable animal teleportation- if the player is near one of their tamed entities (wolves, parrots, or ocelots/cats) and teleports, those entities will teleport too.
- Now is version independent! The plugin should pull from the list of entities on the server and cross-type from that; this means it will work for all future updates. As long as the entity can be leashed or tamed, it'll work!
- Update checker to notify you of any new updates posted on Spigot!
- **NEW**! Added world-group feature to restrict the flow of entities across multiple worlds
- **NEW**! Added support for non-animals to be teleported if you have a custom plugin that allows any entity to be leashed
- **NEW**! Added Entity-List support so you can restrict which entity types are teleported!

# FAQ
Frequently Asked Questions: a compiled list of common questions we've gotten with their answers.
## Q: It isn't teleporting entities, what do I do?
A: Ensure that the user who is teleporting has the otheranimalteleport.player.use permission node, and has the respective teleportpets/teleportleashed permission node as well. Also, ensure the entity is inside the configured radius! See bullets 1 and 2 below for more information.
## Q: Why aren't tamed animals (wolves, ocelots/cats, or parrots teleporting)? 
A: Tamed entities cannot be sitting while the player teleports. If the entities are not attached by a leash, ensure that their "owner" is the teleporting player. If an entity has been tamed by another player, it will NOT teleport.
## Q: How do I teleport hostile monsters and villagers? 
A: The plugin will only teleport entities that can be attached via leads, meaning by default, hostile mobs and non-leadable entities cannot be teleported! However, if you have a plugin that allows these entities to be leashed, the plugin now contains support for teleporting non-animal entities! Check out plugins like [Lasso](https://www.spigotmc.org/resources/lasso.54815/) to allow leading other mobs!
## Q: How do I restrict teleportation perks to certain users/ranks? .
A: The plugin supports permissions, so just give your users/ranks **otheranimalteleport.player.use**! Note that this permission node is REQUIRED to teleport any entity. To further limit teleportation, the plugin supports
- **otheranimalteleport.player.teleportpets** (give access to only teleporting nearby pets)
- **otheranimalteleport.player.teleportleashed** (give access to only teleporting nearby leashed entities)
## Q: How do I make it teleport animals within a larger radius? 
A: Check your config.yml! You can change the teleport radius to any integer you want! By default this value is 2, requiring entities to be within 2 blocks of the player.
## Q: Will animals die if they fall while teleporting? 
A: The plugin is designed to give animals damage resistance while teleporting so they should be protected from fall damage for a few seconds after their teleport.
## Q: Where can I get additional help? 
A: For any other questions, please use the Spigot Discussions forum, or check our [Discord Support Server](https://discord.gg/eHBxk5q)!

---
If you really enjoy this plugin, please drop a review (hopefully 5 stars ;)). If you appreciated my help on a question, also consider rating the plugin! I code for free- I don't ask for money for it, I don't receive donations, and I surely don't get paid to develop. I do it to help others, and when I am recognized for that help, it means everything.
The reviews section is meant for actual reviews, not to degrade the plugin, make a feature request, or get help. Please don't leave negative reviews on amazing plugins in an attempt to get the authors' attention. Believe me, we DO pay attention to every ticket, issue, discussion comment, etc. Leaving bad reviews to "get our attention" just makes us that much less likely to help you out. If you honestly need help, leave a comment on the Discussions page.
If you have a feature you want to see in the plugin, leave a comment on the Discussions page and I'll try to respond ASAP.

[discordImg]: https://img.shields.io/discord/418432278113550337.svg?logo=discord&logoWidth=18&colorB=7289DA

[discordLink]: https://discordapp.com/invite/eHBxk5q

# Squish Logs Minecraft
Official Minecraft integration for [SquishLogs](https://squish.wtf/). Documentation on how to install and setup custom loggers can be found [here](https://docs.squish.wtf/).

### Compiling
Run the "shadowJar" gradle job. The jar has to be shadowed so the user doesn't have to install the dependencies (aka the websocket library I'm using).

### Available Loggers
- Chat
    - Player Messages
    - Player Commands Ran
- Combat
    - PvP / PvE
    - Self-inflicted Player Deaths
- Players
    - Join/Leave
    - Teleport
    - Gamemode Changes
    - Villager Trading
    - Advancements
    - Potions/Effects
- System
    - Console Command ran
- World
    - Village Raids
    - Mob Renaming
    - Fires Started
    - Boss Entity Spawns
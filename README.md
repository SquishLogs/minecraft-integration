# Squish Logs Minecraft
https://squish.wtf/

### Loggers
- System
  - Server Start/Stop
  - Console Command ran
- World
  - Item Dropped
  - Raid Start/Stop
  - Mob renaming
  - Fire starting
  - Boss spawned
- Players
  - Join/Leave
  - Chat
  - Commands
  - Teleport
  - Gamemode Change
  - Villager Trading
  - Crafting/Smelting
  - Enchanting
  - Advancements
  - Potions
- Combat
  - Mob killed / kills player
  - Player killed / kills player
  - Player Suicide

### Compiling
Run the "shadowJar" gradle job. The jar has to be shadowed so the user doesn't have to worry about also installing the dependencies (aka the websocket library I'm using).
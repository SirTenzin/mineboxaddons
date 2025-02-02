<div align="center">
  <h1>[MineboxAddons]</h1>
  <img width="256" height="256" src="https://github.com/SirTenzin/mineboxaddons/blob/master/src/main/resources/assets/mineboxaddons/icon.png?raw=true" alt="logo">
</div>

MineboxAddons is a simple utility mod for the [Minebox](https://minebox.co) server.
It adds many quality of life features and improvements to the experience on the server.

## Features

### Commands:

- `/items view [item name]` - View the wiki page for the item name specified.
- `/items search [item name]` - Search for an item in the wiki and quickly view its stats.
- `/items price (period) (direction) [item name]` - View the buy or sell price of an item in the Bazaar for the latest Day, Week or Month.
- `/mba objective create <objectives>` - Creates a widget to track a custom list of objectives. Simply separate each of your objectives with a comma, and it will display a scoreboard of the current step
- `/mbaq <objectives>` - Alias for above
- `/mba objective next` - Advances the current objective to the next step
- `/mbaqn` - Alias for above
- `/mba config gui` - Move or hide the HUD widgets

### HUD Widgets

- Durability Widget: Displays the durability of the item in your hand, for example a Watering Can
- Objective Tracker: Displays the current step of your objectives, as set by `/mba objective create`
- Bakery, Cheese, Coffee and Cocktail timers: Displays the time until the next refresh
- Bazaar Price Tooltip: Hover over an item in your inventory and the bazaar price will be added to the tooltip

### QoL Features

- Shop Toasts: Displays a toast notification and plays a sound when a shop opens
- Bank Macro: When in the bank menu, type "k", "m" or "b" to quickly type the respective amount of zeros without needing to do it manually
- Island Auto Teleport Macro: When you join the server, you can set a delay of 1-10 seconds before automatically running "/is"

### Languages

Currently, this mod only works with English and French (fr_FR). Some features in other languages may not work correctly or show correct wording.

## Installation

### Requirements

- Minecraft 1.21.3
- Fabric Mod Loader 0.16.9+
- [Fabric API](https://modrinth.com/mod/fabric-api/version/0.114.0+1.21.3)
- [owo-lib](https://modrinth.com/mod/owo-lib/version/0.12.18+1.21.2)
- [Mod Menu](https://modrinth.com/mod/modmenu/version/12.0.0)
- [Fabric Kotlin](https://modrinth.com/mod/fabric-language-kotlin)

### Modrinth

The easiest way is to download the mod from [Modrinth](https://modrinth.com/mod/mineboxaddons).

### Fabric

1. Download the latest version of the mod from the [releases page](https://github.com/sirtenzin/MineboxAddons/releases).
2. Place the downloaded `.jar` file in your `mods` folder.
3. Launch the game and enjoy!

## Licenses and credits:

- [WildfireRomeo (toast stuff)](https://github.com/WildfireRomeo/WildfireFemaleGenderMod/blob/fabric-1.21.4/src/main/java/com/wildfire/gui/WildfireToast.java)
- <a href="https://www.textstudio.com/">TextStudio.com Font Generator (logo)</a>
- Zelda (French Translation)
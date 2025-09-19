# Better Fishing

[English](README.md) | [中文](README-zh_CN.md)

An automatic fishing mod for Minecraft 1.21.8 built on the Fabric framework.

## Features

### 🎣 **Core Functionality**
- **Automatic Fishing**: Automatically pulls up the fishing rod and recasts when a fish is caught
- **Smart Detection**: Uses advanced event detection to prevent duplicate operations
- **Configurable Timing**: Customizable delays for pull-up and recast actions (5-20 ticks for pull-up, 10-500 ticks for recast)

### 🛡️ **Rod Protection**
- **Durability Protection**: Automatically stops fishing when rod durability is low to prevent breaking
- **Smart Threshold**: Configurable protection threshold (default: 5 durability remaining)

### 🚫 **Item Management**
- **Auto-Drop System**: Automatically drops unwanted fishing items based on configurable block list
- **Loot Table Analysis**: Dynamically analyzes fishing loot tables to identify droppable items
- **Inventory Management**: Keeps inventory clean by removing junk items

### ⚙️ **Configuration Interface**
- **ModMenu Integration**: Easy-to-use configuration screen accessible through ModMenu
- **Real-time Settings**: All settings can be changed without restarting the game
- **Responsive UI**: Configuration interface adapts to different screen sizes and window modes
- **Multilingual Support**: Full support for English and Chinese languages

## Requirements

- **Minecraft**: 1.21.8
- **Fabric Loader**: ≥ 0.17.2
- **Fabric API**: 0.133.0+1.21.8
- **Java**: ≥ 21

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Place the Better Fishing mod file in `.minecraft/mods` directory
4. Launch the game

## Usage

### 🎮 **Basic Usage**
1. Hold a fishing rod in game
2. Cast the fishing line normally
3. The mod will automatically:
   - Pull up the fishing rod when a fish is caught
   - Recast the fishing line after a short delay

### ⚙️ **Configuration**
1. Install [ModMenu](https://modrinth.com/mod/modmenu) (recommended)
2. Open the game menu and click "Mods"
3. Find "Better Fishing" and click the config button
4. Adjust settings as needed:
   - **Auto Fishing**: Enable/disable automatic fishing
   - **Rod Protection**: Enable durability protection
   - **Pull Up Delay**: Adjust timing for pulling up (5-20 ticks)
   - **Recast Delay**: Adjust timing for recasting (10-500 ticks)
   - **Block List**: Add items to auto-drop (e.g., `minecraft:cod,minecraft:salmon`)

---

**Enjoy your automatic fishing experience!** 🎣

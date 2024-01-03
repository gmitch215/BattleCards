# <img style="height: 7vh; width: auto;" src="https://repository-images.githubusercontent.com/555359817/10af05e9-d2f4-41c8-9451-d9bbfa2bd0bb"> BattleCards
> Unique, action-packed Minecraft Plugin, featuring upgradable and collectible cards used in Battle.

# 🎴 Deploy your Mobs!

<details>
    <summary>About</summary>

BattleCards is a free Minecraft Plugin developed for Spigot 1.8+ servers. It creates a unique combat system inspired from games like Clash Royale where entities can drop collectible cards that are upgradable and usable in PvP and PvE combat.
Created by GamerCoder, it allows for an amazing and unique Minecraft Experience for all ages. Deploy your Cards!
</details>

---

## ❓ Why?

- **Efficient**: BattleCards is designed in the Kotlin language, providing a unique method of handling API, properties, and elements for a optimized and performative experience.
- **Simple**: BattleCards is one of the most well-documented plugins on the market, allowing for easy customization and modification. We also offer an easy-to-learn API for directly modifying plugin behavior to your liking.
- **Unique**: The concept of BattleCards is one of its unique, inspired by popular games known for their quality and strategy.
- **Strategic**: Similar to how strategy is implemented in other card-based games, it encourages critical thinking through the limiations of how cards are used to win your battles.
- **Transparent**: All source code and implementation is open source for developers to contribute, test, and modify. Please consider supporting my work :)

---

## 🗒️ Usage
Download the plugin from any official providers (Spigot, Modrinth, BuiltByBit, Hangar, etc.). Drop the JAR file into your "plugins" folder.

### ‼️ Important Note
This plugin is built in Kotlin. While the plugin will automatically download the necessary libraries for the plugin to run, it is recommended that you install the [Kotlin Spigot Plugin](https://modrinth.com/plugin/kotlinmc/). **This is only required if you are running on a server version below 1.16.**

---

## 📓Changelog

📖 **v1.1.0** - January 2, 2024 | Generation II
- 3 new Generation II Cards
- New Card Equipment
- Turkish Translation
- **Card & Equipment Catalogue**
  - View useful information & crafting recipes about all cards & card equipment
- Release of the [official guide](https://guide.battlecards.gamercoder215.me)
- Equipment Drops from Normal Entities
- Piglin Bartering for Equipment & Card Shards (1.16+)
- Decrease BASIC and COMMON Max Experience Requirements
- Minor Bug Fixes
  - Added Missing Translations 

🛠️ v1.0.2 - December 2, 2023
- Dependency Updates
- 10+ New Card Equipments
- Added missing `chainmail` Ability Translation

🐛 v1.0.1 - October 27, 2023
- Dependency Updates
- Fix 1.20.2 Support, Packet Errors
- Add New Metrics
- Buff Thunder Revenant's Max Health Modifier
- Fix Card Identification on 1.8-1.11.2
- Other Minor Additions

🃏 **v1.0.0** - September 3, 2023

Initial Release of BattleCards

---

## 🔮 Future Features

Coming Soon...

---
## 💻 BattleCards API
![GitHub](https://img.shields.io/github/license/GamerCoder215/BattleCards)
[![GitHub branch checks state](https://github.com/GamerCoder215/BattleCards/actions/workflows/build.yml/badge.svg)](https://github.com/Team-Inceptus/PlasmaEnchants/actions/)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/GamerCoder215/BattleCards?style=plastic)
![GitHub repo size](https://img.shields.io/github/repo-size/GamerCoder215/BattleCards)

**JavaDocs:** [https://battlecards.gamercoder215.me](https://battlecards.gamercoder215.me)

### Installation

<details>
    <summary>Maven</summary>

```xml
<project>

    <repositories>
        <repository>
            <id>codemc-snapshots</id>
            <url>https://repo.codemc.io/repository/maven-snapshots/</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>me.gamercoder215.battlecards</groupId>
            <artifactId>battlecards-api</artifactId>
            <version>[VERSION]</version>
        </dependency>
    </dependencies>
    
</project>
```
</details>

<details>
    <summary>Gradle (Groovy)</summary>

```gradle
repositories {
    maven { url 'https://repo.codemc.io/repository/maven-snapshots/' }
}

dependencies {
    implementation 'me.gamercoder215.battlecards:battlecards-api:[VERSION]'
}
```
</details>

<details>
    <summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    implementation('me.gamercoder215.battlecards:battlecards-api:[VERSION]')
}
```
</details>

---
## 📷 Screenshots

![Screenshot9](https://media.discordapp.net/attachments/894254760075603980/1167496799611801610/2023-10-27_11.11.38.png?ex=654e5720&is=653be220&hm=33a479b1317d294707995f2e56ebfdd0d4ca90940daa41bb0c2ba01d2aac6aa7)
![Screenshot8](https://media.discordapp.net/attachments/894254760075603980/1167496797523034162/2023-10-27_11.14.03.png?ex=654e571f&is=653be21f&hm=5092e46180fded697d2b7499b65b5254338e0f5712cd17f9e696865eb68387f9)
![Screenshot7](https://media.discordapp.net/attachments/894254760075603980/1167496799234293790/2023-10-27_11.12.14.png?ex=654e571f&is=653be21f&hm=388141e559308b714c9816a3551337dfb3c2308f51647712417a5207d2416cf2)
![Screenshot6](https://media.discordapp.net/attachments/894254760075603980/1167496798512873616/2023-10-27_11.12.42.png?ex=654e571f&is=653be21f&hm=bbbb11836dbd19e2cc1c98236765a8e234689801474975e5984dffaf6bf7823e)
![Screenshot5](https://media.discordapp.net/attachments/894254760075603980/1167496800178016387/2023-10-27_11.10.29.png?ex=654e5720&is=653be220&hm=cde0b63792c69c7cf8471ae5fb67c78a125f1f7306b00659b955741d2b696776)
![Screenshot4](https://media.discordapp.net/attachments/894254760075603980/1135013631440994345/2023-07-29_19.58.35.png)
![Screenshot3](https://media.discordapp.net/attachments/894254760075603980/1135013630971236513/2023-07-29_19.58.19.png)
![Screenshot2](https://media.discordapp.net/attachments/894254760075603980/1135013630618898452/2023-07-29_19.58.16.png)
![Screenshot1](https://media.discordapp.net/attachments/894254760075603980/1135013630241427506/2023-07-29_19.58.08.png)

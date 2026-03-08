# EpicGuardPlus

[![GitHub stars](https://img.shields.io/github/stars/Sqouidly/EpicGuardPlus)](https://github.com/Sqouidly/EpicGuardPlus/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/Sqouidly/EpicGuardPlus)](https://github.com/Sqouidly/EpicGuardPlus/network)
[![GitHub issues](https://img.shields.io/github/issues/Sqouidly/EpicGuardPlus)](https://github.com/Sqouidly/EpicGuardPlus/issues)
[![GitHub license](https://img.shields.io/github/license/Sqouidly/EpicGuardPlus)](https://github.com/Sqouidly/EpicGuardPlus/blob/master/LICENSE)
[![Java CI](https://github.com/Sqouidly/EpicGuardPlus/actions/workflows/maven.yml/badge.svg)](https://github.com/Sqouidly/EpicGuardPlus/actions/workflows/maven.yml)

**🛡️➕ Enhanced bot protection plugin for Minecraft servers with extended security features.**

EpicGuardPlus is an actively maintained fork of [EpicGuard](https://github.com/4drian3d/EpicGuard) (originally by [xxneox](https://github.com/xxneox)), with integrated features from [AntiVPN](https://github.com/funkemunky/AntiVPN) by [funkemunky](https://github.com/funkemunky). Provides robust antibot and anti-VPN protection for your Minecraft server across multiple platforms.

---

## Supported Platforms

| Platform | Version | Notes |
|----------|---------|-------|
| [Paper](https://papermc.io/) | 1.17+ | All Paper forks supported (Purpur, etc.) |
| [Velocity](https://velocitypowered.com/) | 3.0+ | Recommended for proxy setups |
| [Waterfall](https://papermc.io/downloads#Waterfall) | 1.17+ | BungeeCord fork |

**Requires Java 17 or higher.**

---

## Features

- **8+ configurable antibot checks:**
  - **Geographical check** - country/city blacklist or whitelist
  - **VPN/Proxy check** - configurable detection services with caching
  - **Nickname check** - block nickname patterns using regex
  - **Reconnect check** - require re-joining with matching address and nickname
  - **Server list check** - require pinging the server before connecting
  - **Settings check** - verify client sends a settings packet after joining
  - **Lockdown** - temporarily block connections during heavy bot attacks
  - **Name similarity check**
  - **Account limit** - limit accounts per IP address
- **SQLite/MySQL** database support
- **Live actionbar** attack statistics
- **Automatic whitelisting** for verified players
- **Console filter** to reduce log spam during attacks
- **Automatic update checker**
- **Staff alerts** - notify online staff when a player is blocked
- **Custom commands on detection** - execute console commands when a player is blocked
- **Bedrock prefix whitelisting** - exempt players with configurable name prefixes

---

## Commands & Permissions

### Permissions

| Permission | Description |
|------------|-------------|
| `epicguard.admin` | Access to all `/guard` commands and staff alerts |
| `epicguard.bypass` | Bypass all antibot checks (auto-whitelists the player's IP on join) |

### Commands

Platform-specific aliases: `/guardpaper`, `/guardvelocity`, `/guardwaterfall`

| Command | Description |
|---------|-------------|
| `/guard help` | Displays all available commands and status |
| `/guard reload` | Reloads configuration and messages |
| `/guard whitelist <add/remove> <nick/address>` | Manage the whitelist |
| `/guard blacklist <add/remove> <nick/address>` | Manage the blacklist |
| `/guard analyze <nick/address>` | Detailed info about an address or nickname |
| `/guard status` | Toggle live attack info on actionbar |
| `/guard save` | Force save data to the database |

---

## Installation

1. Download the latest release from [Releases](https://github.com/Sqouidly/EpicGuardPlus/releases).
2. Place the correct JAR file for your platform into your server's `plugins` folder:
   - `EpicGuardPlusPaper-x.x.x.jar` for Paper/Spigot servers
   - `EpicGuardPlusVelocity-x.x.x.jar` for Velocity proxies
   - `EpicGuardPlusWaterfall-x.x.x.jar` for Waterfall/BungeeCord proxies
3. Restart your server.
4. Edit the generated configuration files in the `EpicGuardPlus` folder.
5. Use `/guard reload` to apply changes.

---

## Building from Source

```bash
git clone https://github.com/Sqouidly/EpicGuardPlus.git
cd EpicGuardPlus
mvn clean package
```

Build artifacts will be located in the `build/` directory.

---

## API Usage

<details>
<summary>Gradle (Groovy)</summary>

```groovy
repositories {
    maven {
      url = 'https://jitpack.io'
    }
}

dependencies {
    compileOnly 'com.github.Sqouidly:EpicGuardPlus:[VERSION]'
}
```
</details>

<details>
<summary>Gradle (Kotlin)</summary>

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.Sqouidly:EpicGuardPlus:[VERSION]")
}
```
</details>

<details>
<summary>Maven</summary>

```xml
<repositories>
   <repository>
     <id>jitpack.io</id>
     <url>https://jitpack.io</url>
   </repository>
</repositories>

<dependencies>
    <dependency>
       <groupId>com.github.Sqouidly</groupId>
       <artifactId>EpicGuardPlus</artifactId>
       <version>[VERSION]</version>
       <scope>provided</scope>
   </dependency>
</dependencies>
```
</details>

<details>
<summary>Example Code</summary>

Make sure EpicGuardPlus is fully loaded before your plugin (add it as a dependency in your `plugin.yml`).

```java
import me.xneox.epicguard.core.EpicGuardAPI;

public class MyPlugin {
  public void example() {
    EpicGuardAPI api = EpicGuardAPI.INSTANCE;

    // Bypass an IP from all checks (useful for bot panels, trusted proxies, etc.)
    api.addBypassAddress("192.168.1.100");

    // Bypass a specific player nickname
    api.addBypassNickname("TrustedBot");

    // Remove bypass
    api.removeBypassAddress("192.168.1.100");
    api.removeBypassNickname("TrustedBot");

    // Whitelist/blacklist management
    api.whitelistAddress("10.0.0.1");
    api.blacklistAddress("1.2.3.4");
    boolean isWhitelisted = api.isWhitelisted("10.0.0.1");

    // Check if IP is VPN/proxy
    boolean isProxy = api.isProxy("8.8.8.8");

    // Geo lookup
    String country = api.getCountry("8.8.8.8");
    String city = api.getCity("8.8.8.8");

    // Attack status
    boolean underAttack = api.isUnderAttack();
    int cps = api.connectionCounter();
  }
}
```
</details>

---

## Privacy

- [MaxMind GeoLite2](https://dev.maxmind.com/geoip/geoip2/geolite2) databases are downloaded at first startup and updated weekly. Geolocation is checked locally.
- By default, IP addresses are sent to [proxycheck.io](https://proxycheck.io/) for VPN/proxy detection.
- IPs and associated nicknames are stored in the local database.
- The plugin periodically checks for updates from this repository. This can be disabled.
- **No metrics or telemetry data is collected.**

---

## Credits

- EpicGuard - Forked from [EpicGuard](https://github.com/4drian3d/EpicGuard) by [4drian3d](https://github.com/4drian3d) (GPL-3.0)
- [AntiVPN](https://github.com/funkemunky/AntiVPN) by [funkemunky](https://github.com/funkemunky) - Staff alerts, command execution, and prefix whitelisting features (Apache-2.0)

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

Portions of this project are derived from [AntiVPN](https://github.com/funkemunky/AntiVPN), licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

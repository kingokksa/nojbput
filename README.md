# No JB Put

A switchable offhand interception mod for Minecraft. · 一个可开关的「副手拦截」模组。

> **This branch targets Minecraft 1.21.1 with NeoForge 21.1.x (Java 21+).**
> 本分支对应 Minecraft 1.21.1 + NeoForge 21.1.x（Java 21+）。

[English](#english) · [中文](#中文)

---

## English

Have you ever carried a torch in your offhand into a mine, wanted it for dynamic lighting, and ended up planting it in the floor over and over? This mod fixes exactly that. It intercepts **offhand block placement** and **offhand item usage**, so the offhand stays a utility slot instead of a second hotbar.

### Features

| Feature | What it does |
|---|---|
| **Two independent rules** | *Offhand placement* stops blocks held in the offhand from being placed. *Offhand usage* stops items held in the offhand from being used (torches, food, potions, firework rockets, ...). |
| **Master switch** | Turn all interception off at once, without touching any other setting. |
| **Per-hand filters** | The main hand and the offhand each get their own whitelist or blacklist, so you can say "only intercept this while I am holding that". |
| **Flexible list syntax** | Exact ids, `*` / `?` wildcards and `#item tags`. |
| **Four-state toggle** | An in-inventory button cycles allow all → block placement → block usage → block both, colour-coded green / blue / yellow / red. |
| **Keybind** | Right Alt by default, so you do not have to open your inventory. |
| **Config screen** | A full GUI with translated labels and tooltips. |

### List syntax

| Entry | Meaning |
|---|---|
| `minecraft:torch` | exact item id |
| `minecraft:*_planks` | wildcard — `*` is any length, `?` is one character |
| `create:*` | every item of a mod |
| `#minecraft:logs` | item tag (all logs) |

An empty list means "do not filter".

### Installation

1. Install the matching mod loader for your Minecraft version (see the table below).
2. Drop the jar into your `mods/` folder.
3. **Install it on both the client and the server.**

The server is authoritative: the filter lists live in the common config, and each player's toggle state is synced from their client. If the mod is only on the client, the server has no idea and will still place and consume offhand items. If it is only on the server, the client predicts the action locally and then has it rolled back, which shows up as a small visual flicker.

### Configuration

The config lives in `config/nojbput-common.toml` and is read on both sides — set it up on the server so every player gets the same rules.

| Key | Default | Meaning |
|---|---|---|
| `masterSwitch` | `true` | Global on/off for all interception. |
| `disableOffhandPlacement` | `true` | Whether offhand block placement may be intercepted. |
| `disableOffhandUsage` | `true` | Whether offhand item usage may be intercepted. |
| `mainHandList.mode` | `WHITELIST` | Whether the main-hand list is a whitelist or a blacklist. |
| `mainHandList.items` | `[]` | Main-hand item list (see list syntax). |
| `offHandList.mode` | `BLACKLIST` | Whether the offhand list is a whitelist or a blacklist. |
| `offHandList.items` | `[]` | Offhand item list (see list syntax). |

A state that the config disables is skipped by the toggle button, and the button hides itself when nothing is left to toggle. With both rules enabled and empty lists you get the full green → blue → yellow → red cycle.

### Controls

| Action | Default |
|---|---|
| Cycle the interception state | `Right Alt` (rebindable in Options → Controls) |
| Cycle the interception state | Click the small coloured square next to the offhand slot in your inventory |
| Open the config | Mods list → No JB Put → Config (NeoForge) or the bundled config screen (Forge) |

### Supported versions

| Branch | Loader | Minecraft | Java |
|---|---|---|---|
| `forge-1.16.5` | Forge 36.2.x | 1.16.5 | 8+ |
| `forge-1.20.1` | Forge 47.4.x | 1.20.1 | 17+ |
| `neoforge-1.21.1` | NeoForge 21.1.x | 1.21.1 | 21+ |
| `neoforge-26.1.2` | NeoForge 26.1.2.x | 26.1.2 | 25+ |
| `neoforge-26.2` | NeoForge 26.2.x | 26.2 | 25+ |

Each branch is a standalone Gradle project; check out the branch you want and build it on its own.

### Building from source

```bash
./gradlew build
```

The jar lands in `build/libs/`. CI builds every branch on push — see `.github/workflows/build.yml`.

### License

MIT — see [LICENSE](LICENSE). Minecraft is a trademark of Mojang Studios; this project is not affiliated with Mojang or with the Forge/NeoForged projects.

---

## 中文

你有没有过这种情况：副手带着火把进矿洞，想拿它做动态光源照明，结果一路走一路把火把插在地上？这个 mod 就是来解决这个的。它拦截**副手放置方块**和**副手使用物品**，让副手重新变成一个「只带着、不误触」的工具位，而不是第二个快捷栏。

### 功能

| 功能 | 说明 |
|---|---|
| **两条独立规则** | *禁用副手放置* 让副手拿的方块放不出去；*禁用副手使用* 让副手拿的物品用不了（火把、食物、药水、烟花……）。 |
| **全局总开关** | 一键停用全部拦截，不用动其它设置。 |
| **主副手各自一份名单** | 主手和副手各有一份白名单或黑名单，可以做到「只有我主手拿着某样东西时才拦」。 |
| **名单语法灵活** | 精确 ID、`*` / `?` 通配符、`#物品标签`。 |
| **四态开关** | 背包里的按钮循环：全部放行 → 只拦放置 → 只拦使用 → 两者都拦，颜色分别是绿 / 蓝 / 黄 / 红。 |
| **快捷键** | 默认右 Alt，不用开背包。 |
| **配置界面** | 完整 GUI，标签与提示都已翻译。 |

### 名单语法

| 写法 | 含义 |
|---|---|
| `minecraft:torch` | 精确物品 ID |
| `minecraft:*_planks` | 通配符 —— `*` 任意长度、`?` 单个字符 |
| `create:*` | 该模组的全部物品 |
| `#minecraft:logs` | 物品标签（所有原木） |

名单留空 = 不做过滤。

### 安装

1. 先装好对应版本的 mod 加载器（见下方表格）。
2. 把 jar 放进 `mods/` 文件夹。
3. **客户端与服务端都要安装。**

服务端是权威端：过滤名单在通用配置里，每个玩家的开关状态由客户端同步过去。只装客户端的话，服务端根本不知道有这个 mod，照样会放置和消耗副手物品；只装服务端的话，客户端会先本地预测再被服务端回滚，表现为轻微的画面闪动。

### 配置

配置文件是 `config/nojbput-common.toml`，两端各读自己那份 —— 请在服务端设好，让所有玩家规则一致。

| 键 | 默认值 | 含义 |
|---|---|---|
| `masterSwitch` | `true` | 全部拦截的总开关。 |
| `disableOffhandPlacement` | `true` | 是否允许拦截副手放置。 |
| `disableOffhandUsage` | `true` | 是否允许拦截副手使用。 |
| `mainHandList.mode` | `WHITELIST` | 主手名单是白名单还是黑名单。 |
| `mainHandList.items` | `[]` | 主手物品名单（写法见上）。 |
| `offHandList.mode` | `BLACKLIST` | 副手名单是白名单还是黑名单。 |
| `offHandList.items` | `[]` | 副手物品名单（写法见上）。 |

配置里关掉的类型会被切换按钮跳过；一种都切不了时按钮自动隐藏。两条规则都开着、名单都留空时，就是完整的绿 → 蓝 → 黄 → 红循环。

### 操作方式

| 操作 | 默认 |
|---|---|
| 切换拦截状态 | `右 Alt`（可在「选项 → 按键控制」里改键） |
| 切换拦截状态 | 点背包里副手槽旁那个小色块 |
| 打开配置 | mod 列表 → No JB Put → 配置（NeoForge），或自带的配置界面（Forge） |

### 支持的版本

| 分支 | 加载器 | Minecraft | Java |
|---|---|---|---|
| `forge-1.16.5` | Forge 36.2.x | 1.16.5 | 8+ |
| `forge-1.20.1` | Forge 47.4.x | 1.20.1 | 17+ |
| `neoforge-1.21.1` | NeoForge 21.1.x | 1.21.1 | 21+ |
| `neoforge-26.1.2` | NeoForge 26.1.2.x | 26.1.2 | 25+ |
| `neoforge-26.2` | NeoForge 26.2.x | 26.2 | 25+ |

每个分支都是一个独立的 Gradle 工程，切到你要的分支单独构建即可。

### 从源码构建

```bash
./gradlew build
```

产物在 `build/libs/`。CI 会在每次 push 时构建所有分支，见 `.github/workflows/build.yml`。

### 许可证

MIT —— 见 [LICENSE](LICENSE)。Minecraft 是 Mojang Studios 的商标，本项目与 Mojang 及 Forge/NeoForged 项目无隶属关系。

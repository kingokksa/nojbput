# Changelog

## 1.8.0 — 2026-10-06

First public release. · 首次公开发布。

### Added · 新增
- Offhand item usage interception, alongside the existing offhand block placement interception.
  在原有的「副手放置」拦截之外，新增「副手使用」拦截。
- A master switch, plus independent `disableOffhandPlacement` / `disableOffhandUsage` switches.
  全局总开关，以及 `disableOffhandPlacement` / `disableOffhandUsage` 两个独立开关。
- Per-hand whitelist / blacklist filters for the main hand and the offhand.
  主手与副手各自的名单，各带白名单 / 黑名单模式。
- List entries now support `*` / `?` glob wildcards and `#item tags`, in addition to exact ids.
  名单条目现在支持 `*` / `?` 通配符与 `#物品标签`，不再只有精确 ID。
- A four-state, colour-coded toggle: allow all (green) → block placement (blue) → block usage (yellow) → block both (red).
  四态彩色开关：全部放行（绿）→ 只拦放置（蓝）→ 只拦使用（黄）→ 两者都拦（红）。
- An in-inventory toggle button next to the offhand slot, plus a rebindable keybind (right Alt by default).
  背包副手槽旁的开关按钮，以及可改键的快捷键（默认右 Alt）。
- A full config screen with translated labels and tooltips (zh_cn / en_us).
  完整的配置界面，中英双语标签与提示。
- Builds for Forge 1.16.5 / 1.20.1 and NeoForge 1.21.1 / 26.1.2 / 26.2.
  支持 Forge 1.16.5 / 1.20.1 与 NeoForge 1.21.1 / 26.1.2 / 26.2。

### Fixed · 修复
- Offhand items that act on a block through `ItemStack#useOn` (firework rockets, buckets, ...) bypassed the usage interception entirely, because `RightClickBlock` was only checked against the placement rule. This is the bug that made ParCool's right-click vault consume firework rockets.
  通过 `ItemStack#useOn` 作用于方块的副手物品（烟花、桶等）完全绕过了使用拦截 —— 因为 `RightClickBlock` 只查了放置规则。这就是 ParCool 右键翻越时消耗烟花的那个 bug。
- The default main-hand whitelist contained four torch-like items, which exempted them out of the box and made the blue/yellow states unreachable with the old defaults.
  主手白名单默认值里原本有四个火把类物品，开箱即用时就豁免了它们，配合旧默认值会让蓝 / 黄两态根本切不到。
- The NeoForge builds registered the payload handler twice, which made the mod fail to load.
  NeoForge 版本重复注册了 payload handler，导致 mod 加载失败。
- `disableOffhandUsage`'s fallback value disagreed with its declared default.
  `disableOffhandUsage` 的兜底值与其声明的默认值不一致。

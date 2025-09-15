# Conflict – Team PvP Mod for Minecraft 1.20.1 (Forge)

Conflict is a custom Forge mod that brings a **team–based PvP experience** to Minecraft 1.20.1.  
Players join **RED** or **BLUE** factions, fight for points, and win by reaching the target score before time runs out.

---

## ✨ Features

- **Faction system**  
  - Choose your team (RED or BLUE) on first join.  
  - Teams persist across sessions and respawns.
- **Automatic loadouts**  
  - Each faction spawns with its own armor, weapons, ammo, food and tools.
- **Score & timer HUD**  
  - A clean on-screen display shows RED vs BLUE score and the remaining time.
- **Spawn points with safe teleport**  
  - Admins can set custom team spawn points with a defined radius.
- **Resource mines with regeneration**  
  - Add regenerating ore blocks (e.g. iron, diamond) to create resource points.
- **Admin controls**  
  - Start/stop/reset matches, set time limits, toggle friendly fire, and manage spawn points and mines.
- **Faction skins**  
  - Players automatically get custom RED/BLUE skins in third-person view.

---

## 🔧 Commands

*(all commands start with `/`)*

### Factions
- `faction` — open team selection GUI.
- `faction reset` — reset player faction.

### Spawn points
- `spawnpt set <blue|red> [radius]` — set team spawn.
- `spawnpt show` — show all spawn points.

### Game management
- `conflict start` — start a match.
- `conflict stop` — stop the match.
- `conflict reset` — reset all scores.
- `conflict set target <points>` — set score target (default 50).
- `conflict set minutes <time>` — set match duration (default 60 min).
- `conflict ff on|off|toggle|status` — friendly-fire controls.
- `conflict status` — show current match state.

### Resource mines
- `mine add [seconds]` — create a regenerating resource block at crosshair.
- `mine remove` — remove targeted mine.

*(all commands require appropriate operator/permission level)*

---

## 📥 Installation

1. Install **Minecraft Forge 1.20.1** (recommended build 47.4.x).
2. Place the `conflict-x.x.x.jar` file in your server and client `mods/` folder.
3. (Optional) Install **SuperbWarfare** and other weapon/armor mods used in your kits.
4. Start the server and join with the same modpack on your client.

---

## 🗂️ Folder Structure

src/main/java/com/conflict/conflict -> Mod source code
src/main/resources/assets/conflict -> Textures (skins, GUI)

---

## ⚠️ Notes

- If you run a cracked (offline-mode) server, set `online-mode=false` in `server.properties`.  
  In that case, default Minecraft skins will not load; the mod's custom RED/BLUE skins will still display.
- All players must have the same modpack installed.
- Recommended minimum: 4 players for a balanced game.

---

## 🇷🇺 Описание на русском

### Conflict — командный PvP-мод для Minecraft 1.20.1 (Forge)

Conflict — это мод для Forge, который добавляет **командные сражения** в Minecraft 1.20.1.  
Игроки выбирают команду **Красных** или **Синих**, сражаются за очки и побеждают, достигнув целевого счёта до окончания таймера.

---

### Возможности
- **Система фракций** — выбор команды при первом входе, сохранение команды после выхода/смерти.
- **Стартовые наборы (киты)** — каждая команда получает собственное оружие, броню, боеприпасы, еду и инструменты.
- **Очки и таймер** — на экране отображаются очки команд и оставшееся время.
- **Точки спавна** — администратор может задавать их и радиус безопасного появления.
- **Ресурсные рудники** — самовосстанавливающиеся блоки руды для добычи ресурсов.
- **Админ-команды** — управление матчем, таймером, очками, friendly-fire, спавнами и рудниками.
- **Командные скины** — игроки автоматически получают RED/BLUE скин в третьем лице.

---

### Команды
- `faction`, `faction reset` — выбор/сброс фракции.
- `spawnpt set <blue|red> [radius]`, `spawnpt show` — установка и просмотр точек спавна.
- `conflict start|stop|reset`, `conflict set target <points>`, `conflict set minutes <time>` — управление матчем.
- `conflict ff on|off|toggle|status` — включить/выключить friendly-fire.
- `mine add [seconds]`, `mine remove` — рудники, которые восстанавливаются через заданное время.

---

### Установка
1. Установите Minecraft Forge 1.20.1 (рекомендуется сборка 47.4.x).
2. Поместите `conflict-x.x.x.jar` в папку `mods/` на сервере и у всех игроков.
3. (Опционально) Установите мод **SuperbWarfare** и другие, если они используются в комплектах экипировки.
4. Запустите сервер и подключайтесь клиентом с тем же набором модов.

---

### Примечания
- Для игры с нелицензионными клиентами установите `online-mode=false` в `server.properties`.
- Все игроки должны использовать одинаковую сборку модов.
- Для честной игры рекомендую минимум 4 игрока.

---

Enjoy the battle and good luck to both teams!  
Приятной игры и удачи обеим командам!

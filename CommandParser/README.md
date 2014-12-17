Updated: 16 DEC 14

CommandParser
=============
This plugin will parse Command Strings sent by the a Chat Parser. This plugin subscribes to `Player_Command_Parsed_From_Client` events.

Authors: Underbalanced

Requirements
============
- StarNub Server
- Packet decoding turned on (StarNub)
- ChatParser Plugin

Installation
============
- Place inside of `StarNub/Plugins/`

Current Features
========
- Command Parsing, will attempt to parse a command, check for a players permissions and execute the command if successful.
    - Plugins can have similar commands I.E plugin1 and plugin2 both have `kick <username> <reason>`, by default the only
    way to use a command is to call a command by using the Plugin's Command Name or Command Alias, such as `/plugin1 kick <username> <reason>` or `/p1 <username> <reason>`
    and `/plugin2 kick <username> <reason>` or `/p2 <username> <reason>`. This can be tedious, so server owners can dictate
    which command has a "shortcut" by defining under `/yaml_files/shortcut.yml`. You use a plugin name and then supply a list
    of commands that are referenced to that plugin such as `{ plugin1: ['kick', 'ban'] }` would then enable the use of `/kick <username> <reason>`
    which then executes the plugin1 kick command, while you can still use plugin2 kick command by using `/plugin2 kick <username> <reason>` or `/p2 <username> <reason>`.
    - Permissions are auto generated `<plugin_name>.<command>.<main_args>` a list of permissions can be found under each plugin that has commands
    folder `StarNub/Plugins/<PluginName>/Information/Commands/<commands>_Details.yml` Each command will have its own file.
    - Wild card commands are supported. Permissions: `commandparser.shortcuts.remove`, `commandparser.shortcuts.get`,`commandparser.shortcuts.reload`, `commandparser.shortcuts.add` like this
    could all be given to a user by giving them `commandparser.shortcut.*` or `commandparser.*`, please use caution when giving wildcards.
    - Some commands may not be able to be used by Players or Remote Players and is set by the command creator based on the commands effects. For example
    you may not want a "Remote Player" who is using a phone application to connect to the server using a warp command for themselves as it would not
    work when they are not directly logged into the game.
    - Commands are split on spaces, if you command needs to have a whole string grouped together then place it in quotes to prevent it from being
    split into arguments, I.E `/kick Underbalanced You are a sore loser` would result in this command being supplied to the command as Command: Kick, Arguments: Underbalanced, You, Are, A, Sore, Loser when it
    really should be `/kick Underbalanced "You are a sore loser"` Command: Kick, Arguments: Underbalanced, You Are A Sore Loser. Some commands
    can register custom split commands, and shall explain particular use in their documents. Such as a whisper command `/w <username> <message>` could be registered to split only on 2 spaces, thus
    breaking it up into `<username>` and `<message>` without the need for quote around the message, however the player name if it has a space would require quotes only.


Planned Features
================
- None

Configuration Variables
========
| Variable             | Description                                                                           |
|---                   |---                                                                                    |
| auto_shortcuts     |  If this is set to true, then any command that has no duplicate command will be save with a shortcut automatically |                           |

Permissions
========
- None

Published Events
========
| Event Class           | Event Key                               | Event Data                                       |
|---              |---                                |---                                               |
| StarNubEventTwo | Player_Command_Failed_Argument_Count           | PlayerSession.class, String.class (Command) |
| StarNubEventTwo | Player_Command_Failed_No_Plugin                | PlayerSession.class, String.class (Command) |
| StarNubEventTwo | Player_Command_Failed_No_Command               | PlayerSession.class, String.class (Command) |
| StarNubEventTwo | Player_Command_Failed_Remote_Player_Cannot_Use | PlayerSession.class, String.class (Command) |
| StarNubEventTwo | Player_Command_Failed_Player_Cannot_Use        | PlayerSession.class, String.class (Command) |
| StarNubEventTwo | Player_Command_Failed_Permissions              | PlayerSession.class, String.class (Command) |
| StarNubEventTwo | Player_Command_Delivered_To_Plugin             | PlayerSession.class, String.class (Command) |

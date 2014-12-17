Updated: 16 DEC 14

ChatParser
=======
This plugin will parse chat messages and send chat or command events that can be handled by a Chat Manager or Command Parser plugin.

Authors: Underbalanced

Requirements
============
- StarNub Server
- Packet decoding turned on (StarNub)

Installation
============
- Place inside of `StarNub/Plugins/`

Current Features
========
- This plugin will take ChatReceive and ChatSent message and recycle them and create events for other plugins to
handle the chat messages without sending them through to the server or client unless routed by a Chat Manager or Command Parser.

Planned Features
================
- None

Configuration Variables:
========
| Variable             | Description                                                                           |
|---                   |---                                                                                    |
| global_chat_rate     |  The chat rate to use if no permission is assigned                                    |
| spam_message_chat    | Message that is sent to the player if they exceed the global or assigned chat rate    |
| global_command_rate  |   The command rate to use if no permission is assigned                                |
| spam_message_command | Message that is sent to the player if they exceed the global or assigned command rate |


Permissions
========
| Permission           | Description                                             |
|---                   |---                                                      |
| commandparser.chat      | Gives permission to send chat messages                  |
| commandparser.chat.#    | A Integer representing the chat rate in milliseconds    |
| commandparser.command   | Gives permission to send commands                       |
| commandparser.command.# | A Integer representing the command rate in milliseconds |


Published Events
========
| Event           | Key                               | Event Data                                       |
|---              |---                                |---                                               |
| StarNubEvent    | Player_Chat_Parsed_From_Server    | ChatReceivePacket.class (Copy)                   |
| StarNubEvent | Player_Chat_Failed_Spam_Client    | PlayerSession.class |
| StarNubEvent | Player_Chat_Failed_No_Permission_Client    | PlayerSession.class |
| StarNubEventTwo | Player_Chat_Parsed_From_Client    | PlayerSession.class, ChatSentPacket.class (Copy) |
| StarNubEvent | Player_Command_Failed_Spam_Client | PlayerSession.class |
| StarNubEvent | Player_Command_Failed_No_Permission_Client | PlayerSession.class |
| StarNubEventTwo | Player_Command_Parsed_From_Client | PlayerSession.class, String.class (Command) |
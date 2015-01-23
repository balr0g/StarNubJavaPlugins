StarNub Java Plugins
===========
Undergoing refactoring. More coming soon.

Project Start Date: 
- April 2014 - Invested time approximately 30-40 hours weekly to learning and coding

Documents can be found here:
- [StarNub](http://starnub.org)
- [StarNub Repo](http://repo.starnub.org)
- [StarNub Main Documents](http://docs.starnub.org/main/)
- [StarNub Plugins Documents](http://docs.starnub.org/main/)
- [Twitch Stream](http://www.twitch.tv/Underbalanced/)


Freenode IRC:
- '#starnub'
- '##starbound-dev'

Teamspeak 3
- ts3.free-universe.com

Pizza Fund - Paypal
admin@free-universe.com

Code contributions are welcome. I have yet to type out specific guidelines. You will probably want to chat with me.


Plugins:
===========
###ChatParser
	- Recycles chat packets
	- Parses the packet and	sends a Chat or Command use StarNub Event to allow plugin handling

###CommandParser
	- Uses ChatParser events to parse a command and check for all permissions and proper command usage
	- Command shortcut system
	- Custom splitter

###ChatManager
	- Manages chat with many optional enhancements 
	- Whisper and Replies
     	- Chat Rooms
	- Ignore Players
	- Mute / Temp Muting
	- Message override
	- Chat Filter
	- Name Filter, Auto name changer
	- Nick name change notification

###Essentials
	- Server Manager (Crash and Responsiveness detection)
	- Auto Restarter (With Notification and world unloading)
	- MOTD (Static, Random, Rotating)
	- Player Join Leave Messages (Unsubscribale)
	- Broadcaster

###NoCheats
	- Prevent players from exceeding Starbounds intended bounds

###Starbound Commands
	- Bridge from StarNubs command system to Starbound
	- Add permissions to Starbound commands
	- Forwards the commands to the server

###StarNub
	- Used for the server wrapper methods


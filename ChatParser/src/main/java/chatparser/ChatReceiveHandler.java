package chatparser;

import starbounddata.packets.Packet;
import starbounddata.packets.chat.ChatReceivePacket;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.packet.PacketEventHandler;

public class ChatReceiveHandler extends PacketEventHandler {

    @Override
    public void onEvent(Packet packet) {
        ChatReceivePacket chatReceivePacket = (ChatReceivePacket) packet;
        ChatReceivePacket chatReceivePacketCopy = chatReceivePacket.copy();
        packet.recycle();
        new StarNubEvent("Player_Chat_Parsed_From_Server", chatReceivePacketCopy);
    }
}

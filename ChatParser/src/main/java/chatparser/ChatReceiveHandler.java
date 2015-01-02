package chatparser;

import starbounddata.packets.Packet;
import starbounddata.packets.chat.ChatReceivePacket;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEventTwo;
import starnubserver.events.packet.PacketEventHandler;

public class ChatReceiveHandler extends PacketEventHandler {

    @Override
    public void onEvent(Packet packet) {
        ChatReceivePacket chatReceivePacket = (ChatReceivePacket) packet;
        ChatReceivePacket chatReceivePacketCopy = chatReceivePacket.copy();
        packet.recycle();
        PlayerSession playerSession = PlayerSession.getPlayerSession(chatReceivePacketCopy);
        new StarNubEventTwo("Player_Chat_Parsed_From_Server", playerSession, chatReceivePacketCopy);
    }
}

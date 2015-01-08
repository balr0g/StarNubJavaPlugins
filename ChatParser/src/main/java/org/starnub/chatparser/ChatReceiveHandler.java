package org.starnub.chatparser;

import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.StarNubEventTwo;
import org.starnub.starnubserver.events.packet.PacketEventHandler;
import starbounddata.packets.Packet;
import starbounddata.packets.chat.ChatReceivePacket;

public class ChatReceiveHandler extends PacketEventHandler {

    @Override
    public void onEvent(Packet packet) {
        ChatReceivePacket chatReceivePacket = (ChatReceivePacket) packet;
        ChatReceivePacket chatReceivePacketCopy = chatReceivePacket.copy();
        packet.recycle();
        chatReceivePacketCopy.setFromName("Starbound");
        PlayerSession playerSession = PlayerSession.getPlayerSession(chatReceivePacketCopy);
        new StarNubEventTwo("Player_Chat_Parsed_From_Server", playerSession, chatReceivePacketCopy);
    }
}

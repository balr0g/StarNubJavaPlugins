package chatmanager.commands;

import chatmanager.ChatManager;
import chatmanager.PlayerManager;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class Whisper extends Command {

        private final PlayerCtxCacheWrapper WHISPER_CACHE;
        MessageSender msgSend = StarNub.getMessageSender();

    public Whisper(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
            this.WHISPER_CACHE = new PlayerCtxCacheWrapper("ChatManager", "ChatManager - Whisper Cache", true, 2, TimeUnit.SECONDS, 0, 0);
    }


}

//////////////////////////////////     OLD CODE /////////////////////////////////////////

        @Override
        public void onCommand(PlayerSession playerSession, String command, String[] args) {
                final ChatManager CHAT_MANAGER = (ChatManager) getPLUGIN();
                final PlayerManager PLAYER_MANAGER = CHAT_MANAGER.getPlayerManager();


                //Check Ignores
                //

        }
if(args.length==0){
        msgSend.playerOrConsoleMessage("StarNub",sender,"You did not provide enough arguments for /w or /r, to whisper use /w {player} {message},"+
        " to reply to who you have whispered to last or received a message from use /r {message}.");
        }

        Player playerSender=(Player)sender;
        ChannelHandlerContext senderCtx=playerSender.getClientCtx();

        if(command.equalsIgnoreCase("r")){

        if(whisperCache.containsKey(senderCtx)){
        String args1;
        try{
        args1=" "+args[1];
        }catch(IndexOutOfBoundsException e){
        args1="";
        }
        StarNub.getMessageSender().playerMessageWhisper(sender,whisperCache.get(senderCtx),args[0]+args1);
        return;
        }else{
        msgSend.playerOrConsoleMessage("StarNub",sender,"You have not whispered or been whispered by anyone.");
        return;
        }
        }

        if(args[0].equalsIgnoreCase("on")&&playerSender.getCharacter().getAccount()!=null){
        playerSender.getCharacter().getAccount().getAccountSettings().setWhisperBlocking(false);
        msgSend.playerOrConsoleMessage("StarNub",sender,"You are now accepting whispers.");
        //EVENTS
        return;
        }else if(args[0].equalsIgnoreCase("off")&&playerSender.getCharacter().getAccount()!=null){
        playerSender.getCharacter().getAccount().getAccountSettings().setWhisperBlocking(true);
        msgSend.playerOrConsoleMessage("StarNub",sender,"You are now blocking whispers.");
        //EVENTS
        return;
        }else if(args[0].equalsIgnoreCase("on")||args[0].equalsIgnoreCase("off")){
        msgSend.playerOrConsoleMessage("StarNub",sender,"You must create an account or add this character to an existing account to block/unblock whispers.");
        return;
        }

//            StarNub.getMessageSender().commandResults(sender, "Only players can block/unblock whispers.", false);
//            return;

        Player playerReceiver=StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(args[0]);
        if(playerReceiver!=null){
        ChannelHandlerContext receiverCtx=playerReceiver.getClientCtx();
        if(command.equalsIgnoreCase("w")||command.equalsIgnoreCase("pm")||command.equalsIgnoreCase("msg")){
        if(whisperCache.containsKey(playerSender.getClientCtx())){
        whisperCache.replace(senderCtx,receiverCtx);
        }else{
        whisperCache.put(senderCtx,receiverCtx);
        }
        if(whisperCache.containsKey(receiverCtx)){
        whisperCache.replace(receiverCtx,senderCtx);
        }else{
        whisperCache.put(receiverCtx,senderCtx);
        }
        }
        }
        StarNub.getMessageSender().playerMessageWhisper(sender,args[0],args[1]);
        }


package org.lpw.clivia.chat;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ChatModel.NAME + ".ctrl")
@Execute(name = "/chat/", key = ChatModel.NAME, code = "160")
public class ChatCtrl {
    @Inject
    private Request request;
    @Inject
    private ChatService chatService;
}
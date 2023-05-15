package org.lpw.clivia.user.inviter;

import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

@Controller(InviterModel.NAME + ".ctrl")
@Execute(name = "/user/inviter/", key = InviterModel.NAME, code = "151")
public class InviterCtrl {
}

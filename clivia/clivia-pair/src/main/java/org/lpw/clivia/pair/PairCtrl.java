package org.lpw.clivia.pair;

import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

@Controller(PairModel.NAME + ".ctrl")
@Execute(name = "/pair/", key = PairModel.NAME, code = "112")
public class PairCtrl {
}
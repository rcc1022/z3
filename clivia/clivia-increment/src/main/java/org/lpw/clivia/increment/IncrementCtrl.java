package org.lpw.clivia.increment;

import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

@Controller(IncrementModel.NAME + ".ctrl")
@Execute(name = "/increment/", key = IncrementModel.NAME, code = "109")
public class IncrementCtrl {
}

package org.lpw.clivia.console;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ConsoleModel.NAME + ".helper")
public class ConsoleHelperImpl implements ConsoleHelper {
    @Inject
    private Validator validator;
    @Inject
    private Request request;

    @Override
    public boolean search() {
        return request.getAsBoolean("console-grid-search");
    }

    @Override
    public String sort(String order) {
        String sort = request.get("console-grid-sort");
        if (validator.isEmpty(sort))
            return order;

        int index = sort.indexOf(' ');
        if (index == -1)
            return order;

        StringBuilder sb = new StringBuilder("c_");
        for (int i = 0; i < index; i++) {
            char ch = sort.charAt(i);
            if (ch >= 'A' && ch <= 'Z')
                sb.append('_').append((char) (ch - 'A' + 'a'));
            else
                sb.append(ch);
        }
        sb.append(' ');
        if (sort.substring(index + 1).equals("descend"))
            sb.append("desc");
        if (!validator.isEmpty(order))
            sb.append(',').append(order);

        return sb.toString();
    }
}

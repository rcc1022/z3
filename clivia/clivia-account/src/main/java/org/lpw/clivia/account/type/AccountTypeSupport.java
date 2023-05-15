package org.lpw.clivia.account.type;

import org.lpw.clivia.account.AccountModel;
import org.lpw.clivia.account.log.LogService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AccountTypeSupport implements AccountType {
    static final String NAME = "clivia.account.type.";

    @Inject
    private LogService logService;
    private final Set<String> ignores = Set.of("user", "owner", "type", "channel", "amount");

    protected String in(AccountModel account, String channel, long amount, Map<String, String> map) {
        account.setPending(account.getPending() + amount);

        return log(account, channel, amount, LogService.State.New, map);
    }

    protected String out(AccountModel account, String channel, long amount, Map<String, String> map) {
        if (account.getBalance() < amount)
            return null;

        account.setBalance(account.getBalance() - amount);
        account.setPending(account.getPending() + amount);

        return log(account, channel, amount, LogService.State.New, map);
    }

    protected String log(AccountModel account, String channel, long amount, LogService.State state, Map<String, String> map) {
        Map<String, String> parameter = new HashMap<>();
        if (map != null) {
            parameter.putAll(map);
            for (String key : ignores)
                parameter.remove(key);
        }

        return logService.create(account, getName(), channel, amount, state, parameter);
    }
}

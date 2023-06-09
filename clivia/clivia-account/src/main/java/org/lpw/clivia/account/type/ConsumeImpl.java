package org.lpw.clivia.account.type;

import org.lpw.clivia.account.AccountModel;
import org.lpw.clivia.account.log.LogModel;
import org.lpw.clivia.account.log.LogService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service(AccountTypeSupport.NAME + AccountTypes.CONSUME)
public class ConsumeImpl extends AccountTypeSupport {
    @Override
    public String getName() {
        return AccountTypes.CONSUME;
    }

    @Override
    public String change(AccountModel account, String channel, long amount, Map<String, String> map) {
        return out(account, channel, amount, map);
    }

    @Override
    public boolean complete(AccountModel account, LogModel log) {
        if (account.getPending() < log.getAmount())
            return false;

        account.setPending(account.getPending() - log.getAmount());
        if (log.getState() == LogService.State.Pass.ordinal())
            account.setConsume(account.getConsume() + log.getAmount());
        else {
            account.setBalance(account.getBalance() + log.getAmount());
            log.setBalance(log.getBalance() + log.getAmount());
        }

        return true;
    }
}

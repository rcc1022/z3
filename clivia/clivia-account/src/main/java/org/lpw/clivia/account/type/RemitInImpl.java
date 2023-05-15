package org.lpw.clivia.account.type;

import org.lpw.clivia.account.AccountModel;
import org.lpw.clivia.account.log.LogModel;
import org.lpw.clivia.account.log.LogService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service(AccountTypeSupport.NAME + AccountTypes.REMIT_IN)
public class RemitInImpl extends AccountTypeSupport {
    @Override
    public String getName() {
        return AccountTypes.REMIT_IN;
    }

    @Override
    public String change(AccountModel account, String channel, long amount, Map<String, String> map) {
        return in(account, channel, amount, map);
    }

    @Override
    public boolean complete(AccountModel account, LogModel log) {
        if (account.getPending() < log.getAmount())
            return false;

        account.setPending(account.getPending() - log.getAmount());
        if (log.getState() == LogService.State.Pass.ordinal()) {
            account.setBalance(account.getBalance() + log.getAmount());
            account.setRemitIn(account.getRemitIn() + log.getAmount());
            log.setBalance(account.getBalance());
        }

        return true;
    }
}
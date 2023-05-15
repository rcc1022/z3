package org.lpw.clivia.async;

import org.lpw.photon.atomic.Closables;
import org.lpw.photon.atomic.Failable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.Callable;

@Service(AsyncModel.NAME + ".callabler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CallablerImpl implements Callabler {
    @Inject
    private Closables closables;
    @Inject
    private Set<Failable> failables;
    private Callable<String> callable;
    private AsyncNotifier asyncNotifier;

    @Override
    public Callabler set(Callable<String> callable, AsyncNotifier asyncNotifier) {
        this.callable = callable;
        this.asyncNotifier = asyncNotifier;

        return this;
    }

    @Override
    public String call() throws Exception {
        try {
            String result = callable.call();
            if (asyncNotifier != null)
                asyncNotifier.success(result);
            closables.close();

            return result;
        } catch (Exception e) {
            if (asyncNotifier != null)
                asyncNotifier.failure();
            failables.forEach(failable -> failable.fail(e));

            throw e;
        }
    }
}

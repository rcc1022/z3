package org.lpw.clivia.customerservice.binding;

import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(BindingModel.NAME + ".service")
public class BindingServiceImpl implements BindingService {
    @Inject
    private DateTime dateTime;
    @Inject
    private BindingDao bindingDao;

    @Override
    public BindingModel find(String user, String type) {
        return bindingDao.find(user, type, 1);
    }

    @Override
    public void save(String user, String type, String customerservice) {
        BindingModel binding = new BindingModel();
        binding.setUser(user);
        binding.setType(type);
        binding.setCustomerservice(customerservice);
        binding.setState(1);
        binding.setTime(dateTime.now());
        bindingDao.save(binding);
    }

    @Override
    public void type(String customerservice, String type) {
        bindingDao.type(customerservice, type);
    }

    @Override
    public void state(String customerservice, int state) {
        bindingDao.state(customerservice, state);
    }

    @Override
    public void delete(String customerservice) {
        bindingDao.delete(customerservice);
    }
}

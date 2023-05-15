package org.lpw.clivia.customerservice;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.customerservice.binding.BindingModel;
import org.lpw.clivia.customerservice.binding.BindingService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(CustomerserviceModel.NAME + ".service")
public class CustomerserviceServiceImpl implements CustomerserviceService {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private BindingService bindingService;
    @Inject
    private CustomerserviceDao customerserviceDao;

    @Override
    public JSONObject query(String type, int state) {
        return customerserviceDao.query(type, state, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject one(String type) {
        String user = userService.id();
        if (validator.isId(user)) {
            BindingModel binding = bindingService.find(user, type);
            if (binding != null) {
                CustomerserviceModel customerservice = customerserviceDao.findById(binding.getCustomerservice());
                if (customerservice != null && customerservice.getState() == 1)
                    return modelHelper.toJson(customerservice);
            }
        }

        List<CustomerserviceModel> list = customerserviceDao.query(type, 1, 0, 0).getList();
        if (list.isEmpty())
            return new JSONObject();

        CustomerserviceModel customerservice = list.get(generator.random(0, list.size() - 1));
        if (validator.isId(user))
            bindingService.save(user, type, customerservice.getId());

        return modelHelper.toJson(customerservice);
    }

    @Override
    public void save(CustomerserviceModel customerservice) {
        CustomerserviceModel model = validator.isId(customerservice.getId()) ? customerserviceDao.findById(customerservice.getId()) : null;
        if (model == null)
            customerservice.setId(null);
        customerserviceDao.save(customerservice);
        bindingService.type(customerservice.getId(), customerservice.getType());
        bindingService.state(customerservice.getId(), customerservice.getState());
    }

    @Override
    public void state(String id, int state) {
        customerserviceDao.state(id, state);
        bindingService.state(id, state);
    }

    @Override
    public void delete(String id) {
        customerserviceDao.delete(id);
        bindingService.delete(id);
    }
}

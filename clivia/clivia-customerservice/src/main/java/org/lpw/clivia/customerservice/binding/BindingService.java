package org.lpw.clivia.customerservice.binding;

public interface BindingService {
    BindingModel find(String user, String type);

    void save(String user, String type, String customerservice);

    void type(String customerservice, String type);

    void state(String customerservice, int state);

    void delete(String customerservice);
}

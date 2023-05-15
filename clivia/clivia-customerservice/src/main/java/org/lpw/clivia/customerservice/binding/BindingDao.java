package org.lpw.clivia.customerservice.binding;

interface BindingDao {
    BindingModel find(String user, String type, int state);

    void save(BindingModel binding);

    void type(String customerservice, String type);

    void state(String customerservice, int state);

    void delete(String customerservice);
}

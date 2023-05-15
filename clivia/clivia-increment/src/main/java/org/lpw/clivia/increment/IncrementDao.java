package org.lpw.clivia.increment;

interface IncrementDao {
    IncrementModel find(String key);

    void save(IncrementModel increment);
}

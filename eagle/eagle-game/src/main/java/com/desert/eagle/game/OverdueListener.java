package com.desert.eagle.game;

import java.sql.Timestamp;

public interface OverdueListener {
    void overdue(Timestamp time);
}

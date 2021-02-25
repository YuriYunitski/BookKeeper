package com.yunitski.book_keeper;

import android.provider.BaseColumns;

public class InputData {
    public static final String DB_NAME = "history";
    public static final int DB_VERSION = 1;


    static class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String DATE = "date";

        public static final String TOTAL_VALUE = "total_value";

        public static final String VALUE = "value";

        public static final String OP_NAME = "op_name";
    }
}

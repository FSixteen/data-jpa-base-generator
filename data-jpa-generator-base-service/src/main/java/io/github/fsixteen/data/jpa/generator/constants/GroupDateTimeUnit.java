package io.github.fsixteen.data.jpa.generator.constants;

import java.util.Calendar;

/**
 * 时间单位.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public enum GroupDateTimeUnit {

    SDAY("yyyyMMdd", "%Y%m%d", Calendar.DAY_OF_MONTH), DAY("yyyy-MM-dd", "%Y-%m-%d", Calendar.DAY_OF_MONTH), SMONTH("yyyyMM", "%Y%m", Calendar.MONTH),
    MONTH("yyyy-MM", "%Y-%m", Calendar.MONTH), YEAR("yyyy", "%Y", Calendar.YEAR);

    private String local;

    private String database;

    private int offset;

    private GroupDateTimeUnit(String local, String database, int offset) {
        this.local = local;
        this.database = database;
        this.offset = offset;
    }

    public String getLocal() {
        return local;
    }

    public String getDatabase() {
        return database;
    }

    public int getOffset() {
        return offset;
    }

}

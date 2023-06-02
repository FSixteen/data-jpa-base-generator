package io.github.fsixteen.data.jpa.generator.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间处理工具.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class DateTimeUtils {

    /**
     * 数字转日期.<br>
     *
     * @param dateNumber 日期时间数字, 示例: 20220101
     * @return Date
     */
    public static final Date int2Date(int dateNumber) {
        int year = dateNumber / 10000;
        int month = (dateNumber - year * 10000) / 100;
        int day = dateNumber - year * 10000 - month * 100;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 获取日期范围.
     *
     * @param beginDate 开始时间(含). The beginning time, inclusive.
     * @param endDate   结束时间(含). The ending time, inclusive.
     * @param format    模板. The pattern describing the date and time format.
     * @param offset    时间步长. The calendar field.
     * @return String[]
     */
    public static final String[] getTimeArrayByTimeRange(final Date beginDate, final Date endDate, final String format, final int offset) {
        // 时间范围错误
        if (beginDate.after(endDate)) {
            return new String[0];
        }
        // 定义集合容器
        final List<String> dateList = new ArrayList<>();
        // 定义格式化模板
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        // 处理第一个时间串
        String firstStr = sdf.format(beginDate);
        Date firstDate = null;
        try {
            firstDate = sdf.parse(firstStr);
        } catch (ParseException e) {
            firstDate = new Date();
        }
        // 仅当格式化前后时间一致时, 方可保留
        if (firstDate.equals(beginDate)) {
            dateList.add(firstStr);
        }
        // 开始处理时间区间
        final Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(beginDate);
        beginCalendar.add(offset, 1);
        while (beginCalendar.getTime().before(endDate) || beginCalendar.getTime().equals(endDate)) {
            dateList.add(sdf.format(beginCalendar.getTime()));
            beginCalendar.add(offset, 1);
        }
        return dateList.toArray(new String[dateList.size()]);
    }

}

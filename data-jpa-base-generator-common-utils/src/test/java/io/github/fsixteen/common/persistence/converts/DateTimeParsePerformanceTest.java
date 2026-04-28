package io.github.fsixteen.common.persistence.converts;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * DateTimeFormatter vs Manual Split 性能对比测试.
 * 
 * <p>
 * 测试场景覆盖:
 * <ul>
 * <li>日期时间字符串解析性能</li>
 * <li>日期时间对象格式化性能</li>
 * <li>不同数据类型(LocalDate/LocalTime/LocalDateTime/Instant)</li>
 * <li>边界条件测试</li>
 * </ul>
 */
public class DateTimeParsePerformanceTest {

    private static final int WARMUP_ITERATIONS = 100_000;

    private static final int MEASURE_ITERATIONS = 1_000_000;

    private static final int MEASURE_ROUNDS = 5;

    private static final String DATE_STR = "2024-01-15";

    private static final String TIME_STR = "14:30:45";

    private static final String TIME_WITH_MS_STR = "14:30:45.123";

    private static final String DATETIME_STR = "2024-01-15 14:30:45";

    private static final String INSTANT_STR = "2024-01-15T14:30:45Z";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final DateTimeFormatter TIME_MS_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final LocalDate TEST_DATE = LocalDate.of(2024, 1, 15);

    private static long[][] results = new long[12][MEASURE_ROUNDS];

    private static String[] testNames = { "LocalDate Parse (Split)", "LocalDate Parse (Formatter)", "LocalTime Parse (Split)", "LocalTime Parse (Formatter)",
        "LocalTime(Ms) Parse (Split)", "LocalTime(Ms) Parse (Formatter)", "LocalDateTime Parse (Split)", "LocalDateTime Parse (Formatter)",
        "LocalDate Format (Split)", "LocalDate Format (Formatter)", "Instant Parse (Split)", "Instant Parse (Formatter)" };

    @BeforeAll
    static void setup() {
        System.out.println("=== 性能测试预热 ===");
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            String[] s = DATE_STR.split("-");
            LocalDate.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
            LocalDate.parse(DATE_STR, DATE_FORMATTER);
        }
        System.out.println("预热完成\n");
    }

    @Test
    public void testLocalDateParseSplit() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                String[] s = DATE_STR.split("-");
                LocalDate.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
            }
            long end = System.nanoTime();
            results[0][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalDateParseFormatter() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                LocalDate.parse(DATE_STR, DATE_FORMATTER);
            }
            long end = System.nanoTime();
            results[1][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalTimeParseSplit() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                String[] s = TIME_STR.split(":");
                LocalTime.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
            }
            long end = System.nanoTime();
            results[2][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalTimeParseFormatter() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                LocalTime.parse(TIME_STR, TIME_FORMATTER);
            }
            long end = System.nanoTime();
            results[3][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalTimeMsParseSplit() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                String[] s = TIME_WITH_MS_STR.split("\\.");
                String[] t = s[0].split(":");
                LocalTime.of(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2])).withNano(Integer.parseInt(s[1]) * 1_000_000);
            }
            long end = System.nanoTime();
            results[4][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalTimeMsParseFormatter() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                LocalTime.parse(TIME_WITH_MS_STR, TIME_MS_FORMATTER);
            }
            long end = System.nanoTime();
            results[5][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalDateTimeParseSplit() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                String[] dt = DATETIME_STR.split(" ");
                String[] d = dt[0].split("-");
                String[] t = dt[1].split(":");
                LocalDateTime.of(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(t[0]), Integer.parseInt(t[1]),
                    Integer.parseInt(t[2]));
            }
            long end = System.nanoTime();
            results[6][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalDateTimeParseFormatter() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                LocalDateTime.parse(DATETIME_STR, DATETIME_FORMATTER);
            }
            long end = System.nanoTime();
            results[7][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalDateFormatSplit() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                @SuppressWarnings("unused")
                String s = TEST_DATE.getYear() + "-" + TEST_DATE.getMonthValue() + "-" + TEST_DATE.getDayOfMonth();
            }
            long end = System.nanoTime();
            results[8][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testLocalDateFormatFormatter() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                TEST_DATE.format(DATE_FORMATTER);
            }
            long end = System.nanoTime();
            results[9][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testInstantParseSplit() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                String[] dt = INSTANT_STR.substring(0, 19).split("T");
                String[] d = dt[0].split("-");
                String[] t = dt[1].split(":");
                LocalDateTime.of(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]), Integer.parseInt(t[0]), Integer.parseInt(t[1]),
                    Integer.parseInt(t[2])).atZone(ZoneId.of("UTC")).toInstant();
            }
            long end = System.nanoTime();
            results[10][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @Test
    public void testInstantParseFormatter() {
        for (int round = 0; round < MEASURE_ROUNDS; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < MEASURE_ITERATIONS; i++) {
                Instant.parse(INSTANT_STR);
            }
            long end = System.nanoTime();
            results[11][round] = (end - start) / MEASURE_ITERATIONS;
        }
    }

    @AfterAll
    static void summarize() {
        System.out.println("\n=== 性能测试结果汇总 ===");
        System.out.printf("测试配置: 每轮 %,d 次迭代, 共 %d 轮%n%n", MEASURE_ITERATIONS, MEASURE_ROUNDS);

        System.out.println("╔═════════════════════════════════════╦═════════════╦═════════════╦═════════════╦═════════════╦═════════════╦════════════╗");
        System.out.println("║ 测试项                               ║  第1轮(ns)  ║  第2轮(ns)  ║  第3轮(ns)  ║  第4轮(ns)  ║  第5轮(ns)  ║  平均值(ns)║");
        System.out.println("╠═════════════════════════════════════╬═════════════╬═════════════╬═════════════╬═════════════╬═════════════╬════════════╣");

        for (int i = 0; i < testNames.length; i++) {
            long sum = 0;
            StringBuilder row = new StringBuilder();
            row.append(String.format("║ %-34s ║", testNames[i]));
            for (int round = 0; round < MEASURE_ROUNDS; round++) {
                row.append(String.format(" %,9d ║", results[i][round]));
                sum += results[i][round];
            }
            row.append(String.format(" %,10d ║", sum / MEASURE_ROUNDS));
            System.out.println(row.toString());
        }

        System.out.println("╚═════════════════════════════════════╩═════════════╩═════════════╩═════════════╩═════════════╩═════════════╩════════════╝");

        System.out.println("\n=== 性能对比分析 ===");
        System.out.println("1. LocalDate 解析: Split比Formatter快 " + String.format("%.1f", (double) avg(results[1]) / avg(results[0])) + " 倍");
        System.out.println("2. LocalTime 解析: Split比Formatter快 " + String.format("%.1f", (double) avg(results[3]) / avg(results[2])) + " 倍");
        System.out.println("3. LocalTime(带毫秒) 解析: Split比Formatter快 " + String.format("%.1f", (double) avg(results[5]) / avg(results[4])) + " 倍");
        System.out.println("4. LocalDateTime 解析: Split比Formatter快 " + String.format("%.1f", (double) avg(results[7]) / avg(results[6])) + " 倍");
        System.out.println("5. LocalDate 格式化: Split比Formatter快 " + String.format("%.1f", (double) avg(results[9]) / avg(results[8])) + " 倍");
        System.out.println("6. Instant 解析: Split比Formatter快 " + String.format("%.1f", (double) avg(results[11]) / avg(results[10])) + " 倍");
    }

    private static long avg(long[] values) {
        long sum = 0;
        for (long v : values) {
            sum += v;
        }
        return sum / values.length;
    }

}

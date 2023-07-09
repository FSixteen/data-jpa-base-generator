package io.github.fsixteen.data.jpa.generator.base.config;

import java.util.function.Supplier;

import org.springframework.data.domain.Sort;

/**
 * 全局配置.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
public class DataJpaGeneratorConfig {

    // 默认排序字段
    private Supplier<Sort> defaultSortColumns;

    public Supplier<Sort> getDefaultSortColumns() {
        return defaultSortColumns;
    }

    public void setDefaultSortColumns(Supplier<Sort> defaultSortColumns) {
        this.defaultSortColumns = defaultSortColumns;
    }

    public DataJpaGeneratorConfig withDefaultSortColumns(Supplier<Sort> defaultSortColumns) {
        this.defaultSortColumns = defaultSortColumns;
        return this;
    }

}
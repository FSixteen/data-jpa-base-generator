module io.github.fsixteen.tableinit {

    requires static java.sql;
    requires static org.slf4j;
    requires static spring.boot;
    requires static spring.beans;
    requires static spring.jdbc;

    exports io.github.fsixteen.table.init;

}
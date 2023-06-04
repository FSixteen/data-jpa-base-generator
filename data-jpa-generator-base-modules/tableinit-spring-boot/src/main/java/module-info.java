module io.github.fsixteen.tableinit {

    requires static java.sql;
    requires transitive org.slf4j;
    requires transitive spring.boot;
    requires transitive spring.beans;
    requires transitive spring.jdbc;

    exports io.github.fsixteen.table.init;

}
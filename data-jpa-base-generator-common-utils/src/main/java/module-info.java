module io.github.fsixteen.common.utils {

    requires java.base;

    requires transitive org.slf4j;

    requires transitive java.persistence;

    requires transitive com.fasterxml.jackson.databind;

    requires transitive com.fasterxml.jackson.datatype.jsr310;

    exports io.github.fsixteen.common.json.serializes;

    exports io.github.fsixteen.common.persistence.converts;

    exports io.github.fsixteen.common.utils;

}
module io.github.fsixteen.common.utils {

    requires java.base;
    requires transitive org.slf4j;
    requires transitive com.fasterxml.jackson.databind;

    exports io.github.fsixteen.common.json.serializes;
    exports io.github.fsixteen.common.utils;

}
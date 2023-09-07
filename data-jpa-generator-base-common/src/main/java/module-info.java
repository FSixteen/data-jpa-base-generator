module io.github.fsixteen.common.utils {

    requires transitive java.logging;
    requires transitive com.fasterxml.jackson.databind;

    exports io.github.fsixteen.common.json.serializes;
    exports io.github.fsixteen.common.utils;

}
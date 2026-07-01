package io.github.fsixteen.data.jpa.base.generator.plugins.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ReadablePropertySupportTest {

    @Test
    public void shouldReadGetterOnlyPropertiesWithoutSetter() {
        GetterOnlyBean bean = new GetterOnlyBean();

        assertEquals("ACTIVE", ReadablePropertySupport.read(bean, "status"));
        assertEquals("nested-value", ReadablePropertySupport.read(bean, "nested"));
        assertEquals("status", ReadablePropertySupport.descriptor(GetterOnlyBean.class, "status").getName());
    }

    private static final class GetterOnlyBean {

        private final String status = "ACTIVE";

        private final String nested = "nested-value";

        @SuppressWarnings("unused")
        public String getStatus() {
            return this.status;
        }

        @SuppressWarnings("unused")
        public String getNested() {
            return this.nested;
        }

    }

}

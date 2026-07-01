package io.github.fsixteen.data.jpa.base.generator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.jpa.BaseDao;

public class BaseDeleteServiceReadablePropertyTest {

    @Test
    public void shouldReadGetterOnlyCompositeIdPropertyThroughBaseDeleteService() {
        TestDeleteService service = new TestDeleteService();
        GetterOnlyCompositeId id = new GetterOnlyCompositeId("TENANT", 7L);

        assertEquals("TENANT", service.readReadableProperty(id, "tenantCode"));
        assertEquals(7L, service.readReadableProperty(id, "userId"));
    }

    @Test
    public void shouldReturnNullWhenReadablePropertyInputsAreNull() {
        TestDeleteService service = new TestDeleteService();

        assertNull(service.readReadableProperty(null, "tenantCode"));
        assertNull(service.readReadableProperty(new GetterOnlyCompositeId("TENANT", 7L), null));
    }

    private static final class TestDeleteService implements BaseDeleteService<TestEntity, Serializable, TestArgs> {

        @Override
        public BaseDao<TestEntity, Serializable> getDao() {
            return null;
        }

    }

    private static final class TestEntity implements IdEntity<Serializable> {

        private Serializable id;

        @Override
        public Serializable getId() {
            return this.id;
        }

        @Override
        public void setId(final Serializable id) {
            this.id = id;
        }

    }

    private static final class TestArgs implements IdEntity<Serializable> {

        private Serializable id;

        @Override
        public Serializable getId() {
            return this.id;
        }

        @Override
        public void setId(final Serializable id) {
            this.id = id;
        }

    }

    private static final class GetterOnlyCompositeId {

        private final String tenantCode;

        private final Long userId;

        private GetterOnlyCompositeId(final String tenantCode, final Long userId) {
            this.tenantCode = tenantCode;
            this.userId = userId;
        }

        public String getTenantCode() {
            return this.tenantCode;
        }

        public Long getUserId() {
            return this.userId;
        }

    }

}

package io.github.fsixteen.data.jpa.generator.base.jpa;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * JPA custom extension of
 * {@link org.springframework.data.jpa.repository.JpaRepository} and
 * {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor}.
 * 
 * @author FSixteen
 * @since 1.0.0
 */
@NoRepositoryBean
public interface BaseDao<T extends Serializable, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}

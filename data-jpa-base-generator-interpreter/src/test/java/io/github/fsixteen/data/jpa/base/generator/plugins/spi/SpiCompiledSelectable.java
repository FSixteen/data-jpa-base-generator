package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
@Selectable
public @interface SpiCompiledSelectable {
}

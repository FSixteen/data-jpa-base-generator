package io.github.fsixteen.data.jpa.base.generator.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.DefaultSecurityParameterNameDiscoverer;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.github.fsixteen.data.jpa.base.generator.security.FsnPreAuthorizeAnnotationProcessor;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class FsnPreAuthorizeAspect {

    private final FsnPreAuthorizeAnnotationProcessor annotationProcessor;

    private final DefaultMethodSecurityExpressionHandler expressionHandler;

    @Autowired
    public FsnPreAuthorizeAspect(FsnPreAuthorizeAnnotationProcessor annotationProcessor) {
        this.annotationProcessor = annotationProcessor;
        this.expressionHandler = new DefaultMethodSecurityExpressionHandler();
        this.expressionHandler.setParameterNameDiscoverer(new DefaultSecurityParameterNameDiscoverer());
    }

    @Around("execution(* io.github.fsixteen.data.jpa.base.generator.controller.*Controller+.*(..))")
    public Object aroundBaseControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopUtils.getTargetClass(target);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();

        if (!annotationProcessor.hasPreAuthorizeConfig(targetClass, methodName)) {
            return joinPoint.proceed();
        }

        String authorityExpression = annotationProcessor.getAuthorityExpression(targetClass, methodName);

        if (!StringUtils.hasText(authorityExpression)) {
            return joinPoint.proceed();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Access Denied: No authentication found");
        }

        SimpleMethodInvocation methodInvocation = new SimpleMethodInvocation(target, method, joinPoint.getArgs());
        EvaluationContext context = expressionHandler.createEvaluationContext(authentication, methodInvocation);

        boolean isAuthorized = Boolean.TRUE
            .equals(expressionHandler.getExpressionParser().parseExpression(authorityExpression).getValue(context, Boolean.class));

        if (!isAuthorized) {
            throw new AccessDeniedException("Access Denied: Authority check failed for '" + authorityExpression + "'");
        }

        return joinPoint.proceed();
    }

}

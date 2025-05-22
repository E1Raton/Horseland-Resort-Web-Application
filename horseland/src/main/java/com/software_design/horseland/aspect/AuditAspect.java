package com.software_design.horseland.aspect;

import com.software_design.horseland.annotation.Auditable;
import com.software_design.horseland.model.AuditLog;
import com.software_design.horseland.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.domain.AuditorAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@AllArgsConstructor
public class AuditAspect {

    private final AuditLogRepository repo;
    private final AuditorAware<String> auditor;

    @Around("@within(auditable) || @annotation(auditable)")
    public Object auditAdvice(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {
        final Auditable effectiveAuditable = (auditable != null)
                ? auditable
                : pjp.getTarget().getClass().getAnnotation(Auditable.class);

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object targetObject = pjp.getTarget();

        EvaluationContext context = new MethodBasedEvaluationContext(
                targetObject, method, pjp.getArgs(), new DefaultParameterNameDiscoverer()
        );

        String resolvedUsername;
        if (effectiveAuditable != null && effectiveAuditable.username().startsWith("#")) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(effectiveAuditable.username());
            resolvedUsername = expression.getValue(context, String.class);
        } else if (effectiveAuditable != null && !effectiveAuditable.username().isEmpty()) {
            resolvedUsername = effectiveAuditable.username();
        } else {
            resolvedUsername = auditor.getCurrentAuditor().orElse("UNKNOWN");
        }

        String operation = (effectiveAuditable != null && !effectiveAuditable.operation().isEmpty())
                ? effectiveAuditable.operation()
                : pjp.getSignature().getName().toUpperCase();

        String entityName = (effectiveAuditable != null && effectiveAuditable.entity() != Void.class)
                ? effectiveAuditable.entity().getSimpleName().toUpperCase()
                : pjp.getTarget().getClass().getSimpleName().toUpperCase();

        LocalDateTime timestamp = LocalDateTime.now();

        try {
            return pjp.proceed();
        } finally {
            AuditLog log = new AuditLog();
            log.setUsername(resolvedUsername);
            log.setOperation(operation);
            log.setEntity(entityName);
            log.setTimestamp(timestamp);
            repo.save(log);
        }
    }
}

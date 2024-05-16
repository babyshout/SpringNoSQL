package kopo.springnosql.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.util.GenericSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@Slf4j
@Aspect
@Component
public class SimpleLogAop {
    // com.aop.controller 이하 패키지의 모든 클래스 이하 모든 메서드에 적용
    @Pointcut("execution(* kopo.springnosql.app..*.*(..))")
    private void cut() {
    }

    // Pointcut 에 의해 필터링된 경로로 들어오는 경우 메서드 호출 전에 적용
    @Before(value = "cut()")
    public void beforeParameterLog(JoinPoint joinPoint) {
        // 메서드 정보 받아오기
        Method method = getMethod(joinPoint);
//        log.info("================== method START name = {} ================", method.getName());

        log.info("================== method START name = {} ================", joinPoint.getSignature());


//        log.info("joinPoint.getKind() : " + joinPoint.getKind());
//        log.info("joinPoint.toLongString()" + joinPoint.toLongString());
//        log.info("joinPoint.toShortString()" + joinPoint.toShortString());
//        log.info("joinPoint.toString()" + joinPoint.toString());
//        log.info("joinPoint.getThis() {}",joinPoint.getThis());
//        log.info("joinPoint.getArgs() {}",joinPoint.getArgs());
//        log.info("joinPoint.getSignature() {}",joinPoint.getSignature());
//        log.info("joinPoint.getTarget() {}",joinPoint.getTarget());
//        log.info("joinPoint.getStaticPart() {}",joinPoint.getStaticPart());

        // 파라미터 받아오기
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            log.info("no Parameter");
        }

        for (Object arg : args) {
            log.info("parameter type = {}", arg.getClass().getSimpleName());
            log.info("parameter value = {}", arg);
        }

//        log.info("================== method START name = {} ================", joinPoint.getSignature());
    }

    @AfterReturning(value = "cut()", returning = "returnObj")
    public void afterReturnLog(JoinPoint joinPoint, Object returnObj){
        // 메서드 정보 받아오기
        Method method = getMethod(joinPoint);
//        log.info("================== method END name = {} ================", joinPoint.getSignature());
//        log.info("================== method END name = {} ================", method.getName());

        if (returnObj == null) {
            return;
        }

        log.info("return type = {}", returnObj.getClass().getSimpleName());
        if (returnObj instanceof Collection<?>) {
            ((Collection<?>) returnObj).parallelStream().limit(5).forEach(value -> log.info("returnObj's value : " + value));
        } else {
        log.info("return value = {}", returnObj);

        }
        log.info("================== method END name = {} ================", joinPoint.getSignature());
//        log.info("================== method END name = {} ================", method.getName());
    }
    private Method getMethod(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private Class<? extends GenericSignature.ClassSignature> getClass(JoinPoint joinPoint) {

        GenericSignature.ClassSignature signature = (GenericSignature.ClassSignature) joinPoint.getSignature();
        return signature.getClass();
    }
}

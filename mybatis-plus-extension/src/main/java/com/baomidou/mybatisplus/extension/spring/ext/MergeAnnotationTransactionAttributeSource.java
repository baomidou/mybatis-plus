package com.baomidou.mybatisplus.extension.spring.ext;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author dyu 2020/12/21 15:34
 */
public class MergeAnnotationTransactionAttributeSource extends AnnotationTransactionAttributeSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeAnnotationTransactionAttributeSource.class);

    public MergeAnnotationTransactionAttributeSource() {
        LOGGER.debug(() -> "MergeAnnotationTransactionAttributeSource constructor init");
    }

    @Override
    @Nullable
    protected TransactionAttribute computeTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        // 方法必须是PUBLIC修饰的
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // 如果方法未实现，则会从目标类获取属性
        // 如果目标类为NULL,方法保持不变
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        // 第一优先级取具体方法上的属性
        TransactionAttribute txAttr = findTransactionAttribute(specificMethod);

        // 第二优先级取具体方法对应类上的属性
        Class<?> declaringClass = specificMethod.getDeclaringClass();
        boolean userLevelMethod = ClassUtils.isUserLevelMethod(method);
        if (userLevelMethod) {
            txAttr = merge(txAttr, findTransactionAttribute(declaringClass));
        }

        // 第三优先级是目标类上的属性
        if (targetClass != null && !targetClass.equals(declaringClass) && userLevelMethod) {
            txAttr = merge(txAttr, findTransactionAttribute(targetClass));
        }

        if (method != specificMethod) {
            // 第四优先级取声明的类或者接口的方法上的属性
            txAttr = merge(txAttr, findTransactionAttribute(method));

            // 第五优先级取声明的类或者接口上的属性
            txAttr = merge(txAttr, findTransactionAttribute(method.getDeclaringClass()));
        }

        return txAttr;
    }

    /**
     * 设置“次要”对象中“主要”对象的空属性和默认属性
     * <p>调用该方法后不要使用参数，存在修改</p>
     */
    @Nullable
    private TransactionAttribute merge(@Nullable TransactionAttribute primaryObj, @Nullable TransactionAttribute secondaryObj) {
        if (primaryObj == null) {
            return secondaryObj;
        }
        if (secondaryObj == null) {
            return primaryObj;
        }

        if (primaryObj instanceof DefaultTransactionAttribute && secondaryObj instanceof DefaultTransactionAttribute) {
            DefaultTransactionAttribute primary = (DefaultTransactionAttribute) primaryObj;
            DefaultTransactionAttribute secondary = (DefaultTransactionAttribute) secondaryObj;

            if (primary.getQualifier() == null || primary.getQualifier().isEmpty()) {
                primary.setQualifier(secondary.getQualifier());
            }
            if (primary.getDescriptor() == null || primary.getDescriptor().isEmpty()) {
                primary.setDescriptor(secondary.getDescriptor());
            }
            if (primary.getName() == null || primary.getName().isEmpty()) {
                primary.setName(secondary.getName());
            }

            // The following properties have default values in DefaultTransactionDefinition;
            // we cannot distinguish here, whether these values have been set explicitly or implicitly;
            // but it seems to be logical to handle default values like empty values.
            if (primary.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED) {
                primary.setPropagationBehavior(secondary.getPropagationBehavior());
            }
            if (primary.getIsolationLevel() == TransactionDefinition.ISOLATION_DEFAULT) {
                primary.setIsolationLevel(secondary.getIsolationLevel());
            }
            if (primary.getTimeout() == TransactionDefinition.TIMEOUT_DEFAULT) {
                primary.setTimeout(secondary.getTimeout());
            }
            if (!primary.isReadOnly()) {
                primary.setReadOnly(secondary.isReadOnly());
            }
        }

        if (primaryObj instanceof RuleBasedTransactionAttribute && secondaryObj instanceof RuleBasedTransactionAttribute) {
            RuleBasedTransactionAttribute primary = (RuleBasedTransactionAttribute) primaryObj;
            RuleBasedTransactionAttribute secondary = (RuleBasedTransactionAttribute) secondaryObj;

            if (primary.getRollbackRules().isEmpty()) {
                primary.setRollbackRules(secondary.getRollbackRules());
            }
        }

        return primaryObj;
    }
}

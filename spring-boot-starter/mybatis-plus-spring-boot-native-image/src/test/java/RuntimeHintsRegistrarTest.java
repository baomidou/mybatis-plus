import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.aot.AotServices;

/**
 * 测试RuntimeHintsRegistrar是否正常生成反射、资源配置
 * 
 * @author xiaochen
 * @since 2026/1/12
 */
public class RuntimeHintsRegistrarTest {

    @Test
    void registrarTest() {
        RuntimeHints hints = new RuntimeHints();
        AotServices.factories().load(RuntimeHintsRegistrar.class).stream()
                .filter(
                        runtimeHintsRegistrar -> runtimeHintsRegistrar.getClass().getName()
                                .startsWith("com.baomidou.mybatisplus.aot"))
                .forEach(registrar -> registrar.registerHints(hints, getClass().getClassLoader()));
        // 配置校验

    }

}

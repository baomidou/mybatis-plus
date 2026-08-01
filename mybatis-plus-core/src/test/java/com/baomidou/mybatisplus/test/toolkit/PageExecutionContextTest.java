package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.PageExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class PageExecutionContextTest {

    @Test
    void processesOnlyTheBoundStatementAndUnwrapsExactlyOnce() {
        Page<String> page = new Page<>(1, 10);
        List<String> records = List.of("one", "two");

        try (PageExecutionContext ignored = PageExecutionContext.open("demo.selectPage", page)) {
            assertThat(PageExecutionContext.processResult("demo.count", records)).isSameAs(records);

            Object processed = PageExecutionContext.processResult("demo.selectPage", records);
            assertThat(processed).isInstanceOf(List.class);
            assertThat((List<?>) processed).hasSize(1);
            assertThat(((List<?>) processed).get(0)).isSameAs(page);
            assertThat(page.getRecords()).containsExactly("one", "two");

            assertThat(PageExecutionContext.processResult("demo.selectPage", processed))
                .isSameAs(processed);
        }
    }

    @Test
    void restoresNestedContextAndRejectsOutOfOrderClose() {
        Page<String> outer = new Page<>();
        Page<String> inner = new Page<>();
        PageExecutionContext outerContext = PageExecutionContext.open("outer", outer);
        PageExecutionContext innerContext = PageExecutionContext.open("inner", inner);

        assertThatIllegalStateException().isThrownBy(outerContext::close);
        innerContext.close();
        assertThat(PageExecutionContext.processResult("outer", List.of("record")))
            .isEqualTo(List.of(outer));
        outerContext.close();
    }
}

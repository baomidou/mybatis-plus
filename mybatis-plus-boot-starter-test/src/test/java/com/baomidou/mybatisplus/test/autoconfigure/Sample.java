package com.baomidou.mybatisplus.test.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author miemie
 * @since 2020-05-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sample {
    private Long id;
    private String name;
}

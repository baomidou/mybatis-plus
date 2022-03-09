package com.baomidou.mybatisplus.test.cache.xml;

import lombok.Data;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2022-03-07
 */
@Data
public class XmlCache implements Serializable {
    private static final long serialVersionUID = 907016853109330217L;
    private Long id;
    private String name;
}

package com.baomidou.mybatisplus.test.pagecache;

import lombok.Data;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Data
public class PageCache implements Serializable {
    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    private String name;
}

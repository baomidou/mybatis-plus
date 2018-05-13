package com.baomidou.mybatisplus.core.test.lambda1;

import java.util.Map;

/**
 * @author ming
 * @Date 2018/5/11
 * 组装sql
 */
public interface SqlSegment1 {

    Map<String, Object> getParamNameValuePairs();

    String getSqlSegment();
}

package com.baomidou.mybatisplus.test.h2.cache;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author nieqiurong 2020/4/27.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomPage<T> extends Page<T> {
    
    private long offset;
    
    public CustomPage() {
    }
    
    public CustomPage(long current, long size) {
        super(current, size);
    }
    
    public CustomPage(long current, long size, long total) {
        super(current, size, total);
    }
    
    public CustomPage(long current, long size, boolean isSearchCount) {
        super(current, size, isSearchCount);
    }
    
    public CustomPage(long current, long size, long total, boolean isSearchCount) {
        super(current, size, total, isSearchCount);
    }
    
    @Override
    public long offset() {
        return offset == 0 ? super.offset() : offset;
    }
    
}

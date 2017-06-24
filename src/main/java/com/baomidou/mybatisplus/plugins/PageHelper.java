package com.baomidou.mybatisplus.plugins;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

/**
 * 分页工具类
 *
 * @author liutao
 * 2017-06-24 上午10:42
 */

public class PageHelper {

    /**
     * 分页本地线程变量
     */
    private static final ThreadLocal<Pagination> pageThl = new ThreadLocal<>();

    /**
     * 获取总条数
     * @return
     */
    public static int getTotal(){
        if (isPageable()) {
            return pageThl.get().getTotal();
        } else {
            throw new MybatisPlusException("当前线程并没有启动分页，请在查询前调用PageHelper.startPage");
        }
    }

    /**
     * 获取分页
     * @return
     */
    public static Pagination getPagination(){
        return pageThl.get();
    }

    /**
     * 设置分页
     * @param page
     */
    public static void setPagination(Pagination page){
        pageThl.set(page);
    }

    /**
     * 启动分页
     * @param current 当前页
     * @param size 页大小
     */
    public static void startPage(int current, int size){
        Pagination page = new Pagination(current, size);
        pageThl.set(page);
    }

    /**
     * 是否存在分页
     * @return
     */
    public static boolean isPageable(){
        return pageThl.get() != null ? true : false;
    }
}

package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author t1zg
 * @since 2021-01-22
 */
class ArrayUtilsTest {

    @Test
    void groupListBySize() {
        group(101,50);
        group(150,50);
    }

    private void group(int size,int branchSize){
        System.out.println("总数:" + size + "\t" + "分组大小:" + branchSize);
        List<String> data = new ArrayList<>();
        for (int i = 0 ; i < size; i++){
            data.add("data_" + i);
        }
        List<String>[] groups = ArrayUtils.groupBySize(data, branchSize);
        System.out.println("分组:" + groups.length);
        System.out.print("分组大小:");
        Arrays.stream(groups).forEach(e -> System.out.print(e.size() + "\t"));
        System.out.println();
    }
}

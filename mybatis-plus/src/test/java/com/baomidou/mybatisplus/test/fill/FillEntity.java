package com.baomidou.mybatisplus.test.fill;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author nieqiurong 2023年9月29日
 */
@Data
@TableName(value = "t_fill")
public class FillEntity {

    private Long id;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String name;

    private transient int seq;

    public FillEntity() {
    }

    public FillEntity(Long id) {
        this.id = id;
    }

    public FillEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    void addSeq(){
        this.seq ++;
    }

}

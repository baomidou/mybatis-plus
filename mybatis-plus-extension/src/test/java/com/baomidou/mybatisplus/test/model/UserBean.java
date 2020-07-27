package com.baomidou.mybatisplus.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nieqiurong 2020/2/28.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBean implements Serializable {
    
    private Long id;
    
    private String name;
    
}

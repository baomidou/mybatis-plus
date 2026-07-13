package com.baomidou.mybatisplus.test.strictcollection;

import lombok.Data;

/**
 * An entity whose type is <em>not</em> compatible with {@link StrictUserEntity}.
 * Used to verify that strict collection type checking detects mismatches.
 *
 * @author nieqiurong
 * @since 3.5.18
 */
@Data
public class StrictWrongUserEntity {

    private Long id;

    private Long groupId;

    private String userName;
}

/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.mysql;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 自定义 Mapper 接口
 * </p>
 *
 * @author hubin
 * @date 2017-03-14
 */
public interface MyBaseMapper<T> extends BaseMapper<T> {

    // 测试自定义 Mapper 接口

    // 这个类  不要放到  mapper 扫描目录，否则会当做真实 表 mapper 扫描异常！！

}

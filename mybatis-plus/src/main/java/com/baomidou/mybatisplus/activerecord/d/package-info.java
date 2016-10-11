/*
 * The MIT License
 *
 * Copyright 2014 redraiment.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * <p>该包提供不同数据库实现的特殊SQL方言。</p>
 * <p>方言采用<code>ServiceLoader</code>机制自动加载，因此需要Java 6及以上版本支持。</p>
 * <p>如果用户需要支持除 SQLite3、HyperSQL、MySQL以及PostgreSQL外的其他数据库，
 * 可自行实现<code>me.zzp.ar.d.Dialect</code>，并添加到<code>META-INF/services/me.zzp.ar.d.Dialect</code>
 * 文件中。</p>
 * 
 * @since 1.0
 */
package com.baomidou.mybatisplus.activerecord.d;
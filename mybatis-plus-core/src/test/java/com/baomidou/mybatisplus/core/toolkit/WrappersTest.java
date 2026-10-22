/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 钉住 {@link Wrappers} 各工厂方法的返回类型、实体透传与空包装器语义。
 *
 * @author TaoYu
 */
class WrappersTest {

    @Test
    void queryFactoriesCreateQueryWrapperAndCarryEntityOrClass() {
        Entity entity = new Entity();
        QueryWrapper<Entity> empty = Wrappers.query();
        assertThat(empty).isExactlyInstanceOf(QueryWrapper.class);
        assertThat(empty.getEntity()).isNull();
        assertThat(empty.getEntityClass()).isNull();

        QueryWrapper<Entity> withEntity = Wrappers.query(entity);
        assertThat(withEntity).isExactlyInstanceOf(QueryWrapper.class);
        assertThat(withEntity.getEntity()).isSameAs(entity);

        QueryWrapper<Entity> withClass = Wrappers.query(Entity.class);
        assertThat(withClass).isExactlyInstanceOf(QueryWrapper.class);
        assertThat(withClass.getEntityClass()).isEqualTo(Entity.class);
    }

    @Test
    void lambdaQueryFactoriesCreateLambdaQueryWrapperAndCarryEntityOrClass() {
        Entity entity = new Entity();
        assertThat(Wrappers.lambdaQuery()).isExactlyInstanceOf(LambdaQueryWrapper.class);
        LambdaQueryWrapper<Entity> withEntity = Wrappers.lambdaQuery(entity);
        assertThat(withEntity).isExactlyInstanceOf(LambdaQueryWrapper.class);
        assertThat(withEntity.getEntity()).isSameAs(entity);
        LambdaQueryWrapper<Entity> withClass = Wrappers.lambdaQuery(Entity.class);
        assertThat(withClass).isExactlyInstanceOf(LambdaQueryWrapper.class);
        assertThat(withClass.getEntityClass()).isEqualTo(Entity.class);
    }

    @Test
    void updateFactoriesCreateUpdateWrapperAndCarryEntity() {
        Entity entity = new Entity();
        assertThat(Wrappers.update()).isExactlyInstanceOf(UpdateWrapper.class);
        UpdateWrapper<Entity> withEntity = Wrappers.update(entity);
        assertThat(withEntity).isExactlyInstanceOf(UpdateWrapper.class);
        assertThat(withEntity.getEntity()).isSameAs(entity);
    }

    @Test
    void lambdaUpdateFactoriesCreateLambdaUpdateWrapperAndCarryEntityOrClass() {
        Entity entity = new Entity();
        assertThat(Wrappers.lambdaUpdate()).isExactlyInstanceOf(LambdaUpdateWrapper.class);
        LambdaUpdateWrapper<Entity> withEntity = Wrappers.lambdaUpdate(entity);
        assertThat(withEntity).isExactlyInstanceOf(LambdaUpdateWrapper.class);
        assertThat(withEntity.getEntity()).isSameAs(entity);
        LambdaUpdateWrapper<Entity> withClass = Wrappers.lambdaUpdate(Entity.class);
        assertThat(withClass).isExactlyInstanceOf(LambdaUpdateWrapper.class);
        assertThat(withClass.getEntityClass()).isEqualTo(Entity.class);
    }

    @Test
    void emptyWrapperIsASharedReadOnlySingleton() {
        QueryWrapper<Entity> first = Wrappers.emptyWrapper();
        QueryWrapper<Entity> second = Wrappers.emptyWrapper();
        assertThat(first).isSameAs(second).isInstanceOf(QueryWrapper.class);

        assertThat(first.getEntity()).isNull();
        assertThat(first.getEntityClass()).isNull();
        assertThat(first.getSqlSelect()).isNull();
        assertThat(first.getExpression()).isNull();
        assertThat(first.getSqlSegment()).isNull();
        assertThat(first.getParamNameValuePairs()).isEmpty();
        assertThat(first.isEmptyOfWhere()).isTrue();
        assertThat(first.isEmptyOfNormal()).isTrue();
        assertThat(first.isEmptyOfEntity()).isTrue();
        assertThat(first.isNonEmptyOfEntity()).isFalse();
    }

    @Test
    void emptyWrapperRejectsMutation() {
        QueryWrapper<Entity> wrapper = Wrappers.emptyWrapper();
        assertThatThrownBy(() -> wrapper.setEntity(new Entity())).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> wrapper.setEntityClass(Entity.class))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> wrapper.last("limit 1")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> wrapper.last(true, "limit 1")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(wrapper::clear).isInstanceOf(UnsupportedOperationException.class);
    }

    private static class Entity {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

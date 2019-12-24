package com.baomidou.mybatisplus.extension.kotlin

import org.junit.jupiter.api.Test

/**
 * 修复：https://github.com/baomidou/mybatis-plus/issues/1986
 *
 * @author hcl
 * Create at 2019/12/24
 */
class FixIssue1986 {

    /**
     * 测试补全代码
     */
    @Test
    internal fun test1986() {
        val wrapper = fillQueryWrapper(OpportunityWebPageQuery())
        var sql = wrapper.sqlSegment
        TODO("需要补全断言代码")
    }

}

/**
 * 用户代码
 */
private fun fillQueryWrapper(query: OpportunityWebPageQuery): KtQueryWrapper<CustomerEntity> {
    val wrapper = KtQueryWrapper(CustomerEntity::class.java)
    wrapper.eq(CustomerEntity::valid, query.valid)
    if (!query.districtId.isNullOrEmpty()) {
        wrapper.eq(CustomerEntity::districtId, query.districtId)
    } else if (!query.cityId.isNullOrEmpty()) {
        wrapper.eq(CustomerEntity::cityId, query.cityId)
    } else if (!query.provinceId.isNullOrEmpty()) {
        wrapper.eq(CustomerEntity::provinceId, query.provinceId)
    } else if (!query.region.isNullOrEmpty() && RegionType.of(query.region!!.toInt())?.areaCodes?.toList()?.isNullOrEmpty() != false) {
        wrapper.`in`(CustomerEntity::provinceId, RegionType.of(query.region!!.toInt())?.areaCodes?.toList())
    }
    wrapper.entity = CustomerEntity()
    if (!query.searchKey.isNullOrEmpty()) {
        wrapper.and { itemWrapper ->
            itemWrapper.like(CustomerEntity::name, query.searchKey).or()
                    .like(CustomerEntity::phone, query.searchKey)
        }
    }
    if (query.opportunityType != 0) {
        wrapper.eq(CustomerEntity::type, query.opportunityType)
    }
    return wrapper
}

// 用户代码模拟补全
class CustomerEntity {
    var valid: String? = null
    var name: String? = null
    var phone: String? = null
    var provinceId: String? = null
    var districtId: String? = null
    var cityId: String? = null
    var type: Int = 0
}

class OpportunityWebPageQuery {
    var valid: String = ""
    var searchKey: String? = ""
    var provinceId: String? = ""
    var districtId: String? = ""
    var cityId: String? = ""
    var region: String? = ""
    var opportunityType: Int = 0
}

object RegionType {
    fun of(toInt: Int): Area? = Area(toInt)
}

class Area(i: Int) {
    var areaCodes: IntArray? = IntArray(i) { it }
}
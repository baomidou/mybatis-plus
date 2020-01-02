package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.extension.initTableInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 修复：https://github.com/baomidou/mybatis-plus/issues/1986
 *
 * @author hcl
 * Create at 2019/12/24
 */
class FixIssue1986 {

    @BeforeEach
    fun setUp() {
        initTableInfo(CustomerEntity::class)
    }

    /**
     * 测试补全代码
     */
    @Test
    fun test1986() {
        val wrapper = fillQueryWrapper(OpportunityWebPageQuery())
        print(wrapper.targetSql)
        // (valid = ? AND district_id = ? AND name LIKE ? OR phone LIKE ? AND (valid = ? AND district_id = ?))
    }

}

/**
 * 用户代码
 */
private fun fillQueryWrapper(query: OpportunityWebPageQuery): KtQueryWrapper<CustomerEntity> {
    return KtQueryWrapper(CustomerEntity::class.java)
        .eq(CustomerEntity::valid, query.valid)
        .eq(!query.districtId.isNullOrEmpty(), CustomerEntity::districtId, query.districtId)
        .eq(!query.cityId.isNullOrEmpty(), CustomerEntity::cityId, query.cityId)
        .eq(!query.provinceId.isNullOrEmpty(), CustomerEntity::provinceId, query.provinceId)
        .`in`(!query.region.isNullOrEmpty() && RegionType.of(query.region!!.toInt())?.areaCodes?.toList()?.isNullOrEmpty() != false,
            CustomerEntity::provinceId, RegionType.of(query.region!!.toInt())?.areaCodes?.toList())
        .and(!query.searchKey.isNullOrEmpty()) { i ->
            i.like(CustomerEntity::name, query.searchKey).or()
                .like(CustomerEntity::phone, query.searchKey)
        }
        .eq(query.opportunityType != 0, CustomerEntity::type, query.opportunityType)
}

// 用户代码模拟补全
class CustomerEntity(
    var valid: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var provinceId: String? = null,
    var districtId: String? = null,
    var cityId: String? = null,
    var type: Int = 0
)

class OpportunityWebPageQuery(
    var valid: String = "123",
    var searchKey: String? = "123",
    var provinceId: String? = "123",
    var districtId: String? = "123",
    var cityId: String? = "123",
    var region: String? = "123",
    var opportunityType: Int = 0
)

object RegionType {
    fun of(toInt: Int): Area? = Area(toInt)
}

class Area(i: Int) {
    var areaCodes: IntArray? = IntArray(i) { it }
}

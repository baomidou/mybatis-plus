package com.baomidou.mybatisplus.test.h2.kotlin

import com.baomidou.mybatisplus.test.h2.KtTestConfig
import com.baomidou.mybatisplus.test.h2.kotlin.entity.KtH2User
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum
import com.baomidou.mybatisplus.test.h2.kotlin.service.KtH2UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Kotlin h2user test
 *
 * @author FlyInWind
 * @since 2020/10/18
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [KtTestConfig::class])
class KtH2UserTest {

    @Autowired
    private lateinit var userService: KtH2UserService


    @Test
    fun testSave() {
        val user = KtH2User()
        user.age = AgeEnum.ONE
        user.name = "Demo"
        userService.save(user)
        assertThat(user.createdDt).isNotNull()
    }

    @Test
    fun testUpdate() {
        val user = KtH2User()
        user.age = AgeEnum.ONE
        user.name = "Demo"
        userService.save(user)
        user.name = "Update"
        userService.updateById(user)
        assertThat(user.lastUpdatedDt).isNotNull()
    }

    @Test
    fun testDelete() {
        val user = KtH2User()
        user.age = AgeEnum.ONE
        user.name = "Delete"
        userService.save(user)
        userService.removeById(user)
        assertThat(userService.getById(user.testId)).isNull()
    }


    @Test
    fun testServiceImplInnerKtChain() {
        var tomcat = userService.ktQuery().eq(KtH2User::name, "Tomcat").one()
        assertThat(tomcat).isNotNull()
        assertThat(userService.ktQuery().like(KtH2User::name, "a").count()).isNotEqualTo(0)

        val users = userService.ktQuery()
            .like(KtH2User::age, AgeEnum.TWO)
            .ne(KtH2User::version, 1)
            .isNull(KtH2User::price)
            .list()
        assertThat(users).isEmpty()


        userService.ktUpdate()
            .set(KtH2User::name, "Tomcat2")
            .eq(KtH2User::name, "Tomcat")
            .update()
        tomcat = userService.ktQuery().eq(KtH2User::name, "Tomcat").one()
        assertThat(tomcat).isNull()
    }

}

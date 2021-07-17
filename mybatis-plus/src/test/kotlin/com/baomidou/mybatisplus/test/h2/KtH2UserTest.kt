package com.baomidou.mybatisplus.test.h2

import com.baomidou.mybatisplus.test.h2.entity.KtH2User
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum
import com.baomidou.mybatisplus.test.h2.service.KtH2UserService
import org.junit.jupiter.api.Assertions
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
@ContextConfiguration(locations = ["classpath:h2/spring-test-h2.xml"])
class KtH2UserTest {
    @Autowired
    private lateinit var userService: KtH2UserService


    @Test
    fun testServiceImplInnerKtChain() {
        var tomcat = userService.ktQuery().eq(KtH2User::name, "Tomcat").one()
        Assertions.assertNotNull(tomcat)
        Assertions.assertNotEquals(0, userService.ktQuery().like(KtH2User::name, "a").count())

        val users = userService.ktQuery()
            .like(KtH2User::age, AgeEnum.TWO)
            .ne(KtH2User::version, 1)
            .isNull(KtH2User::price)
            .list()
        Assertions.assertTrue(users.isEmpty())


        userService.ktUpdate()
            .set(KtH2User::name, "Tomcat2")
            .eq(KtH2User::name, "Tomcat")
            .update()
        tomcat = userService.ktQuery().eq(KtH2User::name, "Tomcat").one()
        Assertions.assertNull(tomcat)
    }

}

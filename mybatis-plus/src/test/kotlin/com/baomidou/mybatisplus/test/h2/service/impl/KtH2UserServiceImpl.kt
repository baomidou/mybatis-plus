package com.baomidou.mybatisplus.test.h2.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.baomidou.mybatisplus.test.h2.entity.KtH2User
import com.baomidou.mybatisplus.test.h2.mapper.KtUserMapper
import com.baomidou.mybatisplus.test.h2.service.KtH2UserService
import org.springframework.stereotype.Service

@Service
class KtH2UserServiceImpl : ServiceImpl<KtUserMapper, KtH2User>(), KtH2UserService {

}

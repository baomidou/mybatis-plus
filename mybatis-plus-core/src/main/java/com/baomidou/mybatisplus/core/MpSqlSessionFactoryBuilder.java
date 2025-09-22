package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.SqlRunnerInjector;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.NetUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.net.InetAddress;

/**
 * 老名字: MybatisSqlSessionFactoryBuilder
 * 重写SqlSessionFactoryBuilder
 *
 * @author nieqiurong 2019/2/23.
 */
public class MpSqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {
    private static final Log log = LogFactory.getLog(MpSqlSessionFactoryBuilder.class);

    @Override
    public SqlSessionFactory build(Configuration configuration) {
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);

        IdentifierGenerator identifierGenerator;
        if (null == globalConfig.getIdentifierGenerator()) {
            GlobalConfig.Sequence sequence = globalConfig.getSequence();
            if (sequence.getWorkerId() != null && sequence.getDatacenterId() != null) {
                identifierGenerator = new DefaultIdentifierGenerator(sequence.getWorkerId(), sequence.getDatacenterId());
            } else {
                NetUtils.NetProperties netProperties = new NetUtils.NetProperties(sequence.getPreferredNetworks(), sequence.getIgnoredInterfaces());
                try {
                    InetAddress inetAddress = new NetUtils(netProperties).findFirstNonLoopbackAddress();
                    identifierGenerator = new DefaultIdentifierGenerator(inetAddress);
                } catch (Exception e) {
                    identifierGenerator = DefaultIdentifierGenerator.getFixedIdentifierGenerator(log);
                }
            }
            globalConfig.setIdentifierGenerator(identifierGenerator);
        } else {
            identifierGenerator = globalConfig.getIdentifierGenerator();
        }
        IdWorker.setIdentifierGenerator(identifierGenerator);

        if (globalConfig.isEnableSqlRunner()) {
            new SqlRunnerInjector().inject(configuration);
        }

        SqlSessionFactory sqlSessionFactory = super.build(configuration);

        // 缓存 sqlSessionFactory
        globalConfig.setSqlSessionFactory(sqlSessionFactory);

        return sqlSessionFactory;
    }
}

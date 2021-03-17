package ru.kulikovskiy.trading.investmantanalysistinkoff.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
    public static final String ACCOUNT = "InvestAnalyze.Account";

    @Bean
    public CacheManager cacheManager(@Qualifier("hazelcastInstance")HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance(hazelcastConfig());
    }

    private Config hazelcastConfig() {
        return new Config()
                .setInstanceName(ACCOUNT);
    }
}

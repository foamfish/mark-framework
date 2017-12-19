package com.mark.framework.configuration;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.net.InetAddress;

@SuppressWarnings("SpringComponentScan")
@Configuration
@ConditionalOnProperty(prefix = "mark-framework.request.elasticsearch", name="enable")
public class ElasticSearchConfiguration {

    private static Logger logger = LoggerFactory.getLogger(ElasticSearchConfiguration.class);
    @Value("${mark-framework.elasticsearch.host}")
    private String host;

    @Value("${mark-framework.elasticsearch.port}")
    private int port;

    @Value("${mark-framework.elasticsearch.cluster.name}")
    private String clusterName;

    @Bean
    public Client client() throws Exception {
        Settings esSettings = Settings.settingsBuilder()
                .put("cluster.name", clusterName)
                .build();

        logger.info("clusterName=" + clusterName);
        logger.info("host=" + host);
        logger.info("port=" + port);
        return TransportClient.builder()
                .settings(esSettings)
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(host), port));
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(client());
    }

}
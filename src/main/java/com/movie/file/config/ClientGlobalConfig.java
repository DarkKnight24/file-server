package com.movie.file.config;

import org.csource.common.IniFileReader;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @作者 ：soul
 * @创建日期 ：Created in 2020/3/1 00:29
 * @描述：
 */
@Configuration
public class ClientGlobalConfig {

    @Value("${connect_timeout}")
    public int g_connect_timeout;
    @Value("${network_timeout}")
    public int g_network_timeout;
    @Value("${charset}")
    public String g_charset;
    @Value("${http.tracker_http_port}")
    public int g_tracker_http_port;
    @Value("${http.anti_steal_token}")
    public boolean g_anti_steal_token;
    @Value("${http.secret_key}")
    public String g_secret_key;
    @Value("${tracker_server}")
    public String[] szTrackerServers;
    public static TrackerGroup g_tracker_group;
    public final int DEFAULT_CONNECT_TIMEOUT = 5;
    public final int DEFAULT_NETWORK_TIMEOUT = 30;

    @Bean
    public void clientGlobalInit() throws MyException {

        if (g_connect_timeout < 0) {
            g_connect_timeout = DEFAULT_CONNECT_TIMEOUT;
        }

        g_connect_timeout *= 1000;
        if (g_network_timeout < 0) {
            g_network_timeout = DEFAULT_NETWORK_TIMEOUT;
        }

        g_network_timeout *= 1000;
        if (g_charset == null || g_charset.length() == 0) {
            g_charset = "ISO8859-1";
        }

        if (szTrackerServers == null) {
            throw new MyException("item \"tracker_server\" in "  + " not found");
        } else {
            InetSocketAddress[] tracker_servers = new InetSocketAddress[szTrackerServers.length];

            for(int i = 0; i < szTrackerServers.length; ++i) {
                String[] parts = szTrackerServers[i].split("\\:", 2);
                if (parts.length != 2) {
                    throw new MyException("the value of item \"tracker_server\" is invalid, the correct format is host:port");
                }

                tracker_servers[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }

            g_tracker_group = new TrackerGroup(tracker_servers);
            if (!g_anti_steal_token) {
                g_secret_key = null;
            }
        }

        setConfig();
    }

    private void setConfig(){
        ClientGlobal.g_anti_steal_token = g_anti_steal_token;
        ClientGlobal.g_tracker_http_port = g_tracker_http_port;
        ClientGlobal.g_tracker_group = g_tracker_group;
        ClientGlobal.g_secret_key = g_secret_key;
        ClientGlobal.g_network_timeout = g_network_timeout;
        ClientGlobal.g_connect_timeout = g_connect_timeout;
        ClientGlobal.g_charset = g_charset;
    }
}

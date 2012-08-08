/*
 * Copyright 2012 Midokura Europe SARL
 */
package com.midokura.midolman.host.guice;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.midokura.config.ConfigProvider;
import com.midokura.midolman.config.ZookeeperConfig;
import com.midokura.midolman.state.Directory;
import com.midokura.midolman.state.ZkConnection;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete Guice module configurator that is used when you launch the
 * HostAgent in standalone mode.
 *
 * @author Mihai Claudiu Toader <mtoader@midokura.com>
 *         Date: 2/9/12
 */
public class StandAloneHostAgentModule extends HostAgentModule {

    private final static Logger log =
            LoggerFactory.getLogger(StandAloneHostAgentModule.class);

    private String configFilePath;

    public StandAloneHostAgentModule(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    @Provides
    @Singleton
    public ZookeeperConfig buildZookeeperConfiguration(ConfigProvider config) {
        return config.getConfig(ZookeeperConfig.class);
    }

    @Provides
    @Singleton
    public ConfigProvider buildConfigProvider() throws ConfigurationException {
        HierarchicalINIConfiguration config =
                new HierarchicalINIConfiguration(configFilePath);

        return ConfigProvider.providerForIniConfig(config);
    }

    @Provides
    @Singleton
    Directory builtRootDirectory(ZookeeperConfig config)
            throws Exception {

        final ZkConnection zkConnection = new ZkConnection(
                config.getZooKeeperHosts(),
                config.getZooKeeperSessionTimeout(),
                null, null);

        log.debug("Opening a ZkConnection");
        zkConnection.open();
        log.debug("Opening of the ZkConnection was successful");

        log.info("Adding shutdownHook");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.warn("In shutdown hook: disconnecting ZK.");
                zkConnection.close();
                log.warn("Exiting. BYE!");
            }
        });

        return zkConnection.getRootDirectory();
    }

}

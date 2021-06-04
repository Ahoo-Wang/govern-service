package me.ahoo.cosky.config;

import com.google.common.base.Strings;
import lombok.var;
import me.ahoo.cosky.core.Consts;

/**
 * @author ahoo wang
 */
public final class ConfigKeyGenerator {

    private ConfigKeyGenerator() {
    }

    private final static String CONFIG_IDX = "cfg_idx";
    private final static String CONFIG_HISTORY_IDX = "cfg_htr_idx";
    private final static String CONFIG_HISTORY = "cfg_htr";
    private final static String CONFIG = "cfg";

    /**
     * {namespace}:{@link #CONFIG_IDX}
     */
    private static final String configIdxKeyFormat = "%s:" + CONFIG_IDX;
    /**
     * {namespace}:{@link #CONFIG_HISTORY_IDX}:{configId}
     */
    private static final String configHistoryIdxKeyFormat = "%s:" + CONFIG_HISTORY_IDX + ":%s";
    /**
     * {namespace}:{@link #CONFIG_HISTORY}:
     */
    private static final String configHistoryKeyPrefixFormat = "%s:" + CONFIG_HISTORY + ":";

    /**
     * {namespace}:{@link #CONFIG_HISTORY}:{configId}:{version}
     */
    private static final String configHistoryKeyFormat = configHistoryKeyPrefixFormat + "%s:%s";
    /**
     * {namespace}:{@link #CONFIG}:
     */
    private static final String configKeyPrefixFormat = "%s:" + CONFIG + ":";

    /**
     * {namespace}:{@link #CONFIG}:{configId}
     */
    private static final String configKeyFormat = configKeyPrefixFormat + "%s";

    /**
     * {namespace}:{@link #CONFIG_IDX}
     *
     * @param namespace namespace
     * @return
     */
    public static String getConfigIdxKey(String namespace) {
        return namespace + ":" + CONFIG_IDX;
    }

    public static String getConfigHistoryIdxKey(String namespace, String configId) {
        return Strings.lenientFormat(configHistoryIdxKeyFormat, namespace, configId);
    }

    public static String getConfigHistoryKey(String namespace, String configId, Integer version) {
        return Strings.lenientFormat(configHistoryKeyFormat, namespace, configId, version);
    }

    public static String getConfigKey(String namespace, String configId) {
        return Strings.lenientFormat(configKeyFormat, namespace, configId);
    }

    public static NamespacedConfigId getConfigIdOfKey(String configKey) {
        var firstSplitIdx = configKey.indexOf(Consts.KEY_SEPARATOR);
        var namespace = configKey.substring(0, firstSplitIdx);
        var configKeyPrefix = Strings.lenientFormat(configKeyPrefixFormat, namespace);
        var configId = configKey.substring(configKeyPrefix.length());
        return NamespacedConfigId.of(namespace, configId);
    }

    public static ConfigVersion getConfigVersionOfHistoryKey(String namespace, String configHistoryKey) {
        var configHistoryKeyPrefix = Strings.lenientFormat(configHistoryKeyPrefixFormat, namespace);
        var configIdWithVersion = configHistoryKey.substring(configHistoryKeyPrefix.length());
        var configIdWithVersionSplit = configIdWithVersion.split(Consts.KEY_SEPARATOR);
        if (configIdWithVersionSplit.length != 2) {
            throw new IllegalArgumentException(Strings.lenientFormat("configHistoryKey:[%s] format error.", configHistoryKey));
        }
        var configVersion = new ConfigVersion();
        configVersion.setConfigId(configIdWithVersionSplit[0]);
        configVersion.setVersion(Integer.parseInt(configIdWithVersionSplit[1]));
        return configVersion;
    }
}

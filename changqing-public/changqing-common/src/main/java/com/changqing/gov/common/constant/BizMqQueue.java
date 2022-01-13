package com.changqing.gov.common.constant;

/**
 * 队列常量
 *
 * @author changqing
 * @date 2020年04月05日15:44:03
 */
public interface BizMqQueue {

    /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_OAUTH = "tenant_ds_fe_oauth";
    /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_AUTHORITY = "tenant_ds_fe_authority";
    /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_MSGS = "tenant_ds_fe_msgs";
    /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_DEMO = "tenant_ds_fe_demo";
    /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_ORDER = "tenant_ds_fe_order";
    /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_FILE = "tenant_ds_fe_file";
    /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_GATEWAY = "tenant_ds_fe_geteway";

    /**
     * 租户数据源 队列 消费者
     */
    String TENANT_DS_QUEUE_BY_OAUTH = "tenant_ds_oauth";
    /**
     * 租户数据源 队列 消费者
     */
    String TENANT_DS_QUEUE_BY_AUTHORITY = "tenant_ds_authority";
    /**
     * 租户数据源 队列 消费者
     */
    String TENANT_DS_QUEUE_BY_DEMO = "tenant_ds_demo";

    /**
     * 租户数据源 队列 消费者
     */
    String TENANT_DS_QUEUE_BY_ORDER = "tenant_ds_order";
    /**
     * 租户数据源 队列 消费者
     */
    String TENANT_DS_QUEUE_BY_FILE = "tenant_ds_file";
    /**
     * 租户数据源 队列 消费者
     */
    String TENANT_DS_QUEUE_BY_MSGS = "tenant_ds_msgs";
    /**
     * 租户数据源 队列 消费者
     */
    String TENANT_DS_QUEUE_BY_GATEWAY = "tenant_ds_gateway";
    /**
     * 租户数据源 队列 esap
     */
    String TENANT_DS_FANOUT_EXCHANGE_SERVER="tenant_ds_fe_server";
    String TENANT_DS_FANOUT_EXCHANGE_LDAP="tenant_ds_fe_ldap";
    String TENANT_DS_QUEUE_BY_ESAP="tenant_ds_esap";
        /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_EECP = "tenant_ds_fe_eecp";
        /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_EDSS = "tenant_ds_fe_edss";
    /**
     * 租户数据源 广播
     */
    String TENANT_DS_FANOUT_EXCHANGE_DASHBOARD = "tenant_ds_fe_dashboard";

    String TENANT_DS_FANOUT_EXCHANGE_GROUP="tenant_ds_fanout_exchange_group";
}

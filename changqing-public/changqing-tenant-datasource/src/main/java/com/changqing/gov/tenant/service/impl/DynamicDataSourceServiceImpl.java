package com.changqing.gov.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.exception.ErrorCreateDataSourceException;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.exception.BizException;
import com.changqing.gov.tenant.dao.InitDatabaseMapper;
import com.changqing.gov.tenant.dto.DataSourcePropertyDTO;
import com.changqing.gov.tenant.enumeration.TenantConnectTypeEnum;
import com.changqing.gov.tenant.service.DataSourceService;
import com.changqing.gov.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.Objects;
import java.util.Set;


/**
 * 数据源管理
 * <p>
 * changqing.database.multiTenantType=DATASOURCE 时，该类才会生效
 *
 * @author changqing
 * @date 2020年03月15日11:35:08
 */
@Service
@ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "multiTenantType", havingValue = "DATASOURCE")
@Slf4j
public class DynamicDataSourceServiceImpl implements DataSourceService {
    private final static String SQL_RESOURCE_PATH = "sqls/%s.sql";

    @Value("${changqing.mysql.database}")
    private String defaultDatabase;
    @Value("${changqing.mysql.driverClassName}")
    private String driverClassName;
    @Value("${changqing.mysql.username}")
    private String username;
    @Value("${changqing.mysql.password}")
    private String password;
    @Value("${changqing.mysql.url}")
    private String url;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private DataSourceCreator druidDataSourceCreator;

    @Autowired
    private DatabaseProperties databaseProperties;
    @Autowired
    private DynamicDataSourceProperties properties;
    @Autowired
    private InitDatabaseMapper initDbMapper;

    /**
     * 关闭链接
     *
     * @param connection
     */
    private static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                log.error("连接关闭错误：", e);
            }
        }
    }

    @Override
    public Set<String> findAll() {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        return ds.getCurrentDataSources().keySet();
    }


    @Override
    public Set<String> remove(String name) {
        // 这里每个服务删除的数据库是自己服务配置的
        String database = new StringBuilder().append(databaseProperties.getTenantDatabasePrefix()).append(StrPool.UNDERSCORE).append(name).toString();
        initDbMapper.dropDatabase(database);

        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(name);
        return ds.getCurrentDataSources().keySet();
    }

    @Override
    public boolean addLocalDynamicRoutingDataSource(String tenant) {
        DataSourceProperty dto = new DataSourceProperty();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setUrl(StrUtil.replace(url, defaultDatabase, databaseProperties.getTenantDatabasePrefix() + StrUtil.UNDERLINE + tenant));
        dto.setDriverClassName(driverClassName);
        dto.setPoolName(tenant);
        addDynamicRoutingDataSource(dto);
        return true;
    }

    @Override
    public Set<String> addDynamicRoutingDataSource(DataSourceProperty dto) {
        DataSource newDataSource = createDataSource(dto);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) this.dataSource;
        ds.addDataSource(dto.getPoolName(), newDataSource);
        return ds.getCurrentDataSources().keySet();
    }

    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        if (dataSourceProperty == null) {
            dataSourceProperty = new DataSourceProperty();
        }
        dataSourceProperty.setSeata(databaseProperties.getIsSeata());
        dataSourceProperty.setDruid(properties.getDruid());
        try {
            return druidDataSourceCreator.createDataSource(dataSourceProperty);
        } catch (ErrorCreateDataSourceException e) {
            log.error("数据源初始化期间出现异常", e);
            throw new BizException("数据源初始化期间出现异常");
        }
    }


    private Connection getTestConnection(DataSourceProperty dataSourceProperty) {
        dataSourceProperty.setSeata(false);
        dataSourceProperty.setDruid(BeanUtil.toBean(properties.getDruid(), DruidConfig.class));
        // 配置获取连接等待超时的时间
        dataSourceProperty.getDruid().setMaxWait(3000);
        // 配置初始化大小、最小、最大
        dataSourceProperty.getDruid().setInitialSize(1);
        dataSourceProperty.getDruid().setMinIdle(1);
        dataSourceProperty.getDruid().setMaxActive(1);
        // 连接错误重试次数
        dataSourceProperty.getDruid().setConnectionErrorRetryAttempts(0);
        // 获取失败后中断
        dataSourceProperty.getDruid().setBreakAfterAcquireFailure(true);

        DataSource testDataSource = null;
        try {
            testDataSource = druidDataSourceCreator.createDataSource(dataSourceProperty);
        } catch (ErrorCreateDataSourceException e) {
            log.error("数据源初始化期间出现异常", e);
            throw new BizException("数据源初始化期间出现异常");
        }

        Connection connection = null;
        try {
            connection = testDataSource.getConnection();
        } catch (Exception ignored) {
        }
        try {
            int timeOut = 5;
            if (null == connection || connection.isClosed() || !connection.isValid(timeOut)) {
                log.info("连接已关闭或无效，请重试获取连接！");
                connection = testDataSource.getConnection();
            }
        } catch (Exception e) {
            log.error("创建连接错误 {}", dataSourceProperty.getUrl());
            throw new RuntimeException("创建连接错误 " + dataSourceProperty.getUrl());
        }
        return connection;
    }

    /**
     * 测试链接
     *
     * @param dataSourceProperty
     * @return
     */
    @Override
    public boolean testConnection(DataSourceProperty dataSourceProperty) {
        Connection connection = null;
        try {
            connection = getTestConnection(dataSourceProperty);
            if (null != connection) {
                return true;
            }
        } catch (Exception e) {
            log.info("获取连接失败:" + e.getMessage());
        } finally {
            closeConnection(connection);
        }
        return false;
    }

    public void initDatabases(String tenant) {
        this.initDbMapper.createDatabase(StrUtil.join(StrUtil.UNDERLINE, databaseProperties.getTenantDatabasePrefix(), tenant));
    }

    @Override
    public boolean initConnect(DataSourcePropertyDTO dto) {
        if (TenantConnectTypeEnum.LOCAL.eq(dto.getType())) {
            // 创建 库
            this.initDatabases(dto.getPoolName());

            dto.setUsername(username);
            dto.setPassword(password);
            dto.setUrl(StrUtil.replace(url, defaultDatabase, databaseProperties.getTenantDatabasePrefix() + StrUtil.UNDERLINE + dto.getPoolName()));
            dto.setDriverClassName(driverClassName);
        }
        // 链接
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dto, dataSourceProperty);
        addDynamicRoutingDataSource(dataSourceProperty);

        ScriptRunner runner = this.getScriptRunner(dataSourceProperty.getPoolName());
        //创建表
        this.initTables(runner);
        //初始化数据
        this.initData(runner,dataSourceProperty.getPoolName());
        return true;
    }

    public void initTables(ScriptRunner runner) {
        try {
            Reader resourceAsReader = Resources.getResourceAsReader(String.format(SQL_RESOURCE_PATH, databaseProperties.getTenantDatabasePrefix()));
            runner.runScript(resourceAsReader);
        } catch (IOException e) {
            log.warn("初始化表失败", e);
        } catch (Exception e) {
            log.error("初始化表失败", e);
            throw new BizException(-1, "初始化表失败");
        }
    }
    public InputStream getJarInputStream(String filePath)
            throws Exception {
        URL url = new URL("jar:"+filePath);
        JarURLConnection jarConnection = (JarURLConnection) url
                .openConnection();
        InputStream in = jarConnection.getInputStream();

        return in;
    }
    /**
     * 角色表
     * 菜单表
     * 资源表
     *
     * @param runner
     */
    public void initData(ScriptRunner runner,String tenant) {
        try {
            String dataScript = databaseProperties.getTenantDatabasePrefix() + "_data";
            File file = Resources.getResourceAsFile(String.format(SQL_RESOURCE_PATH, dataScript));
            log.info("file.getPath()=" + file.getPath());
            // 读取配置文件的内容
            BufferedReader in = new BufferedReader(new InputStreamReader(getJarInputStream(file.getPath()), "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            String content = buffer.toString();
            in.close();
            buffer = null;
            if (content.contains("{tenant}")) {
                content = content.replace("{tenant}", tenant);
                Reader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(content.getBytes("UTF-8")));
                runner.runScript(inputStreamReader);
                inputStreamReader.close();
            } else {
                runner.runScript(Resources.getResourceAsReader(String.format(SQL_RESOURCE_PATH, dataScript)));
            }
        } catch (Exception e) {
            log.error("初始化数据失败", e);
            throw new BizException(-1, "初始化数据失败");
        }
    }
    public void initData(ScriptRunner runner) {
        try {
            String dataScript = databaseProperties.getTenantDatabasePrefix() + "_data";
            runner.runScript(Resources.getResourceAsReader(String.format(SQL_RESOURCE_PATH, dataScript)));
        } catch (Exception e) {
            log.error("初始化数据失败", e);
            throw new BizException(-1, "初始化数据失败");
        }
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public ScriptRunner getScriptRunner(String tenant) {
        try {
            DynamicRoutingDataSource ds = (DynamicRoutingDataSource) this.dataSource;
            DataSource curDataSource = ds.getDataSource(tenant);
            Connection connection = curDataSource.getConnection();
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(false);
            //遇见错误是否停止
            runner.setStopOnError(true);
            /*
             * 按照那种方式执行 方式一：true则获取整个脚本并执行； 方式二：false则按照自定义的分隔符每行执行；
             */
            runner.setSendFullScript(true);

            Resources.setCharset(Charset.forName("UTF8"));

            runner.setFullLineDelimiter(false);
            return runner;
        } catch (Exception ex) {
            throw new BizException(-1, "获取连接失败");
        }
    }

}

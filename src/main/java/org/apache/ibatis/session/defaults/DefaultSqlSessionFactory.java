/*
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session.defaults;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

/**
 *
 * 1. SqlSessionFactory 自己维护了一个{@link #configuration} 对象（包含dataSource对象），
 * {@link #openSessionFromDataSource(ExecutorType, TransactionIsolationLevel, boolean)}} 用于创建sqlSession 对象
 *
 * 2. SqlSession 自己维护了一个connection对象
 *
 * 3. SqlSession 维护 一个 Executor  对象
 *
 * 4. Executor （SimpleExecutor 、 ReuseExecutor 、 BatchExecutor 皆继承 BaseExecutor） 维护了 StatementHandler 对象
 *
 * 5. StatementHandler 用于最终使用Connection（不同驱动Connection对象可能不同） 对象 执行对应SQL
 *
 * 用法：
 *
 *  1. 通过SqlSession.getMapper 方法获取对应MapperProxy对象（Jdk Proxy实现）
 *
 *  2. MapperProxy 通过创建 MapperMethod 去执行对应mapper方法
 *
 *  3. Mapper 通过SqlSession 执行DB请求
 *
 * 缓存：
 *
 *  1. 一级缓存 ：
 *    由BaseExecutor实现 ， 作用域为当前事务（默认打开 ， 且只能通过 mapper内的flushCache 来关闭） , 在事务提交之后会被清除
 *
 *  2. 二级缓存 ：
 *    由CachingExecutor实现（默认关闭 ， 通过mapper内cache标签指定对应mapper下的cache对象） ，
 *    全局级别（Configuration 内 引用 MappedStatement 内的 cache）缓存 ，通过将提交后的查询结果封装至TransactionalCache 实现
 *
 * 插件：
 *  1. 通过 {@link Interceptor} 封装 {@link Plugin}
 *
 *
 * @author Clinton Begin
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

  private final Configuration configuration;

  public DefaultSqlSessionFactory(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public SqlSession openSession() {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
  }

  @Override
  public SqlSession openSession(boolean autoCommit) {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, autoCommit);
  }

  @Override
  public SqlSession openSession(ExecutorType execType) {
    return openSessionFromDataSource(execType, null, false);
  }

  @Override
  public SqlSession openSession(TransactionIsolationLevel level) {
    return openSessionFromDataSource(configuration.getDefaultExecutorType(), level, false);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
    return openSessionFromDataSource(execType, level, false);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
    return openSessionFromDataSource(execType, null, autoCommit);
  }

  @Override
  public SqlSession openSession(Connection connection) {
    return openSessionFromConnection(configuration.getDefaultExecutorType(), connection);
  }

  @Override
  public SqlSession openSession(ExecutorType execType, Connection connection) {
    return openSessionFromConnection(execType, connection);
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {

    // 通过连接池获取sqlSession 对象
    Transaction tx = null;
    try {

      // 通过连接池 & 隔离级别 & 是否自动提交 属性 构建一个 transaction 对象
      final Environment environment = configuration.getEnvironment();
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);

      // transaction 对象通过维护了一个connection对象来支持 rollback & commit 操作
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);


      // 通过transaction & 执行器类型 构建 执行器对象
      final Executor executor = configuration.newExecutor(tx, execType);

      // 通过配置类 & 执行器 & 是否自动提交属性 生成 sqlSession 对象
      return new DefaultSqlSession(configuration, executor, autoCommit);

    } catch (Exception e) {
      // may have fetched a connection so lets call close()
      closeTransaction(tx);

      // 包装异常对象 & 将异常信息封装至 ErrorContext 对象中
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  private SqlSession openSessionFromConnection(ExecutorType execType, Connection connection) {
    try {
      boolean autoCommit;
      try {
        autoCommit = connection.getAutoCommit();
      } catch (SQLException e) {
        // Failover to true, as most poor drivers
        // or databases won't support transactions
        autoCommit = true;
      }
      final Environment environment = configuration.getEnvironment();
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      final Transaction tx = transactionFactory.newTransaction(connection);
      final Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }

  private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
    if (environment == null || environment.getTransactionFactory() == null) {
      return new ManagedTransactionFactory();
    }
    return environment.getTransactionFactory();
  }

  private void closeTransaction(Transaction tx) {
    if (tx != null) {
      try {
        tx.close();
      } catch (SQLException ignore) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }
}

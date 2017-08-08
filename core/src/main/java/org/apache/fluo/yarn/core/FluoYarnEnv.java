/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.fluo.yarn.core;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import com.google.common.base.Preconditions;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

public class FluoYarnEnv {

  private Properties props;
  private String applicationName;
  private String connPropsPath;
  private String logPropsPath;
  private String bundledJarPath = null;
  private YarnConfiguration yarnConfig = null;

  public FluoYarnEnv(String yarnProps, String connProps, String logProps, String appName,
      String jarPath) {
    Objects.requireNonNull(yarnProps);
    Objects.requireNonNull(connProps);
    Objects.requireNonNull(logProps);
    Objects.requireNonNull(appName);
    Objects.requireNonNull(jarPath);
    verifyPath(connProps);
    verifyPath(yarnProps);
    verifyPath(logProps);
    verifyPath(jarPath);
    props = FluoYarnProperties.loadFromFile(yarnProps);
    connPropsPath = connProps;
    logPropsPath = logProps;
    applicationName = appName;
    bundledJarPath = jarPath;

  }

  public String getBundledJarPath() {
    return bundledJarPath;
  }

  public String getBundledJarName() {
    return Paths.get(getBundledJarPath()).getFileName().toString();
  }

  public String getLogPropsPath() {
    return logPropsPath;
  }

  public String getConnPropsPath() {
    return connPropsPath;
  }

  public String getApplicationName() {
    return applicationName;
  }

  private static void verifyPath(String path) {
    File f = new File(path);
    Preconditions.checkState(f.exists());
    Preconditions.checkState(f.canRead());
  }

  public YarnConfiguration getYarnConfiguration() {
    if (yarnConfig == null) {
      yarnConfig = new YarnConfiguration();
      yarnConfig.set("mapreduce.framework.name", "yarn");
      yarnConfig.set("yarn.resourcemanager.hostname", getYarnResourceManager());
    }
    return yarnConfig;
  }

  public String getYarnResourceManager() {
    return props.getProperty(FluoYarnProperties.YARN_RESOURCE_MANAGER_PROP,
        FluoYarnProperties.YARN_RESOUCE_MANAGER_DEFAULT);
  }

  public String getZookeepers() {
    return props.getProperty(FluoYarnProperties.ZOOKEEPERS_PROP,
        FluoYarnProperties.ZOOKEEPERS_DEFAULT);
  }

  public String getAppZookeepers() {
    return getZookeepers() + "/" + applicationName;
  }

  public int getWorkerCores() {
    return Integer.valueOf(props.getProperty(FluoYarnProperties.WORKER_NUM_CORES_PROP,
        FluoYarnProperties.WORKER_NUM_CORES_DEFAULT));
  }

  public int getWorkerInstances() {
    return Integer.valueOf(props.getProperty(FluoYarnProperties.WORKER_INSTANCES_PROP,
        FluoYarnProperties.WORKER_INSTANCES_DEFAULT));
  }

  public int getWorkerMaxMemory() {
    return Integer.valueOf(props.getProperty(FluoYarnProperties.WORKER_MAX_MEMORY_MB_PROP,
        FluoYarnProperties.WORKER_MAX_MEMORY_MB_DEFAULT));
  }

  public int getOracleCores() {
    return Integer.valueOf(props.getProperty(FluoYarnProperties.ORACLE_NUM_CORES_PROP,
        FluoYarnProperties.ORACLE_NUM_CORES_DEFAULT));
  }

  public int getOracleInstances() {
    return Integer.valueOf(props.getProperty(FluoYarnProperties.ORACLE_INSTANCES_PROP,
        FluoYarnProperties.ORACLE_INSTANCES_DEFAULT));
  }

  public int getOracleMaxMemory() {
    return Integer.valueOf(props.getProperty(FluoYarnProperties.ORACLE_MAX_MEMORY_MB_PROP,
        FluoYarnProperties.ORACLE_MAX_MEMORY_MB_DEFAULT));
  }

  public int getTotalInstances() {
    return getOracleInstances() + getWorkerInstances();
  }
}

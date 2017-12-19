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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FluoYarnProperties {

  private static final String YARN_PREFIX = "fluo.yarn";

  // General properties
  public static final String YARN_RESOURCE_MANAGER_PROP = YARN_PREFIX + ".resource.manager";
  public static final String ZOOKEEPERS_PROP = YARN_PREFIX + ".zookeepers";
  public static final String DFS_ROOT_PROP = YARN_PREFIX + ".dfs.root";
  public static final String YARN_RESOURCE_MANAGER_DEFAULT = "localhost";
  public static final String ZOOKEEPERS_DEFAULT = "localhost/fluo-yarn";
  public static final String DFS_ROOT_DEFAULT = "hdfs://localhost:8020/";

  // Worker properties
  public static final String WORKER_INSTANCES_PROP = YARN_PREFIX + ".worker.instances";
  public static final String WORKER_MAX_MEMORY_MB_PROP = YARN_PREFIX + ".worker.max.memory.mb";
  public static final String WORKER_NUM_CORES_PROP = YARN_PREFIX + ".worker.num.cores";
  public static final String WORKER_INSTANCES_DEFAULT = "1";
  public static final String WORKER_MAX_MEMORY_MB_DEFAULT = "1024";
  public static final String WORKER_NUM_CORES_DEFAULT = "1";

  // Oracle properties
  public static final String ORACLE_INSTANCES_PROP = YARN_PREFIX + ".oracle.instances";
  public static final String ORACLE_MAX_MEMORY_MB_PROP = YARN_PREFIX + ".oracle.max.memory.mb";
  public static final String ORACLE_NUM_CORES_PROP = YARN_PREFIX + ".oracle.num.cores";
  public static final String ORACLE_INSTANCES_DEFAULT = "1";
  public static final String ORACLE_MAX_MEMORY_MB_DEFAULT = "512";
  public static final String ORACLE_NUM_CORES_DEFAULT = "1";

  public static Properties loadFromFile(String propsFilePath) {
    try {
      return loadFromStream(new FileInputStream(propsFilePath));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static Properties loadFromStream(FileInputStream fis) {
    Properties props = new Properties();
    try {
      props.load(fis);
      fis.close();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return props;
  }
}

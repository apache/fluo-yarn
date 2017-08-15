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
import java.util.Collection;

import org.apache.twill.api.ResourceReport;
import org.apache.twill.api.ResourceSpecification;
import org.apache.twill.api.TwillApplication;
import org.apache.twill.api.TwillController;
import org.apache.twill.api.TwillPreparer;
import org.apache.twill.api.TwillRunResources;
import org.apache.twill.api.TwillRunnerService;
import org.apache.twill.api.TwillSpecification;
import org.apache.twill.ext.BundledJarRunnable;
import org.apache.twill.ext.BundledJarRunner;
import org.apache.twill.yarn.YarnTwillRunnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluoYarnLauncher {

  private static final Logger log = LoggerFactory.getLogger(FluoYarnLauncher.class);

  private static final String ORACLE_ID = "Oracle";
  private static final String WORKER_ID = "Worker";
  private static final String CONN_PROPS = "fluo-conn.properties";
  private static final String LOG4J_PROPS = "log4j.properties";

  private static class FluoYarnApp implements TwillApplication {

    private FluoYarnEnv env;

    FluoYarnApp(FluoYarnEnv env) {
      this.env = env;
    }

    @Override
    public TwillSpecification configure() {
      ResourceSpecification oracleResources =
          ResourceSpecification.Builder.with().setVirtualCores(env.getOracleCores())
              .setMemory(env.getOracleMaxMemory(), ResourceSpecification.SizeUnit.MEGA)
              .setInstances(env.getOracleInstances()).build();

      ResourceSpecification workerResources =
          ResourceSpecification.Builder.with().setVirtualCores(env.getWorkerCores())
              .setMemory(env.getWorkerMaxMemory(), ResourceSpecification.SizeUnit.MEGA)
              .setInstances(env.getWorkerInstances()).build();

      return TwillSpecification.Builder.with().setName("fluo-app-" + env.getApplicationName())
          .withRunnable().add(ORACLE_ID, new BundledJarRunnable(), oracleResources)
          .withLocalFiles().add(env.getBundledJarName(), new File(env.getBundledJarPath()), false)
          .add(CONN_PROPS, new File(env.getConnPropsPath()), false)
          .add(LOG4J_PROPS, new File(env.getLogPropsPath()), false).apply()
          .add(WORKER_ID, new BundledJarRunnable(), workerResources).withLocalFiles()
          .add(env.getBundledJarName(), new File(env.getBundledJarPath()), false)
          .add(CONN_PROPS, new File(env.getConnPropsPath()), false)
          .add(LOG4J_PROPS, new File(env.getLogPropsPath()), false).apply().anyOrder().build();
    }
  }

  private static int getNumRunning(TwillController controller) {
    ResourceReport report = controller.getResourceReport();
    if (report == null) {
      return 0;
    }
    int total = 0;
    Collection<TwillRunResources> resources = report.getRunnableResources(ORACLE_ID);
    if (resources != null) {
      total += resources.size();
    }
    resources = report.getRunnableResources(WORKER_ID);
    if (resources != null) {
      total += resources.size();
    }
    return total;
  }

  public static void main(String[] args) throws Exception {

    if (args.length != 5) {
      System.err.println("Invalid arguments");
      System.exit(-1);
    }

    String connProps = args[0];
    String yarnProps = args[1];
    String logProps = args[2];
    String appName = args[3];
    String jarPath = args[4];

    FluoYarnEnv env = new FluoYarnEnv(yarnProps, connProps, logProps, appName, jarPath);

    BundledJarRunner.Arguments oracleArgs =
        new BundledJarRunner.Arguments.Builder().setJarFileName(env.getBundledJarName())
            .setLibFolder("lib").setMainClassName("org.apache.fluo.command.FluoOracle")
            .setMainArgs(new String[] {CONN_PROPS, appName}).createArguments();

    BundledJarRunner.Arguments workerArgs =
        new BundledJarRunner.Arguments.Builder().setJarFileName(env.getBundledJarName())
            .setLibFolder("lib").setMainClassName("org.apache.fluo.command.FluoWorker")
            .setMainArgs(new String[] {CONN_PROPS, appName}).createArguments();

    TwillRunnerService twillRunner =
        new YarnTwillRunnerService(env.getYarnConfiguration(), env.getZookeepers());
    twillRunner.start();

    TwillPreparer preparer =
        twillRunner.prepare(new FluoYarnApp(env))
            .addJVMOptions("-Dlog4j.configuration=file:$PWD/" + LOG4J_PROPS)
            .withArguments(ORACLE_ID, oracleArgs.toArray())
            .withArguments(WORKER_ID, workerArgs.toArray());

    TwillController controller = preparer.start();

    ResourceReport report = controller.getResourceReport();
    log.info("Waiting for Fluo application '{}' to start in YARN...", appName);
    while (report == null) {
      Thread.sleep(500);
      report = controller.getResourceReport();
    }
    String appID = report.getApplicationId();
    log.info("Fluo application '{}' has started in YARN with ID '{}'", appName, appID);

    log.info("Waiting for all containers of Fluo application '{}' to start in YARN...", appName);
    int numRunning = getNumRunning(controller);
    while (numRunning != env.getTotalInstances()) {
      log.info("{} of {} containers have started in YARN", numRunning, env.getTotalInstances());
      Thread.sleep(2000);
      numRunning = getNumRunning(controller);
    }
    log.info("{} of {} containers have started in YARN", numRunning, env.getTotalInstances());
    log.info("Fluo application '{}' has successfully started in YARN with ID '{}'", appName, appID);
  }
}

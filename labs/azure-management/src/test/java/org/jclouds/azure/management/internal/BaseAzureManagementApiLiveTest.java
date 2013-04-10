/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.azure.management.internal;

import java.util.Properties;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.AzureManagementApiMetadata;
import org.jclouds.azure.management.AzureManagementAsyncApi;
import org.jclouds.azure.management.config.AzureManagementProperties;
import org.jclouds.rest.RestContext;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
public class BaseAzureManagementApiLiveTest extends
         BaseContextLiveTest<RestContext<AzureManagementApi, AzureManagementAsyncApi>> {

   protected String subscriptionId;

   public BaseAzureManagementApiLiveTest() {
      provider = "azure-management";
   }
   
   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      subscriptionId = setIfTestSystemPropertyPresent(props, AzureManagementProperties.SUBSCRIPTION_ID);
      return props;
   }

   @Override
   protected TypeToken<RestContext<AzureManagementApi, AzureManagementAsyncApi>> contextType() {
      return AzureManagementApiMetadata.CONTEXT_TOKEN;
   }

}
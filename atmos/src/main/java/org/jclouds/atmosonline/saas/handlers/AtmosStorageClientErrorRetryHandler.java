/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.atmosonline.saas.handlers;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.atmosonline.saas.domain.AtmosStorageError;
import org.jclouds.atmosonline.saas.util.AtmosStorageUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * Handles Retryable responses with error codes in the 4xx range
 * 
 * @author Adrian Cole
 */
public class AtmosStorageClientErrorRetryHandler implements HttpRetryHandler {
   private final AtmosStorageUtils utils;
   private final BackoffLimitedRetryHandler backoffHandler;

   @Inject
   public AtmosStorageClientErrorRetryHandler(BackoffLimitedRetryHandler backoffHandler,
            AtmosStorageUtils utils) {
      this.backoffHandler = backoffHandler;
      this.utils = utils;
   }

   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   private int retryCountLimit = 5;
   @Resource
   protected Logger logger = Logger.NULL;

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (command.getFailureCount() > retryCountLimit)
         return false;
      if (response.getStatusCode() == 404 && command.getRequest().getMethod().equals("DELETE")) {
         command.incrementFailureCount();
         return true;
      } else if (response.getStatusCode() == 409 || response.getStatusCode() == 400) {
         byte[] content = HttpUtils.closeClientButKeepContentStream(response);
         // Content can be null in the case of HEAD requests
         if (content != null) {
            try {
               AtmosStorageError error = utils.parseAtmosStorageErrorFromContent(command, response,
                        new String(content));
               if (error.getCode() == 1016) {
                  return backoffHandler.shouldRetryRequest(command, response);
               }
               // don't increment count before here, since backoff handler does already
               command.incrementFailureCount();
            } catch (HttpException e) {
               logger.warn(e, "error parsing response: %s", new String(content));
            }
         } else {
            command.incrementFailureCount();
         }
         return true;
      }
      return false;
   }

}

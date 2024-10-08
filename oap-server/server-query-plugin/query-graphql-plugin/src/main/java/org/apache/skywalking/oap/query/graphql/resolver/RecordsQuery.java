/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.query.graphql.resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.apache.skywalking.oap.server.core.CoreModule;
import org.apache.skywalking.oap.server.core.query.RecordQueryService;
import org.apache.skywalking.oap.server.core.query.input.Duration;
import org.apache.skywalking.oap.server.core.query.input.RecordCondition;
import org.apache.skywalking.oap.server.core.query.type.Record;
import org.apache.skywalking.oap.server.library.module.ModuleManager;
import java.util.List;

import static org.apache.skywalking.oap.query.graphql.AsyncQueryUtils.queryAsync;

public class RecordsQuery implements GraphQLQueryResolver {
    private ModuleManager moduleManager;
    private RecordQueryService recordQueryService;

    public RecordsQuery(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    private RecordQueryService getRecordQueryService() {
        if (recordQueryService == null) {
            recordQueryService = moduleManager.find(CoreModule.NAME)
                .provider()
                .getService(RecordQueryService.class);
        }
        return recordQueryService;
    }

    public CompletableFuture<List<Record>> readRecords(RecordCondition condition, Duration duration) {
        return queryAsync(() -> {
            if (!condition.senseScope() || !condition.getParentEntity().isValid()) {
                return Collections.emptyList();
            }
            return getRecordQueryService().readRecords(condition, duration);
        });
    }
}

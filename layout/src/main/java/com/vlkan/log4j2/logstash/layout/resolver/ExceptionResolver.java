/*
 * Copyright 2017-2020 Volkan Yazıcı
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permits and
 * limitations under the License.
 */

package com.vlkan.log4j2.logstash.layout.resolver;

import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.LogEvent;

import java.io.IOException;

class ExceptionResolver implements EventResolver {

    private static final ExceptionInternalResolverFactory INTERNAL_RESOLVER_FACTORY =
            new ExceptionInternalResolverFactory() {

                @Override
                EventResolver createClassNameResolver() {
                    return (logEvent, jsonGenerator) -> {
                        Throwable exception = logEvent.getThrown();
                        if (exception == null) {
                            jsonGenerator.writeNull();
                        } else {
                            String exceptionClassName = exception.getClass().getCanonicalName();
                            jsonGenerator.writeString(exceptionClassName);
                        }
                    };
                }

                @Override
                EventResolver createMessageResolver(EventResolverContext context) {
                    return (logEvent, jsonGenerator) -> {
                        Throwable exception = logEvent.getThrown();
                        if (exception != null) {
                            String exceptionMessage = exception.getMessage();
                            boolean exceptionMessageExcluded = context.isEmptyPropertyExclusionEnabled() && StringUtils.isEmpty(exceptionMessage);
                            if (!exceptionMessageExcluded) {
                                jsonGenerator.writeString(exceptionMessage);
                                return;
                            }
                        }
                        jsonGenerator.writeNull();
                    };
                }

                @Override
                EventResolver createStackTraceTextResolver(EventResolverContext context) {
                    StackTraceTextResolver stackTraceTextResolver = new StackTraceTextResolver(context.getWriterCapacity());
                    return (logEvent, jsonGenerator) -> {
                        Throwable exception = logEvent.getThrown();
                        if (exception == null) {
                            jsonGenerator.writeNull();
                        } else {
                            stackTraceTextResolver.resolve(exception, jsonGenerator);
                        }
                    };
                }

                @Override
                EventResolver createStackTraceObjectResolver(EventResolverContext context) {
                    return (logEvent, jsonGenerator) -> {
                        Throwable exception = logEvent.getThrown();
                        if (exception == null) {
                            jsonGenerator.writeNull();
                        } else {
                            context.getStackTraceObjectResolver().resolve(exception, jsonGenerator);
                        }
                    };
                }

            };

    private final EventResolver internalResolver;

    ExceptionResolver(EventResolverContext context, String key) {
        this.internalResolver = INTERNAL_RESOLVER_FACTORY.createInternalResolver(context, key);
    }

    static String getName() {
        return "exception";
    }

    @Override
    public void resolve(LogEvent logEvent, JsonGenerator jsonGenerator) throws IOException {
        internalResolver.resolve(logEvent, jsonGenerator);
    }

}

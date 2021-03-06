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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class EventResolverContext implements TemplateResolverContext<LogEvent, EventResolverContext> {

    private final ObjectMapper objectMapper;

    private final StrSubstitutor substitutor;

    private final int writerCapacity;

    private final TimeZone timeZone;

    private final Locale locale;

    private final FastDateFormat timestampFormat;

    private final boolean locationInfoEnabled;

    private final boolean stackTraceEnabled;

    private final TemplateResolver<Throwable> stackTraceObjectResolver;

    private final boolean emptyPropertyExclusionEnabled;

    private final Pattern mdcKeyPattern;

    private final Pattern ndcPattern;

    private final KeyValuePair[] additionalFields;

    private final boolean mapMessageFormatterIgnored;

    private EventResolverContext(Builder builder) {
        this.objectMapper = builder.objectMapper;
        this.substitutor = builder.substitutor;
        this.writerCapacity = builder.writerCapacity;
        this.timeZone = builder.timeZone;
        this.locale = builder.locale;
        this.timestampFormat = builder.timestampFormat;
        this.locationInfoEnabled = builder.locationInfoEnabled;
        this.stackTraceEnabled = builder.stackTraceEnabled;
        this.stackTraceObjectResolver = stackTraceEnabled
                ? new StackTraceObjectResolver(builder.stackTraceElementObjectResolver)
                : null;
        this.emptyPropertyExclusionEnabled = builder.emptyPropertyExclusionEnabled;
        this.mdcKeyPattern = builder.mdcKeyPattern == null ? null : Pattern.compile(builder.mdcKeyPattern);
        this.ndcPattern = builder.ndcPattern == null ? null : Pattern.compile(builder.ndcPattern);
        this.additionalFields = builder.additionalFields;
        this.mapMessageFormatterIgnored = builder.mapMessageFormatterIgnored;
    }

    @Override
    public Class<EventResolverContext> getContextClass() {
        return EventResolverContext.class;
    }

    @Override
    public Map<String, TemplateResolverFactory<LogEvent, EventResolverContext, ? extends TemplateResolver<LogEvent>>> getResolverFactoryByName() {
        return EventResolverFactories.getResolverFactoryByName();
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public StrSubstitutor getSubstitutor() {
        return substitutor;
    }

    int getWriterCapacity() {
        return writerCapacity;
    }

    TimeZone getTimeZone() {
        return timeZone;
    }

    Locale getLocale() {
        return locale;
    }

    FastDateFormat getTimestampFormat() {
        return timestampFormat;
    }

    boolean isLocationInfoEnabled() {
        return locationInfoEnabled;
    }

    boolean isStackTraceEnabled() {
        return stackTraceEnabled;
    }

    TemplateResolver<Throwable> getStackTraceObjectResolver() {
        return stackTraceObjectResolver;
    }

    @Override
    public boolean isEmptyPropertyExclusionEnabled() {
        return emptyPropertyExclusionEnabled;
    }

    Pattern getMdcKeyPattern() {
        return mdcKeyPattern;
    }

    Pattern getNdcPattern() {
        return ndcPattern;
    }

    KeyValuePair[] getAdditionalFields() {
        return additionalFields;
    }

    boolean isMapMessageFormatterIgnored() {
        return mapMessageFormatterIgnored;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private ObjectMapper objectMapper;

        private StrSubstitutor substitutor;

        private int writerCapacity;

        private TimeZone timeZone;

        private Locale locale;

        private FastDateFormat timestampFormat;

        private boolean locationInfoEnabled;

        private boolean stackTraceEnabled;

        private TemplateResolver<StackTraceElement> stackTraceElementObjectResolver;

        private boolean emptyPropertyExclusionEnabled;

        private String mdcKeyPattern;

        private String ndcPattern;

        private KeyValuePair[] additionalFields;

        private boolean mapMessageFormatterIgnored;

        private Builder() {
            // Do nothing.
        }

        public Builder setObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder setSubstitutor(StrSubstitutor substitutor) {
            this.substitutor = substitutor;
            return this;
        }

        public Builder setWriterCapacity(int writerCapacity) {
            this.writerCapacity = writerCapacity;
            return this;
        }

        public Builder setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder setTimestampFormat(FastDateFormat timestampFormat) {
            this.timestampFormat = timestampFormat;
            return this;
        }

        public Builder setLocationInfoEnabled(boolean locationInfoEnabled) {
            this.locationInfoEnabled = locationInfoEnabled;
            return this;
        }

        public Builder setStackTraceEnabled(boolean stackTraceEnabled) {
            this.stackTraceEnabled = stackTraceEnabled;
            return this;
        }

        public Builder setStackTraceElementObjectResolver(TemplateResolver<StackTraceElement> stackTraceElementObjectResolver) {
            this.stackTraceElementObjectResolver = stackTraceElementObjectResolver;
            return this;
        }

        public Builder setEmptyPropertyExclusionEnabled(boolean emptyPropertyExclusionEnabled) {
            this.emptyPropertyExclusionEnabled = emptyPropertyExclusionEnabled;
            return this;
        }

        public Builder setMdcKeyPattern(String mdcKeyPattern) {
            this.mdcKeyPattern = mdcKeyPattern;
            return this;
        }

        public Builder setNdcPattern(String ndcPattern) {
            this.ndcPattern = ndcPattern;
            return this;
        }

        public Builder setAdditionalFields(KeyValuePair[] additionalFields) {
            this.additionalFields = additionalFields;
            return this;
        }

        public Builder setMapMessageFormatterIgnored(boolean mapMessageFormatterIgnored) {
            this.mapMessageFormatterIgnored = mapMessageFormatterIgnored;
            return this;
        }

        public EventResolverContext build() {
            validate();
            return new EventResolverContext(this);
        }

        private void validate() {
            Validate.notNull(objectMapper, "objectMapper");
            Validate.notNull(substitutor, "substitutor");
            Validate.isTrue(writerCapacity > 0, "writerCapacity requires a non-zero positive integer");
            Validate.notNull(timeZone, "timeZone");
            Validate.notNull(locale, "locale");
            Validate.notNull(timestampFormat, "timestampFormat");
            if (stackTraceEnabled) {
                Validate.notNull(stackTraceElementObjectResolver, "stackTraceElementObjectResolver");
            }
        }

    }

}

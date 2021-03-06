/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.wavefront;

import java.time.Duration;

import com.wavefront.sdk.common.WavefrontSender;
import com.wavefront.sdk.common.clients.WavefrontClient.Builder;

import org.springframework.boot.actuate.autoconfigure.metrics.export.wavefront.WavefrontMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.tracing.wavefront.WavefrontTracingAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Wavefront common infrastructure.
 * Metrics are auto-configured in {@link WavefrontMetricsExportAutoConfiguration}, and
 * tracing is auto-configured in {@link WavefrontTracingAutoConfiguration}.
 *
 * @author Moritz Halbritter
 * @since 3.0.0
 */
@AutoConfiguration
@ConditionalOnClass(WavefrontSender.class)
@EnableConfigurationProperties(WavefrontProperties.class)
public class WavefrontAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public WavefrontSender wavefrontSender(WavefrontProperties properties) {
		Builder builder = new Builder(properties.getEffectiveUri().toString(), properties.getApiTokenOrThrow());
		PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		WavefrontProperties.Sender sender = properties.getSender();
		mapper.from(sender.getMaxQueueSize()).to(builder::maxQueueSize);
		mapper.from(sender.getFlushInterval()).asInt(Duration::getSeconds).to(builder::flushIntervalSeconds);
		mapper.from(sender.getMessageSize()).asInt(DataSize::toBytes).to(builder::messageSizeBytes);
		mapper.from(sender.getBatchSize()).to(builder::batchSize);
		return builder.build();
	}

}

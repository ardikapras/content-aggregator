package io.content.scraper.config

import org.springframework.boot.actuate.autoconfigure.tracing.TracingProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

/**
 * Configuration for distributed tracing
 * Excludes actuator endpoints from being traced to reduce noise in Zipkin UI
 */
@Configuration
@ConditionalOnClass(TracingProperties::class)
class TracingConfig {

    @Bean
    fun skipActuatorEndpointsCustomizer(): org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer<io.micrometer.observation.ObservationRegistry> {
        return org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer { registry ->
            registry.observationConfig().observationPredicate { name, context ->
                // Skip observations for actuator endpoints
                if (context is org.springframework.http.server.observation.ServerRequestObservationContext) {
                    val uri = context.carrier.requestURI
                    !uri.startsWith("/actuator")
                } else {
                    true
                }
            }
        }
    }
}

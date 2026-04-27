package com.example.product.infrastructure.message.kafka.producer;

import com.example.product.domain.event.ProductEventProducer;
import com.example.product.domain.event.payload.ProductCreatedEvent;
import com.example.product.domain.event.payload.ProductDeletedEvent;
import com.example.product.domain.event.payload.ProductUpdatedEvent;
import com.ezmeal.common.security.principal.CustomUserPrincipal;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventProducerImpl implements ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishCreatedEvent(ProductCreatedEvent event) {
        // 헤더와 페이로드를 함께 담아서 보내는 sendWithHeaders를 사용
        sendWithHeaders("product-created-topic", event);
    }

    @Override
    public void publishUpdatedEvent(ProductUpdatedEvent event) {
        // 헤더와 페이로드를 함께 담아서 보내는 sendWithHeaders를 사용
        sendWithHeaders("product-updated-topic", event);
    }

    @Override
    public void publishDeletedEvent(ProductDeletedEvent event) {
        // 헤더와 페이로드를 함께 담아서 보내는 sendWithHeaders를 사용
        sendWithHeaders("product-deleted-topic", event);
    }

    // 카프카 이벤트 헤더에 사용자 점보를 담는 메서드
    private void sendWithHeaders(String topic, Object payload) {
        // 1. 토픽과 데이터(Payload)를 담은 레코드 생성
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, payload);

        // 2. 현재 스레드의 인증 정보(SecurityContext)를 가져옴
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 3. 인증 정보가 있는 유저의 요청일 경우에만 카프카 헤더 추가
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            record.headers().add("X-User-Id", principal.getUserId().getBytes(StandardCharsets.UTF_8));
            record.headers().add("X-User-Role", principal.getRole().name().getBytes(StandardCharsets.UTF_8));
        }

        // 4. 카프카로 전송
        kafkaTemplate.send(record);
    }
}

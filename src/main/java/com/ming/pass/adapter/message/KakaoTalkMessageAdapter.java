package com.ming.pass.adapter.message;

import com.ming.pass.config.KakaoTalkMessageConfig;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.*;

@Service
public class KakaoTalkMessageAdapter {
    // 실제로 카톡 API 를 호출해서 알람 전송

    private final WebClient webClient; // WebClient 는 재사용 가능한 thread-safe 객체

    public KakaoTalkMessageAdapter(KakaoTalkMessageConfig config) {
        webClient = WebClient.builder() // WebClient.builder() 는 한 번만 호출하고 앱 전반에서 재사용 하는 게 좋음
                .baseUrl(config.getHost())
                .defaultHeaders(h -> {
                    h.setBearerAuth(config.getToken());
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                }).build();

    }

    // 수신자 uuid 와 메시지 내용을 받아 카톡 API 에 POST 요청을 보내고 전송 성공 여부를 반환하는 함수
    public boolean sendKakaoTalkMessage(final String uuid, final String text) {
        KakaoTalkMessageResponse response = webClient.post().uri("/v1/api/talk/friends/message/default/send")
                .body(BodyInserters.fromValue(new KakaoTalkMessageRequest(uuid, text))) // JSON 으로 자동 변환된 상태로 카톡 서버에 HTTP POST 요청을 보냄
                .retrieve()
                .bodyToMono(KakaoTalkMessageResponse.class) // 카톡 서버에서 받은 응답 바디(JSON)를 자바 객체로 변환함
                .block();

        if (response == null || response.getSuccessfulReceiverUuids() == null) {
            return false;

        }
        return response.getSuccessfulReceiverUuids().size() > 0;

    }
}

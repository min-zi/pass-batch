package com.ming.pass.adapter.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class KakaoTalkMessageResponse {
    // 카카오 서버가 보낸 JSON 응답을 자바 객체로 변환(=역직렬화) 해주는 클래스
    @JsonProperty("successful_receiver_uuids")
    private List<String> successfulReceiverUuids;
}

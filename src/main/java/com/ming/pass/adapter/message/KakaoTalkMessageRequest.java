package com.ming.pass.adapter.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
public class KakaoTalkMessageRequest {
    @JsonProperty("template_object") // 카톡 API 규격에 맞게 필드명을 snake_case 로 맞추기 위해
    private TemplateObject templateObject;

    @JsonProperty("receiver_uuids")
    private List<String> receiverUuids;

    @Getter
    @Setter
    @ToString
    public static class TemplateObject {
        private String objectType;
        private String text;
        private Link link;

        @Getter
        @Setter
        @ToString
        public static class Link {
            @JsonProperty("web_url")
            private String webUrl;

        }
    }

    // JSON 으로 변환되기 전 자바 객체 상태의 HTTP 요청의 body 가 만들어짐
    public KakaoTalkMessageRequest(String uuid, String text) {
        List<String> receiverUuids = Collections.singletonList(uuid);

        TemplateObject.Link link = new TemplateObject.Link();
        TemplateObject templateObject = new TemplateObject();
        templateObject.setObjectType("text");
        templateObject.setText(text);
        templateObject.setLink(link);

        this.receiverUuids = receiverUuids;
        this.templateObject = templateObject;

    }
}

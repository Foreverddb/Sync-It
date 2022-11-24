package com.example.sync_everything.response;

import lombok.Data;

@Data
public class Response {
    Integer code;
    String message;
    Object data;
    Integer len;

    Response (Builder builder) {
        this.code = builder.code.getCode();
        if (builder.message != null) this.message = builder.message;
        else this.message = builder.code.getMessage();
        this.len = builder.len;
        this.data = builder.data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        ResponseCode code;
        String message;
        Object data;
        Integer len;

        public Builder code(ResponseCode code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.message = msg;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder len(Integer len) {
            this.len = len;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}

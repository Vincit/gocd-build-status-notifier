package com.tw.go.plugin.util;


import com.tw.go.plugin.jsonapi.Pipeline;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HttpConnectionUtilTest {

    @Test
    public void shouldParsePipeline() throws Exception {
        String pipelineJson = "{" +
                "\"name\": \"pipeline-name\"," +
                "\"counter\": 10," +
                "\"stages\": [{ \"name\": \"stage-name\"}]" +
                "}";
        ByteArrayInputStream inputStream = stringToInputString(pipelineJson);

        HttpConnectionUtil httpConnectionUtil = new HttpConnectionUtil();
        Pipeline pipeline = httpConnectionUtil.responseToType(inputStream, Pipeline.class);

        assertThat(pipeline.name, is("pipeline-name"));
        assertThat(pipeline.counter, is("10"));
        assertThat(pipeline.stages.length, is(1));
        assertThat(pipeline.stages[0].name, is("stage-name"));
    }

    private ByteArrayInputStream stringToInputString(String pipelineJson) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(pipelineJson.getBytes("UTF-8"));
    }

}
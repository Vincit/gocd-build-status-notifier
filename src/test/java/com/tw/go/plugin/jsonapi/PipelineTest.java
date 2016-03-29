package com.tw.go.plugin.jsonapi;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PipelineTest {

    @Test
    public void testLastStage_OneStage() {
        Pipeline pipeline = new Pipeline();
        pipeline.name = "pipeline";
        pipeline.stages = new Stage[] {
                new Stage("stage-1")
        };

        assertThat(pipeline.getLastStageName(), is("pipeline/stage-1"));
    }

    @Test
    public void testLastStage_MultipleStages() {
        Pipeline pipeline = new Pipeline();
        pipeline.name = "pipeline";
        pipeline.stages = new Stage[] {
            new Stage("stage-1"),
                new Stage("stage-2"),
                new Stage("stage-3")
        };

        assertThat(pipeline.getLastStageName(), is("pipeline/stage-3"));
    }

}
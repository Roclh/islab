package org.Roclh;

import org.Roclh.service.SpaceMarineService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ContextTest extends RunAppTest{

    @Autowired
    private SpaceMarineService spaceMarineService;

    @Test
    public void contextLoads(){
        Assertions.assertNotNull(spaceMarineService);
    }
}

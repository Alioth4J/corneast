package com.alioth4j.corneast_core;

import com.alioth4j.corneast_core.util.ProtobufRequestGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CorneastCoreApplicationTests {

	@Test
	void generateProtobufRequest() {
		ProtobufRequestGenerator.generate();
	}

}

package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.testfixture.beans.TestBean;
import org.springframework.stereotype.Component;

/**
 * Description
 *
 * @author cunzhiwang
 * @date 2025/1/25 17:30
 */

@Component
public class TestData  {

	@Autowired
	private StaticMessageSource staticMessageSource;
	@Autowired
	private TestDataImpl testInterFace;

	public StaticMessageSource getStaticMessageSource() {
		return staticMessageSource;
	}

	public void setStaticMessageSource(StaticMessageSource staticMessageSource) {
		this.staticMessageSource = staticMessageSource;
	}

	public TestDataImpl getTestInterFace() {
		return testInterFace;
	}

	public void setTestInterFace(TestDataImpl testInterFace) {
		this.testInterFace = testInterFace;
	}

	void testPrint() {
		System.out.println("test print");
	}
}

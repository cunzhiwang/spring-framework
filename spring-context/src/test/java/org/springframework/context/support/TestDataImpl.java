package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.testfixture.beans.TestBean;

/**
 * Description
 *
 * @author cunzhiwang
 * @date 2025/1/25 17:41
 */
public class TestDataImpl implements ObjectFactory<TestBean> {


	@Override
	public TestBean getObject() throws BeansException {
		return new TestBean();
	}
}

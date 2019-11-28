package org.springframework.beans.factory.xml;

public class CustomizeNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("customize", new CustomizeBeanDefinitionParse());
	}
}

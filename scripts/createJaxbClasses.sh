#!/bin/sh

# Change to top of the source tree.
cd src

# Run JAXB compiler with schema and bindings.
java -cp ../../tools/lib/jaxb-xjc.jar com.sun.tools.xjc.Driver -b ../script/dummy-bindings.xml ../resources/jar/dummy.xsd

# remove the generated files which will not be used
rm icecube/icebucket/jaxb/Dummy.java
rm icecube/icebucket/jaxb/ObjectFactory.java
rm icecube/icebucket/jaxb/bgm.ser
rm icecube/icebucket/jaxb/jaxb.properties
rm icecube/icebucket/jaxb/package.html
rm icecube/icebucket/jaxb/impl/DummyImpl.java

===========================
Running the client examples
===========================

Use maven to build the module, and additionally copy the dependencies
alongside their output:

  mvn clean package dependency:copy-dependencies -DincludeScope=runtime -DskipTests

Now you can run the examples using commands of the format:

  Linux:   java -cp "target/classes/:target/dependency/*" org.apache.qpid.jms.example.Sender

  Windows: java -cp "target\classes\;target\dependency\*" org.apache.qpid.jms.example.Sender

NOTE: The example expects to use a Queue named "test001". You may need to create
this before running the examples.

NOTE: The example expects to use an URI in this format "amqps://...servicebus.windows.net?jms.username=...&jms.password=..."

NOTE: You can configure the connection and queue details used by updating the
JNDI configuration file before building. It can be found at:
src/main/resources/jndi.properties


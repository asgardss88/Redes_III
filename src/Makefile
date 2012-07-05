CLASSPATH="$(CURDIR)/redesiii/nanoxml-2.2.3.jar:$(CURDIR)/redesiii/mail.jar:"
JFLAGS = -g -cp $(CLASSPATH)
#JC = /home/edward/BARRA_LDC/TESIS/jdk1.7.0_03/bin/javac
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	redesiii/Cliente.java \
	redesiii/Servidor.java \
	redesiii/Interfaz_Cliente_Servidor.java \
	redesiii/Caso.java \
	redesiii/Correo.java \
	redesiii/LectorConfig.java \
	redesiii/maquinaCliente.java \
	redesiii/Prueba.java \
	redesiii/Interfaz_Servidor_Cliente.java \
	redesiii/servidor_web.java \
	redesiii/servidor_web_starter.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) redesiii/*.class

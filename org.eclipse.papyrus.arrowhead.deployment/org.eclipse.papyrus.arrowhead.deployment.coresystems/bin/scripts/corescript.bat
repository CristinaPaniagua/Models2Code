C:
cd C:\Users\usuario\Documents\ltu\2022-support-software-engineer\sysml-plugin-development\workspace\
mkdir arrowhead-core-systems
cd arrowhead-core-systems
git clone https://github.com/CristinaPaniagua/MinimalLocalCloud.git
cd MinimalLocalCloud
CALL mvn clean install -DskipTests
copy serviceregistry\target\arrowhead-serviceregistry-4.4.1.jar ..
copy authorization\target\arrowhead-authorization-4.4.1.jar ..
copy orchestrator\target\arrowhead-orchestrator-4.4.1.jar ..
copy C:\Users\usuario\Documents\ltu\2022-support-software-engineer\sysml-plugin-development\workspace\\org.eclipse.papyrus.arrowhead\org.eclipse.papyrus.arrowhead.deployment\org.eclipse.papyrus.arrowhead.deployment.coresystems\src\scripts\StartMandatoryCoreSystems.bat ..
cd ..
rm MinimalLocalCloud/* MinimalLocalCloud/.*
rmdir MinimalLocalCloud
del "corescript.bat"
del	"init.bat"
PAUSE
C:
cd C:\Users\usuario\Documents\ltu\2022-support-software-engineer\sysml-plugin-development\workspace\
mkdir arrowhead
cd arrowhead
mkdir core-systems
cd core-systems
git clone https://github.com/CristinaPaniagua/MinimalLocalCloud.git
cd MinimalLocalCloud
CALL mvn clean install -DskipTests
copy serviceregistry\target\arrowhead-serviceregistry-4.4.1.jar ..
copy authorization\target\arrowhead-authorization-4.4.1.jar ..
copy orchestrator\target\arrowhead-orchestrator-4.4.1.jar ..
copy C:\Users\usuario\Documents\ltu\2022-support-software-engineer\sysml-plugin-development\workspace\\arrowhead-papyrus-plugin\org.eclipse.papyrus.arrowhead.deployment\coresystems\src\scripts\StartMandatoryCoreSystems.bat ..
cd ..
rmdir MinimalLocalCloud /S /Q
del C:\Users\usuario\Documents\ltu\2022-support-software-engineer\sysml-plugin-development\workspace\\arrowhead-papyrus-plugin\org.eclipse.papyrus.arrowhead.deployment\coresystems\src\scripts\corescript.bat
del	C:\Users\usuario\Documents\ltu\2022-support-software-engineer\sysml-plugin-development\workspace\\arrowhead-papyrus-plugin\org.eclipse.papyrus.arrowhead.deployment\coresystems\src\scripts\init.bat
PAUSE
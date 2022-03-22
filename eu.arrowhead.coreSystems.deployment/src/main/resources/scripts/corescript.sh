cd /Users/cristina.paniagua/Desktop/EclipseWorkSpace/Tests
mkdir Arrowhead_mandatory_systems
mkdir Executables_core_systems
cd Arrowhead_mandatory_systems
git clone https://github.com/CristinaPaniagua/MinimalLocalCloud.git
cd MinimalLocalCloud
mvn clean install -DskipTests
cp serviceregistry/target/arrowhead-serviceregistry-4.4.1.jar /Users/cristina.paniagua/Desktop/EclipseWorkSpace/Tests/Executables_core_systems
cp authorization/target/arrowhead-authorization-4.4.1.jar /Users/cristina.paniagua/Desktop/EclipseWorkSpace/Tests/Executables_core_systems
cp orchestrator/target/arrowhead-orchestrator-4.4.1.jar /Users/cristina.paniagua/Desktop/EclipseWorkSpace/Tests/Executables_core_systems
echo  moving files
cd /Users/cristina.paniagua/Desktop/EclipseWorkSpace/eu.arrowhead.coreSystems.deployment/src/main/resources/scripts/
echo .....
cp StartMandatoryCoreSystems.sh /Users/cristina.paniagua/Desktop/EclipseWorkSpace/Tests/Executables_core_systems
sleep 60s
rm corescript.sh
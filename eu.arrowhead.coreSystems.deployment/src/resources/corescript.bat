D:
cd D:\SysMLPlugins\TestDirectory
mkdir Arrowhead_mandatory_systems
mkdir Executables_core_systems
cd Arrowhead_mandatory_systems
git clone https://github.com/CristinaPaniagua/MinimalLocalCloud.git
cd MinimalLocalCloud
mvn clean install -DskipTests
copy serviceregistry\target\arrowhead-serviceregistry-4.3.0.jar D:\SysMLPlugins\TestDirectory\Executables_core_systems
copy authorization\target\arrowhead-authorization-4.3.0.jar D:\SysMLPlugins\TestDirectory\Executables_core_systems
copy orchestrator\target\arrowhead-orchestrator-4.3.0.jar D:\SysMLPlugins\TestDirectory\Executables_core_systems
copy D:\SysMLPlugins\Scripts\CoreSystems\StartMandatoryCoreSystems.bat D:\SysMLPlugins\TestDirectory\Executables_core_systems
PAUSE
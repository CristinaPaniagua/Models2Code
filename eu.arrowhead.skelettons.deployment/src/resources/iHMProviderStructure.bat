%systemroot%\System32\xcopy  D:\SysMLPlugins\Code\eu.arrowhead.skelettons.deployment\src\resources\provider D:\SysMLPlugins\TestDirectory\test3_ApplicationSystems\iHM_Provider /e
D:
cd D:\SysMLPlugins\TestDirectory\test3_ApplicationSystems\iHM_Provider\src\main\java\eu\arrowhead
mkdir iHM_Provider
mkdir security
%systemroot%\System32\xcopy D:\SysMLPlugins\Code\client-skeleton-java-spring-master\client-skeleton-provider\src\main\java\eu\arrowhead\client\skeleton\provider\security D:\SysMLPlugins\TestDirectory\test3_ApplicationSystems\iHM_Provider\src\main\java\eu\arrowhead\security /e
g:
cd G:\Documents\CM\Code\workspace_pentair\pentair-client
docker build -t 192.168.1.2:6000/pentair-prom-client:latest .

rem NOT NEEDED docker image tag pentair-prom-client:latest 192.168.1.2:6000/pentair-prom-client:latest

rem increaes the rev in the SW to verify at runtime
rem delete JARs in target dir
rem run->maven build for single assembly
rem run this for docker build
rem do the push below manually
rem go to diskstation and "USE" local registry, then download the latest
rem stop the container, action->reset, then start & verify the latest version

rem docker image push 192.168.1.2:6000/pentair-prom-client:latest

pause

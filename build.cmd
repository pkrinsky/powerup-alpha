

mkdir target
xcopy /s /y bin target
xcopy /s /y images target

cd target
jar cvf powerup-game-01.jar ./*
move *.jar ../releases

cd ..
rd /s /q target


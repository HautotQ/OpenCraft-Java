#!/bin/bash

JAVAFX_SDK=/Users/quentin/MavenTest/OpenCraft/src/main/resources/jmods
JAVA_HOME=$(/usr/libexec/java_home)
RUNTIME_IMAGE=custom-runtime

jlink \
  --module-path $JAVAFX_SDK:$JAVA_HOME/jmods \
  --add-modules java.base,javafx.controls,javafx.fxml,javafx.swing \
  --output $RUNTIME_IMAGE \
  --strip-debug \
  --compress=2 \
  --no-header-files \
  --no-man-pages
# dmg, ou app-image pour le field --type
jpackage \
  --input /Users/quentin/MavenTest/OpenCraft/out/artifacts/OpenCraft_jar \
  --name OpenCraft \
  --main-jar OpenCraft.jar \
  --main-class net.tabarcraft.opencraft.Launcher \
  --type dmg \
  --icon /Users/quentin/MavenTest/OpenCraft/src/main/resources/icon.icns \
  --resource-dir /Users/quentin/MavenTest/OpenCraft/src/main/resources \
  --mac-package-name OpenCraft \
  --mac-package-identifier net.tabarcraft.opencraft \
  --mac-sign \
  --runtime-image $RUNTIME_IMAGE \
  --java-options '--enable-preview --enable-native-access=ALL-UNNAMED' \
  --verbose





# define things
PROJ_FULLY_PATH="`pwd`/`dirname $0`/.."

# chdir to PROJ
cd "$PROJ_FULLY_PATH"

git checkout app/build.gradle
git checkout app/gradle.properties
rm `find app/build/outputs | grep "\.apk"`

./bin/generate_keystore_alias.sh
./bin/generate_packagename.sh
./gradlew assembleRelease
./bin/collect_apks.sh

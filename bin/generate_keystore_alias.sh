

# define things
PROJ_FULLY_PATH="`pwd`/`dirname $0`/.."
TIMESTAMP="`date +%Y%m%d%H%M`"
KEYSTORE_FOLDER_NAME="keysorebucket"
KEYSTORE_NAME="dafacloud_production_release_${TIMESTAMP}.keystore"
KEYSTORE_PATH="${PROJ_FULLY_PATH}/${KEYSTORE_FOLDER_NAME}/${KEYSTORE_NAME}"
ALIAS_NAME="key-dfgame-release-${TIMESTAMP}"
KEYPASS="16c3500db1e0a8c5f0faff1beff62423a96959fe"

# chdir to PROJ
cd "$PROJ_FULLY_PATH"

# echo info
echo PROJ_FULLY_PATH: "$PROJ_FULLY_PATH"
echo TIMESTAMP: "$TIMESTAMP"
echo KEYSTORE_PATH: "$KEYSTORE_PATH"
echo KEYSTORE_NAME: "$KEYSTORE_NAME"

# init folder
mkdir -p "$KEYSTORE_FOLDER_NAME"

keytool -genkey -v -keystore "$KEYSTORE_PATH" -storetype PKCS12  -storepass "$KEYPASS" -dname "CN=Q" -alias "$ALIAS_NAME" -keypass "$KEYPASS" -keyalg RSA -keysize 2048 -validity 10000

gsed -i "/.*KEYSTORE_PASSWORD.*/d; /.*KEY_PASSWORD.*/d" app/gradle.properties
echo "KEYSTORE_PASSWORD=${KEYPASS}" >> app/gradle.properties
echo "KEY_PASSWORD=${KEYPASS}" >> app/gradle.properties

gsed -i "s#\(storeFile file(\)[^)]\+\()\)#\1\"../${KEYSTORE_FOLDER_NAME}/${KEYSTORE_NAME}\"\2#" app/build.gradle
gsed -i "s#\(keyAlias '\)[^']\+\('\)#\1${ALIAS_NAME}\2#" app/build.gradle

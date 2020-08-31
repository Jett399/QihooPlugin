

# define things
PROJ_FULLY_PATH="`pwd`/`dirname $0`/.."

# chdir to PROJ
cd "$PROJ_FULLY_PATH"

timestamp=`date +%Y%m%d%H%M`
tfolderpath="apks/${timestamp}"
mkdir -p "$tfolderpath"
rm -rf "$tfolderpath"/*

find app/build/outputs | grep "\.apk" | while read opath; do oname=$(awk -F/ '{print $NF}' <<< "$opath"); id=$(grep -o "\d\+" <<< "$oname"); tname1="caishen${id}_110_${timestamp}.apk"; tname=`cat app/build.gradle | grep "^[ \t]*applicationId" | grep "$id\""| grep -o '[0-9a-f\.]\{20\}.\+' | gsed 's#"#.apk#;s#\.apk.\+#.apk#'`; cp "${opath}" "${tfolderpath}/${tname}";done

find "$tfolderpath"

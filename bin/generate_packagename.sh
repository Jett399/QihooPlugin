

# define things
PROJ_FULLY_PATH="`pwd`/`dirname $0`/.."

# chdir to PROJ
cd "$PROJ_FULLY_PATH"

cat app/build.gradle | grep -o "com.dafa_200423_cloud.caishen[0-9]\{2\}" | while read pname; do id=`echo "$pname" | grep -o "[0-9]\{2\}$"`; newpname=`echo "$pname"$(date) | md5 | gsed "s/^\([0-9a-f]\{5\}\)\([0-9a-f]\+\)\([0-9a-f]\{6\}\)/a\1.\2.\3$id/" | gsed "s/\(\)\.\(\)/a.a/g"`; gsed -i "s/$pname/$newpname/" app/build.gradle ;done

# cat app/build.gradle | grep -o "com.dafa_200423_cloud.caishen[0-9]\{2\}" | while read pname; do id=`echo "$pname" | grep -o "[0-9]\{2\}$"`; newpname=`echo "$pname"$(date) | md5 | gsed "s/^\([0-9a-f]\{3\}\)\([0-9a-f]\+\)\([0-9a-f]\{5\}\)/\1.\2.\3$id/"`; gsed -i "s/$pname/$newpname/" app/build.gradle ;done

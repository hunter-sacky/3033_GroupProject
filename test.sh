
#password=$(ldapsearch -x -b "uid=$1,dc=People" -H ldap://10.0.2.4 -D "uid=1234,dc=People" -w password | grep userPassword) 
check=$(ldapsearch -x -b "uid=1234,dc=People" -H ldap://10.0.2.4 -D "uid=$1,dc=People" -w $2 | grep -c userPassword)
echo $check

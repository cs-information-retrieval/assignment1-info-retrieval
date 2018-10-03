Run count_word.sh in Content folder to get .xlsx file with word frequency. No need to run below command or python script to get csv or xlsx.


To count frequency of words run below command on content folder.

```
cat * | tr -s [:punct:] ' '| tr -s ' ' '\n' | awk '{print tolower($0)}'| sort | uniq -c | awk '{print $1" "$2}'| sort -nr | awk '{print $2" "$1}' | sort
```

```
cat * | tr -s [:punct:] ' '| tr -s ' ' '\n' | awk '{print tolower($0)}'| sort | uniq -c | awk '{print $1" "$2}'| sort -nr
```

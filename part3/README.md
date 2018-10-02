To count frequency of words run below command on content folder.

```
cat * | tr -s [:punct:] ' '| tr -s ' ' '\n' | awk '{print tolower($0)}'| sort | uniq -c | awk '{print $1" "$2}'| sort -nr | awk '{print $2" "$1}' | sort
```

```
cat * | tr -s [:punct:] ' '| tr -s ' ' '\n' | awk '{print tolower($0)}'| sort | uniq -c | awk '{print $1" "$2}'| sort -nr
```

cat * | tr -s [:punct:] ' '| tr -s ' ' '\n' | awk '{print tolower($0)}'| sort | uniq -c | awk '{print $1" "$2}'| sort -nr > raw_input
awk '
    BEGIN {OFS=","; print "Word Frequency", "Word"}
    {print $1, $2}
' raw_input > Output.xlsx
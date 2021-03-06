import convert_csv_to_xlsx

text_filename = "output.txt"
csv_output_filename = "output.csv"
xlsx_output_filename = "output.xlsx"

with open(text_filename, "r") as file:
    data = file.read()

string1 = data.replace(" ", ",")

with open(csv_output_filename, "w") as file:
    file.write("Word,")
    file.write("Number of Occurrences\n")

    # num = number of occurences
    # Write word,num instead of num,word -> makes it easier to graph
    for str in string1.split("\n"):
        split1 = str.split(",")
        if (len(split1) < 2):
            split1.append("")
        file.write(split1[1] + ",")
        file.write(split1[0])
        file.write("\n")

print("Done converting to csv!")
convert_to_xlsx = input("Would you like to convert to .xlsx file? Y/N: ")

if convert_to_xlsx.lower() == "y":
    convert_csv_to_xlsx.convert()
    print("Finished converting to xlsx.")

print("All done!")

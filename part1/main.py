import os
import pandas

file_name = "specification.csv"


def execute():
    """
    Main function that will execute all necessary functions.
    """
    # If file does not exist
    if not os.path.isfile(file_name):
        print("{} does not exist in this current directory.".format(file_name))
    else:
        with open(file_name, "r") as file:
            data = file.read()

        # Split the read data into three parts based on commas
        data = data.split(",")
        seed = data[0]
        max_pages = data[1]
        domain_restriction = data[2]

        print("Seed: {}".format(seed))
        print("Max Pages: {}".format(max_pages))
        print("Domain Restriction: {}".format(domain_restriction))


# Main function. Will execute if this file is run from the command line
if __name__ == "__main__":
    execute()

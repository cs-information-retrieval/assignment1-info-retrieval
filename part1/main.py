import os

file_name = "specification.csv"


def execute():
    """
    Main function that will execute all necessary functions.
    """
    # If file does not exist
    if not os.path.isfile(file_name):
        print("{} does not exist in this current directory.".format(file_name))
    else:
        seed, max_pages, domain_restriction = obtain_user_input(file_name)
        print("Seed: {}".format(seed))
        print("Max Pages: {}".format(max_pages))
        print("Domain Restriction: {}".format(domain_restriction))


def obtain_user_input(filename):
    """
    Obtain the user input from the filename.

    # Arguments
    filename -> Filename to read data from.
    """
    with open(file_name, "r") as file:
            data = file.read()

    # Split the read data into three parts based on commas
    data = data.split(",")
    seed = data[0]
    max_pages = data[1]
    domain_restriction = data[2]

    return seed, max_pages, domain_restriction


# Main function. Will execute if this file is run from the command line
if __name__ == "__main__":
    execute()

# to install this, type into a terminal
# python -m pip install -r requirements.txt

import pandas as pd

csv_filename = "output.csv"
xlsx_filename = "output.xlsx"


def convert():
    # Read in csv
    df = pd.read_csv(csv_filename)

    # Export to csv
    df.to_excel(xlsx_filename, index=False, header=False)

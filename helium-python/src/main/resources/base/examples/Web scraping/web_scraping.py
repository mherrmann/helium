"""
Helium can be used to extract data from websites. After reading the data it can
be further processed using standard libraries. In this example we extract
Helium's advantages listed on our homepage and write them into a csv file.
"""
from helium.api import *
import csv

start_chrome("heliumhq.com")
advantage_names = find_all(S("dt", below="Advantages"))
advantages = find_all(S("dd", below="Advantages"))

assert len(advantage_names) == len(advantages)

with open('helium_advantages.csv', "wb") as adv_csv_file:
    writer = csv.writer(adv_csv_file, delimiter=';')
    writer.writerow(['Advantage Name', 'Advantage Description'])
    for index in range(0, len(advantages)):
        advantage_name = advantage_names[index].web_element.text
        advantage_descr = advantages[index].web_element.text
        writer.writerow([advantage_name, advantage_descr])

kill_browser()
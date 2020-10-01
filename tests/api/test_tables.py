from helium import *
from tests.api import BrowserAT


class TablesTest(BrowserAT):
    def get_page(self):
        return 'test_tables.html'

    def test_s_below_above(self):
        second_table_cells = find_all(
            S("table > tbody > tr > td",
              below=Text("Table no. 2"),
              above=Text("Table no. 3")
              )
        )
        self.assertEqual(len(second_table_cells), 9)
        self.assertListEqual(
            sorted([cell.web_element.text for cell in second_table_cells]),
            ['T2R1C1', 'T2R1C2', 'T2R1C3',
             'T2R2C1', 'T2R2C2', 'T2R2C3',
             'T2R3C1', 'T2R3C2', 'T2R3C3']
        )

    def test_s_read_table_column(self):
        email_cells = find_all(S("table > tbody > tr > td", below="Email"))
        self.assertEqual(len(email_cells), 3)
        self.assertListEqual(
            sorted([cell.web_element.text for cell in email_cells]),
            ['email1@domain.com', 'email2@domain.com', 'email3@domain.com']
        )

    def test_text_below_to_left_of(self):
        self.assertEqual(
            'Abdul', Text(below='Name', to_left_of='email2@domain.com').value
        )

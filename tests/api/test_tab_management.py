from helium import start_chrome, open_new_tab, close_current_tab, switch_to_tab, get_tab_count, get_current_tab_index
from tests.api import BrowserAT
from time import sleep

class TabManagementTest(BrowserAT):
    def get_page(self):
        return 'test_tab_management.html'
    
    def setUp(self):
        super().setUp()
        # 确保开始时只有一个标签页
        while get_tab_count() > 1:
            close_current_tab()
    
    def test_open_new_tab(self):
        initial_count = get_tab_count()
        open_new_tab()
        self.assertEqual(initial_count + 1, get_tab_count())
    
    def test_open_new_tab_with_url(self):
        open_new_tab('https://www.google.com')
        sleep(1)  # 等待页面加载
        self.assertEqual('Google', self.driver.title)
    
    def test_close_current_tab(self):
        open_new_tab()
        initial_count = get_tab_count()
        close_current_tab()
        self.assertEqual(initial_count - 1, get_tab_count())
    
    def test_switch_to_tab(self):
        # 打开两个新标签页
        open_new_tab()
        open_new_tab()
        
        # 切换到第一个标签页
        switch_to_tab(0)
        self.assertEqual(0, get_current_tab_index())
        
        # 切换到第二个标签页
        switch_to_tab(1)
        self.assertEqual(1, get_current_tab_index())
        
        # 切换到第三个标签页
        switch_to_tab(2)
        self.assertEqual(2, get_current_tab_index())
    
    def test_get_tab_count(self):
        self.assertEqual(1, get_tab_count())
        open_new_tab()
        self.assertEqual(2, get_tab_count())
        open_new_tab()
        self.assertEqual(3, get_tab_count())
    
    def test_get_current_tab_index(self):
        self.assertEqual(0, get_current_tab_index())
        open_new_tab()
        self.assertEqual(1, get_current_tab_index())
        switch_to_tab(0)
        self.assertEqual(0, get_current_tab_index()) 
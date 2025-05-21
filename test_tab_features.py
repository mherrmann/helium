from helium import start_chrome, open_new_tab, close_current_tab, switch_to_tab, get_tab_count, get_current_tab_index
import time

def test_tab_management():
    start_chrome()
    
    print("opening new tab...")
    open_new_tab()
    print(f"the number of current tab: {get_tab_count()}")
    
    print("\nopening new tab...")
    open_new_tab('https://www.google.com')
    time.sleep(2) 
    print(f"the number of current tab: {get_tab_count()}")
    
    print("\nchange tab...")
    switch_to_tab(0)
    print(f"current tab index: {get_current_tab_index()}")
    
    print("\nclose current tab...")
    close_current_tab()
    print(f"the number of current tab: {get_tab_count()}")

if __name__ == '__main__':
    test_tab_management() 

from helium import start_chrome, open_new_tab, close_current_tab, switch_to_tab, get_tab_count, get_current_tab_index
import time

def test_tab_management():
    # 启动浏览器
    start_chrome()
    
    # 打开新标签页
    print("打开新标签页...")
    open_new_tab()
    print(f"当前标签页数量: {get_tab_count()}")
    
    # 打开带URL的新标签页
    print("\n打开带URL的新标签页...")
    open_new_tab('https://www.google.com')
    time.sleep(2)  # 等待页面加载
    print(f"当前标签页数量: {get_tab_count()}")
    
    # 切换标签页
    print("\n切换标签页...")
    switch_to_tab(0)
    print(f"当前标签页索引: {get_current_tab_index()}")
    
    # 关闭当前标签页
    print("\n关闭当前标签页...")
    close_current_tab()
    print(f"当前标签页数量: {get_tab_count()}")

if __name__ == '__main__':
    test_tab_management() 
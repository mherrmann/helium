from psutil import NoSuchProcess

import psutil


class KillServiceAtExitAT:
    def test_kill_service_at_exit(self):
        self.start_browser_in_sub_process()
        self.assertEqual([], self.get_new_running_services())

    def start_browser_in_sub_process(self):
        raise NotImplementedError()

    def get_new_running_services(self):
        return [s for s in self.get_running_services()
                if s not in self.running_services_before]

    def setUp(self):
        self.running_services_before = self.get_running_services()
        self.running_browsers_before = self.get_running_browsers()

    def tearDown(self):
        for service in self.get_new_running_services():
            try:
                service.terminate()
            except NoSuchProcess:
                # Process terminated already.
                pass
        for browser in self.get_new_running_browsers():
            try:
                browser.terminate()
            except NoSuchProcess:
                # Process terminated already.
                pass

    def get_new_running_browsers(self):
        return [s for s in self.get_running_browsers()
                if s not in self.running_browsers_before]

    def get_running_services(self):
        return self._get_running_processes(self.get_service_process_names())

    def get_running_browsers(self):
        return self._get_running_processes([self.get_browser_process_name()])

    def _get_running_processes(self, image_names):
        result = []
        for p in psutil.process_iter():
            if p.name in image_names:
                result.append(p)
        return result

    def get_service_process_names(self):
        raise NotImplementedError()

    def get_browser_process_name(self):
        raise NotImplementedError()

    def start_browser(self):
        raise NotImplementedError()

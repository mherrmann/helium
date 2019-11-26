#ifndef UNICODE
#define UNICODE
#endif

#ifndef _UNICODE
#define _UNICODE
#endif

#include <algorithm> 
#include <functional> 
#include <iostream>
#include <stdio.h>
#include <string>
#include <windows.h>

using namespace std;

static inline wstring &ltrim(wstring &s) {
	// Taken from http://stackoverflow.com/questions/216823/whats-the-best-way-
	// to-trim-stdstring
	s.erase(
		s.begin(), find_if(s.begin(), s.end(), not1(ptr_fun<int, int>(isspace)))
	);
	return s;
}

STARTUPINFO createStartupInfo() {
	STARTUPINFO result;
	ZeroMemory(&result, sizeof(result));
	result.cb = sizeof result; // Compulsory.
	result.dwFlags = STARTF_USESHOWWINDOW;
	result.wShowWindow = SW_HIDE;
	return result;
}

wstring CHROMEDRIVER_PATH_OPTION = L"--chromedriver-path=";

wstring getChromeDriverPath(int argc, WCHAR *argv[]) {
	int i;
	for (i=1; i<argc; i++) {
		wstring arg = argv[i];
		if (arg.find(CHROMEDRIVER_PATH_OPTION) != wstring::npos) {
			return arg.substr(CHROMEDRIVER_PATH_OPTION.length());
		}
	}
	return L"";
}

wstring getChromeDriverCommandLine(
	wstring chromeDriverPath, int argc, WCHAR *argv[]
) {
	wstring result = chromeDriverPath;
	int i;
	for (i=1; i<argc; i++) {
		wstring arg = argv[i];
		if (arg.find(CHROMEDRIVER_PATH_OPTION) == wstring::npos) {
			result += L" ";
			result += arg;
		}
	}
	return result;
}

HANDLE getJobObject() {
	HANDLE result = CreateJobObject(NULL, NULL);
	if (result == NULL)
		return NULL;
	JOBOBJECT_EXTENDED_LIMIT_INFORMATION jeli = {0};
	jeli.BasicLimitInformation.LimitFlags =
		JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE |
		JOB_OBJECT_LIMIT_SILENT_BREAKAWAY_OK;
	if (! SetInformationJobObject(
		result, JobObjectExtendedLimitInformation, &jeli, sizeof(jeli)
	))
		return NULL;
	return result;
}

int wmain(int argc, WCHAR *argv[]) {
	wstring chromeDriverPath = getChromeDriverPath(argc, argv);
	if (chromeDriverPath == L"") {
		wcerr << L"Error: Please supply " << CHROMEDRIVER_PATH_OPTION;
		return -1;
	}
	wstring commandLine =
		getChromeDriverCommandLine(chromeDriverPath, argc, argv);

	HANDLE jobObject = getJobObject();
	if (jobObject == NULL) {
		wcerr << L"Could not create job object.";
		return -1;
	}

	STARTUPINFO startupInfo = createStartupInfo();
	PROCESS_INFORMATION processInfo = {0};
	if(CreateProcessW(
		chromeDriverPath.c_str(), (wchar_t*) commandLine.c_str(), NULL, NULL,
		FALSE, CREATE_NEW_CONSOLE, NULL, NULL, &startupInfo, &processInfo
	)) {
		if (! AssignProcessToJobObject(jobObject, processInfo.hProcess)) {
			wcerr << L"Could not assign job object to process: Error "
			      << GetLastError() << L".";
			return -1;
		}
		WaitForSingleObject(processInfo.hProcess, INFINITE);
		CloseHandle(processInfo.hThread);
		CloseHandle(processInfo.hProcess);
		return 0;
	} else {
		wcerr << L"The ChromeDriver at " << chromeDriverPath
			<< L" could not be started.";
		return -1;
	}
}
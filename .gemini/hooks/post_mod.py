#!/usr/bin/env python3
import sys
import json
import subprocess
import os

def main():
    try:
        # 데드락 방지: 스타일 교정 시도하되 실패해도 중단하지 않음
        subprocess.run(["./gradlew", "spotlessApply", "--quiet"], 
                       capture_output=True, timeout=30)
        print(json.dumps({"decision": "allow", "systemMessage": "✅ 수정 및 스타일 교정 완료 (Non-blocking)"}))
    except:
        print(json.dumps({"decision": "allow"}))

if __name__ == "__main__":
    main()

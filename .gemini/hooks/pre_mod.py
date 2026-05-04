#!/usr/bin/env python3
import sys
import json
import os

def main():
    try:
        input_data = sys.stdin.read()
        if not input_data:
            print(json.dumps({"decision": "allow"}))
            return
        tool_input = json.loads(input_data)
        file_path = tool_input.get('file_path', '')
        if 'hooks' in file_path:
            print(json.dumps({"decision": "allow"}))
            return
        if os.environ.get('GEMINI_HOOK_APPROVED') == 'true':
            print(json.dumps({"decision": "allow", "systemMessage": "✅ 에이전트 선승인 완료"}))
            return
        print(json.dumps({
            "decision": "deny", 
            "reason": "❌ 사전 승인이 필요합니다.",
            "systemMessage": "수정 계획을 듣고 '진행해줘'라고 말씀하시면 승인 모드가 됩니다."
        }))
    except Exception as e:
        print(json.dumps({"decision": "allow", "systemMessage": str(e)}))

if __name__ == "__main__":
    main()

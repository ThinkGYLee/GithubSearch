#!/usr/bin/env python3
import sys
import json
import re

def main():
    try:
        # 1. stdin에서 JSON 입력 읽기
        input_data = json.load(sys.stdin)
        tool_input = input_data.get('tool_input', {})
        command = tool_input.get('command', '')

        # 2. 'git commit' 또는 'git push' 명령인지 확인
        is_git_commit = command.startswith('git commit')
        is_git_push = command.startswith('git push')

        if is_git_commit or is_git_push:
            # 2-1. 커밋 메시지 한국어 체크 (커밋 시에만)
            if is_git_commit:
                korean_pattern = re.compile('[가-힣ㄱ-ㅎㅏ-ㅣ]')
                if not korean_pattern.search(command):
                    print(json.dumps({
                        "decision": "deny",
                        "reason": "❌ [Git Policy 위반]: 커밋 메시지에 한국어가 포함되어 있지 않습니다.",
                        "systemMessage": "커밋 메시지에 한국어로 상세한 설명을 포함하여 다시 시도하세요."
                    }))
                    return

            # 2-2. 명시적 승인 여부 확인 (GEMINI.md 지침 강제)
            confirm_msg = f"\n⚠️ [CRITICAL CHECK]: 현재 '{command}' 명령을 수행하려 합니다.\n"
            confirm_msg += "GEMINI.md 규정에 따라 사용자의 명시적인 승인('네', '진행하세요' 등)을 이미 받았습니까?\n"
            confirm_msg += "아직 받지 않았다면 명령을 취소하고 먼저 사용자에게 승인을 요청하십시오."

            print(json.dumps({
                "decision": "allow", # 우선 allow하되 systemMessage로 AI에게 경고
                "systemMessage": confirm_msg
            }))
            return

        # 3. 그 외의 경우 허용
        print(json.dumps({"decision": "allow"}))

    except Exception as e:
        # 훅 자체 에러 시 통과시키되 로그 남김
        sys.stderr.write(f"Error in git_check hook: {str(e)}\n")
        print(json.dumps({"decision": "allow"}))

if __name__ == "__main__":
    main()

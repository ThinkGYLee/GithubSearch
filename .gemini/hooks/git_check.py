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

        # 2. 'git commit' 명령인지 확인
        if command.startswith('git commit'):
            # 한국어 유니코드 범위: 가-힣 (AC00-D7A3), 자음/모음 (3131-318E)
            korean_pattern = re.compile('[가-힣ㄱ-ㅎㅏ-ㅣ]')
            
            # 명령어 전체에서 한국어 포함 여부 확인
            if not korean_pattern.search(command):
                # 한국어가 없으면 거부(deny)
                print(json.dumps({
                    "decision": "deny",
                    "reason": "❌ [Git Policy 위반]: 커밋 메시지에 한국어가 포함되어 있지 않습니다. KeepTrip 프로젝트의 컨벤션에 따라 상세한 한국어 설명을 포함해 주세요.",
                    "systemMessage": "커밋 메시지에 한국어로 상세한 설명을 포함하여 다시 시도하세요."
                }))
                return

        # 3. 그 외의 경우 또는 검증 통과 시 허용(allow)
        print(json.dumps({"decision": "allow"}))

    except Exception as e:
        # 훅 자체 에러 시 통과시키되 로그 남김
        sys.stderr.write(f"Error in git_check hook: {str(e)}\n")
        print(json.dumps({"decision": "allow"}))

if __name__ == "__main__":
    main()

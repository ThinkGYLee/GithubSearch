#!/usr/bin/env python3
import sys
import subprocess
import os

def print_banner(text):
    print("\n" + "=" * 60)
    print(f" {text}")
    print("=" * 60)

def get_staged_files():
    """스테이징된 파일 목록을 가져옵니다."""
    result = subprocess.run(
        ["git", "diff", "--cached", "--name-only"],
        capture_output=True, text=True
    )
    return result.stdout.strip().split("\n")

def check_logic_consistency():
    staged_files = get_staged_files()
    if not staged_files or staged_files == ['']:
        return True

    print_banner("🔍 커밋 전 로직 정밀 검증 (Logic Integrity Check)")
    print(f"변경된 파일: {len(staged_files)}건")
    
    has_logic_file = any(f.endswith(".kt") or f.endswith(".py") for f in staged_files)
    
    if has_logic_file:
        print("\n[!] 경고: 비즈니스 로직이 포함된 소스 코드가 수정되었습니다.")
        print("AI는 다음 질문에 대해 스스로 검증해야 합니다:")
        print("1. 리팩터링 전의 '구제 로직(Recovery)'이나 '예외 처리'가 유실되었는가?")
        print("2. '정밀 필터링' 규칙이 이전과 동일하게 유지되었는가?")
        print("3. 임의로 추가한 로직이 도메인 규칙을 위반하지 않는가?")
        
        # 실제 AI 에이전트가 이 출력을 보고 판단하도록 유도
        print("\n[AI Action Required] 위 항목에 대해 'git show HEAD:path/to/file'과 비교하여")
        print("누락이 없음을 확인했다면 'y'를 입력하여 진행하십시오.")
        
        try:
            with open('/dev/tty', 'r') as f:
                response = f.readline().strip().lower()
                if response != 'y':
                    print("\n🛑 커밋이 중단되었습니다. 로직 누락 여부를 다시 점검하세요.")
                    return False
        except Exception:
            print("\n⚠️ 대화형 확인이 불가능한 환경입니다. 로직 검증을 수동으로 확인하십시오.")
            return True

    return True

if __name__ == "__main__":
    if not check_logic_consistency():
        sys.exit(1)
    sys.exit(0)
